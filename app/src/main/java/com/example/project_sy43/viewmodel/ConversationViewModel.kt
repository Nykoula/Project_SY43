package com.example.project_sy43.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sy43.model.Conversation
import com.example.project_sy43.model.Message
import com.example.project_sy43.repository.ConversationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ConversationViewModel : ViewModel() {

    // Dependencies
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var auth: FirebaseAuth
    private var currentConversationId: String? = null
    private var messagesListenerJob: Job? = null

    // --- StateFlows for UI State ---
    private val _conversationDetails = MutableStateFlow<Conversation?>(null)
    val conversationDetails: StateFlow<Conversation?> = _conversationDetails.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoadingDetails = MutableStateFlow(false)
    val isLoadingDetails: StateFlow<Boolean> = _isLoadingDetails.asStateFlow()

    private val _isLoadingMessages = MutableStateFlow(false)
    val isLoadingMessages: StateFlow<Boolean> = _isLoadingMessages.asStateFlow()

    private val _isSendingMessage = MutableStateFlow(false)
    val isSendingMessage: StateFlow<Boolean> = _isSendingMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentMessageText = MutableStateFlow("")
    val currentMessageText: StateFlow<String> = _currentMessageText.asStateFlow()

    private val _currentOfferPrice = MutableStateFlow("")
    val currentOfferPrice: StateFlow<String> = _currentOfferPrice.asStateFlow()

    private val _currentOfferOptionalText = MutableStateFlow("")
    val currentOfferOptionalText: StateFlow<String> = _currentOfferOptionalText.asStateFlow()

    // NOUVEAU : Contrôle de l'affichage de la section d'offre
    private val _showOfferSection = MutableStateFlow(false)
    val showOfferSection: StateFlow<Boolean> = _showOfferSection.asStateFlow()

    // AMÉLIORATION : StateFlow pour le nom de l'autre participant
    private val _otherParticipantDisplayName = MutableStateFlow<String?>("Chargement...")
    val otherParticipantDisplayName: StateFlow<String?> = _otherParticipantDisplayName.asStateFlow()

    // AMÉLIORATION : StateFlow pour l'URL de l'image du produit
    private val _productImageUrl = MutableStateFlow<String?>(null)
    val productImageUrl: StateFlow<String?> = _productImageUrl.asStateFlow()

    // NOUVEAU : StateFlow pour les offres acceptées
    private val _acceptedOffers = MutableStateFlow<Set<String>>(emptySet())
    val acceptedOffers: StateFlow<Set<String>> = _acceptedOffers.asStateFlow()

    // --- Initialization ---
    fun initialize(conversationId: String) {
        if (this.currentConversationId == conversationId && _conversationDetails.value != null) {
            Log.d("ConvVM" , "ViewModel already initialized for conversation: $conversationId")
            if (messagesListenerJob == null || messagesListenerJob?.isActive == false) {
                fetchMessagesForCurrentConversation()
            }
            return
        }

        this.currentConversationId = conversationId
        _error.value = null
        Log.d("ConvVM" , "Initializing for conversation: $conversationId")

        // Initialize dependencies if not already done
        if (!this::auth.isInitialized) {
            auth = FirebaseAuth.getInstance()
        }
        if (!this::conversationRepository.isInitialized) {
            val firestore = FirebaseFirestore.getInstance()
            conversationRepository = ConversationRepository(firestore , auth)
        }

        if (conversationId.isNotBlank()) {
            fetchConversationDetails()
            fetchMessagesForCurrentConversation()
            loadAcceptedOffers() // Nouvelle ligne ajoutée
        } else {
            _error.value = "Conversation ID is missing."
            Log.e("ConvVM" , "Conversation ID is blank in initialize.")
        }
    }

    // --- Data Fetching Functions ---
    private fun fetchConversationDetails() {
        val convId = currentConversationId ?: return
        viewModelScope.launch {
            _isLoadingDetails.value = true
            Log.d("ConvVM" , "Fetching details for $convId")

            conversationRepository.getConversationDetails(convId)
                .onSuccess { details ->
                    _conversationDetails.value = details
                    Log.d("ConvVM" , "Details fetched: $details")

                    updateOtherParticipantInfo(details)
                }
                .onFailure { e ->
                    _error.value = "Failed to load conversation details: ${e.message}"
                    Log.e("ConvVM" , "Error fetching conversation details" , e)
                }
            _isLoadingDetails.value = false
        }
    }

    private fun updateOtherParticipantInfo(details: Conversation?) {
        val currentUid = auth.currentUser?.uid

        if (details == null || currentUid == null) {
            _otherParticipantDisplayName.value = "Utilisateur inconnu"
            return
        }

        val otherParticipantId = details.participants.find { it != currentUid }

        if (otherParticipantId != null) {
            viewModelScope.launch {
                try {
                    conversationRepository.getUserName(otherParticipantId)
                        .onSuccess { userName ->
                            _otherParticipantDisplayName.value = userName
                            Log.d("ConvVM" , "Other participant name updated: $userName")
                        }
                        .onFailure { e ->
                            _otherParticipantDisplayName.value = "Utilisateur inconnu"
                            Log.e("ConvVM" , "Error fetching user name for $otherParticipantId" , e)
                        }

                    if (!details.productId.isNullOrBlank()) {
                        val productImagesResult =
                            conversationRepository.fetchProductImagesInBatch(listOf(details.productId))
                        productImagesResult.onSuccess { imagesMap ->
                            _productImageUrl.value = imagesMap[details.productId]
                            Log.d("ConvVM" , "Product image URL updated: ${_productImageUrl.value}")
                        }
                    }
                } catch (e: Exception) {
                    _otherParticipantDisplayName.value = "Utilisateur inconnu"
                    Log.e("ConvVM" , "Error updating participant info" , e)
                }
            }
        } else {
            _otherParticipantDisplayName.value = "Utilisateur inconnu"
            Log.w(
                "ConvVM" ,
                "Could not determine other participant. CurrentUID: $currentUid, Participants: ${details.participants}"
            )
        }
    }

    private fun fetchMessagesForCurrentConversation() {
        val convId = currentConversationId ?: return
        messagesListenerJob?.cancel()

        Log.d("ConvVM" , "Setting up messages listener for $convId")
        messagesListenerJob = viewModelScope.launch {
            conversationRepository.getMessagesForConversation(convId)
                .onStart {
                    _isLoadingMessages.value = true
                    Log.d("ConvVM" , "Message fetching flow started for $convId...")
                }
                .catch { e ->
                    Log.e("ConvVM" , "Error in messages flow for $convId" , e)
                    _error.value = "Connection error fetching messages: ${e.message}"
                    _isLoadingMessages.value = false
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { messageList ->
                            _messages.value = messageList
                            _isLoadingMessages.value = false
                            Log.d(
                                "ConvVM" ,
                                "Messages updated for $convId: ${messageList.size} messages"
                            )
                        } ,
                        onFailure = { e ->
                            _error.value = "Error processing messages for $convId: ${e.message}"
                            _isLoadingMessages.value = false
                            Log.e("ConvVM" , "Failure collecting messages result for $convId" , e)
                        }
                    )
                }
        }
    }

    // --- UI Event Handlers / Actions ---
    fun onCurrentMessageTextChanged(newText: String) {
        _currentMessageText.value = newText
    }

    fun onOfferPriceChanged(newPrice: String) {
        _currentOfferPrice.value = newPrice
    }

    fun onOfferOptionalTextChanged(newText: String) {
        _currentOfferOptionalText.value = newText
    }

    fun toggleOfferSection() {
        _showOfferSection.value = !_showOfferSection.value
        if (!_showOfferSection.value) {
            // Nettoyer les champs quand on ferme la section
            _currentOfferPrice.value = ""
            _currentOfferOptionalText.value = ""
        }
    }

    fun sendTextMessage() {
        val convId = currentConversationId ?: run {
            _error.value = "Cannot send message: Conversation ID not set."
            Log.w("ConvVM" , "sendTextMessage: currentConversationId is null.")
            return
        }
        val userId = auth.currentUser?.uid ?: run {
            _error.value = "Cannot send message: User not authenticated."
            Log.w("ConvVM" , "sendTextMessage: User not authenticated.")
            return
        }
        val textToSend = _currentMessageText.value.trim()
        if (textToSend.isBlank()) {
            _error.value = "Message cannot be empty."
            Log.w("ConvVM" , "sendTextMessage: Attempted to send blank message.")
            return
        }

        _isSendingMessage.value = true
        _error.value = null

        viewModelScope.launch {
            Log.d(
                "ConvVM" ,
                "Attempting to send text message: '$textToSend' to conversation $convId"
            )
            val result = conversationRepository.sendTextMessage(
                conversationId = convId ,
                text = textToSend ,
                senderId = userId
            )

            result.fold(
                onSuccess = {
                    _currentMessageText.value = ""
                    Log.d("ConvVM" , "Text message sent successfully to $convId.")
                } ,
                onFailure = { e ->
                    _error.value = "Failed to send message: ${e.message}"
                    Log.e("ConvVM" , "Error sending text message to $convId" , e)
                }
            )
            _isSendingMessage.value = false
        }
    }

    fun sendOfferMessage() {
        val convId = currentConversationId ?: run {
            _error.value = "Cannot send offer: Conversation ID not set."
            Log.w("ConvVM" , "sendOfferMessage: currentConversationId is null.")
            return
        }
        val userId = auth.currentUser?.uid ?: run {
            _error.value = "Cannot send offer: User not authenticated."
            Log.w("ConvVM" , "sendOfferMessage: User not authenticated.")
            return
        }

        val priceString = _currentOfferPrice.value.trim()
        val optionalText = _currentOfferOptionalText.value.trim()

        if (priceString.isBlank()) {
            _error.value = "Offer price cannot be empty."
            Log.w("ConvVM" , "sendOfferMessage: Offer price is blank.")
            return
        }

        val proposedPrice = priceString.toDoubleOrNull()
        if (proposedPrice == null || proposedPrice < 0) {
            _error.value = "Invalid offer price entered."
            Log.w("ConvVM" , "sendOfferMessage: Invalid offer price '$priceString'.")
            return
        }

        _isSendingMessage.value = true
        _error.value = null

        viewModelScope.launch {
            Log.d(
                "ConvVM" ,
                "Attempting to send offer. Price: $proposedPrice, Text: '$optionalText'"
            )
            val result = conversationRepository.sendOfferMessage(
                conversationId = convId ,
                proposedPrice = proposedPrice ,
                optionalText = optionalText.ifBlank { null } ,
                senderId = userId ,
                productImageUrl = _productImageUrl.value // Inclure l'image du produit
            )

            result.fold(
                onSuccess = {
                    _currentOfferPrice.value = ""
                    _currentOfferOptionalText.value = ""
                    _showOfferSection.value = false
                    Log.d("ConvVM" , "Offer message sent successfully to $convId.")
                } ,
                onFailure = { e ->
                    _error.value = "Failed to send offer: ${e.message}"
                    Log.e("ConvVM" , "Error sending offer message to $convId" , e)
                }
            )
            _isSendingMessage.value = false
        }
    }

    fun acceptOffer(messageId: String , proposedPrice: Double) {
        val convId = currentConversationId ?: return
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _isSendingMessage.value = true

            val result = conversationRepository.acceptOffer(
                conversationId = convId ,
                messageId = messageId ,
                acceptingUserId = userId
            )

            result.fold(
                onSuccess = {
                    // Ajouter l'offre aux offres acceptées
                    _acceptedOffers.value = _acceptedOffers.value + messageId

                    // Envoyer un message de confirmation
                    conversationRepository.sendTextMessage(
                        conversationId = convId ,
                        text = "Votre proposition a été acceptée" ,
                        senderId = userId
                    )

                    Log.d("ConvVM" , "Offer accepted successfully")
                } ,
                onFailure = { e ->
                    _error.value = "Failed to accept offer: ${e.message}"
                    Log.e("ConvVM" , "Error accepting offer" , e)
                }
            )
            _isSendingMessage.value = false
        }
    }

    fun isCurrentUserBuyer(): Boolean {
        val currentUserId = auth.currentUser?.uid
        return _conversationDetails.value?.buyerId == currentUserId
    }


    fun isOfferAccepted(messageId: String): Boolean {
        return _acceptedOffers.value.contains(messageId)
    }

    private fun loadAcceptedOffers() {
        val convId = currentConversationId ?: return

        viewModelScope.launch {
            conversationRepository.getAcceptedOffers(convId)
                .onSuccess { acceptedOfferIds ->
                    _acceptedOffers.value = acceptedOfferIds.toSet()
                    Log.d("ConvVM" , "Loaded ${acceptedOfferIds.size} accepted offers")
                }
                .onFailure { e ->
                    Log.e("ConvVM" , "Error loading accepted offers" , e)
                }
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("ConvVM" , "ViewModel cleared for conversation: $currentConversationId")
        messagesListenerJob?.cancel()
        messagesListenerJob = null
        Log.d("ConvVM" , "Messages listener job cancelled.")
    }


}