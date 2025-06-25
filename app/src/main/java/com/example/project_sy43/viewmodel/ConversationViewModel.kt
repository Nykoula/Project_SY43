package com.example.project_sy43.viewmodel

//import _otherParticipantDisplayName

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sy43.model.Conversation // Your Conversation model
import com.example.project_sy43.model.Message    // Your Message model
import com.example.project_sy43.repository.ConversationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.io.path.exists
import kotlin.text.get
import kotlinx.coroutines.channels.awaitClose

class ConversationViewModel : ViewModel() {

    // Dependencies will be initialized (ensure this happens only once)
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var auth: FirebaseAuth
    private var currentConversationId: String? = null
    private var messagesListenerJob: Job? = null // To manage the messages flow collection

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

    private val _error = MutableStateFlow<String?>(null) // General error for the screen
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentMessageText = MutableStateFlow("")
    val currentMessageText: StateFlow<String> = _currentMessageText.asStateFlow()

    // Example for offer input, if you have separate UI elements
    private val _currentOfferPrice = MutableStateFlow("")
    val currentOfferPrice: StateFlow<String> = _currentOfferPrice.asStateFlow()

    private val _currentOfferOptionalText = MutableStateFlow("")
    val currentOfferOptionalText: StateFlow<String> = _currentOfferOptionalText.asStateFlow()


    // --- Initialization ---
    fun initialize(conversationId: String) {
        if (this.currentConversationId == conversationId && _conversationDetails.value != null) {
            Log.d("ConvVM" , "ViewModel already initialized for conversation: $conversationId")
            // Optionally, re-fetch messages if needed or if the listener might have been detached
            // For now, assume if details are loaded, messages listener is also active or will be reactivated.
            if (messagesListenerJob == null || messagesListenerJob?.isActive == false) {
                fetchMessagesForCurrentConversation()
            }
            return
        }
        this.currentConversationId = conversationId
        _error.value = null // Clear previous errors
        Log.d("ConvVM" , "Initializing for conversation: $conversationId")

        // Initialize dependencies if not already done
        if (!this::auth.isInitialized) {
            auth = FirebaseAuth.getInstance()
        }
        if (!this::conversationRepository.isInitialized) {
            val firestore = FirebaseFirestore.getInstance() // Or get from a shared instance
            conversationRepository = ConversationRepository(firestore , auth)
        }

        if (conversationId.isNotBlank()) {
            fetchConversationDetails()
            fetchMessagesForCurrentConversation() // This will now set up the listener
        } else {
            _error.value = "Conversation ID is missing."
            Log.e("ConvVM" , "Conversation ID is blank in initialize.")
        }
    }


    private val _otherParticipantDisplayName = MutableStateFlow<String?>("Chat Partner") // Default
    val otherParticipantDisplayName: StateFlow<String?> = _otherParticipantDisplayName.asStateFlow()

    // We'll call this function when conversationDetails are loaded or updated.
    private fun updateOtherParticipantDisplayName() {
        val details = _conversationDetails.value
        val currentUid = auth.currentUser?.uid


        if (details == null || currentUid == null) {
            _otherParticipantDisplayName.value = "Chat Partner"
            return
        }

        val otherParticipantId = details.participants.find { it != currentUid }

        if (otherParticipantId != null) {
            viewModelScope.launch {
                _otherParticipantDisplayName.value = "Loading..." // Temporary state
                try {
                    val userDocument = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(otherParticipantId)
                        .get()
                        .await() // Requires kotlinx-coroutines-play-services

                    if (userDocument.exists()) {
                        val firstName = userDocument.getString("firstName")
                        val lastName = userDocument.getString("lastName")
                        val fullName = when {
                            firstName != null && lastName != null -> "$firstName $lastName"
                            //firstName != null -> firstName
                            //lastName != null -> lastName
                            else -> null
                        }
                        _otherParticipantDisplayName.value =
                            fullName ?: "User: ${otherParticipantId.take(6)}..."
                    } else {
                        _otherParticipantDisplayName.value =
                            "User: ${otherParticipantId.take(6)}..."
                        Log.w("ConvVM" , "User document not found for ID: $otherParticipantId")
                    }
                } catch (e: Exception) {
                    _otherParticipantDisplayName.value = "User (Error)"
                    Log.e("ConvVM" , "Error fetching user display name for $otherParticipantId" , e)
                }
            }
        } else {
            _otherParticipantDisplayName.value = "Unknown Participant"
            Log.w(
                "ConvVM" ,
                "Could not determine other participant. CurrentUID: $currentUid, Participants: ${details.participants}"
            )
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
                    // You might want to derive other participant's name/ID here
                    // based on details.buyerId, details.sellerId and currentUserId
                    Log.d("ConvVM" , "Details fetched: $details")
                }
                .onFailure { e ->
                    _error.value = "Failed to load conversation details: ${e.message}"
                    Log.e("ConvVM" , "Error fetching conversation details" , e)
                }
            _isLoadingDetails.value = false
        }
    }

    private fun fetchMessagesForCurrentConversation() {
        val convId = currentConversationId ?: return
        messagesListenerJob?.cancel() // Cancel any previous listener job

        Log.d("ConvVM" , "Setting up messages listener for $convId")
        messagesListenerJob = viewModelScope.launch {
            conversationRepository.getMessagesForConversation(convId)
                .onStart {
                    _isLoadingMessages.value = true
                    // _messages.value = emptyList() // Optionally clear messages on new fetch start
                    Log.d("ConvVM" , "Message fetching flow started for $convId...")
                }
                .catch { e -> // Catch errors from the flow itself (e.g., repository issues before Result)
                    Log.e("ConvVM" , "Error in messages flow for $convId" , e)
                    _error.value = "Connection error fetching messages: ${e.message}"
                    _isLoadingMessages.value = false
                }
                .collect { result -> // Collect the Result<List<Message>>
                    result.fold(
                        onSuccess = { messageList ->
                            _messages.value = messageList
                            _isLoadingMessages.value =
                                false // Assuming success means loading is done for this batch
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

    fun sendTextMessage() {
        val convId = currentConversationId ?: run {
            _error.value = "Cannot send message: Conversation ID not set."
            Log.w("ConvVM" , "sendTextMessage: currentConversationId is null.")
            return
        }
        val userId = auth.currentUser?.uid ?: run {
            _error.value = "Cannot send message: User not authenticated."
            Log.w(
                "ConvVM" ,
                "sendTextMessage: User not authenticated (currentUser or UID is null)."
            )
            return
        }
        val textToSend = _currentMessageText.value.trim()
        if (textToSend.isBlank()) {
            _error.value = "Message cannot be empty." // Or show a temporary snackbar/toast
            Log.w("ConvVM" , "sendTextMessage: Attempted to send blank message.")
            return
        }

        _isSendingMessage.value = true
        _error.value = null // Clear previous errors related to sending

        viewModelScope.launch {
            Log.d(
                "ConvVM" ,
                "Attempting to send text message: '$textToSend' to conversation $convId by user $userId"
            )
            val result = conversationRepository.sendTextMessage(
                conversationId = convId ,
                text = textToSend ,
                senderId = userId
                // If you denormalize senderName in messages, get it from auth.currentUser.displayName or profile
            )

            result.fold(
                onSuccess = {
                    _currentMessageText.value = "" // Clear the input field
                    Log.d("ConvVM" , "Text message sent successfully to $convId.")
                    // Messages list will update automatically due to the Flow from getMessagesForConversation
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
            Log.w(
                "ConvVM" ,
                "sendOfferMessage: User not authenticated (currentUser or UID is null)."
            )
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
        if (proposedPrice == null || proposedPrice < 0) { // Or proposedPrice <= 0 if 0 is not allowed
            _error.value = "Invalid offer price entered."
            Log.w("ConvVM" , "sendOfferMessage: Invalid offer price '$priceString'.")
            return
        }

        _isSendingMessage.value = true // Reuse the same loading state, or create a specific one
        _error.value = null

        viewModelScope.launch {
            Log.d(
                "ConvVM" ,
                "Attempting to send offer. Price: $proposedPrice, Text: '$optionalText' to $convId by $userId"
            )
            val result = conversationRepository.sendOfferMessage(
                conversationId = convId ,
                proposedPrice = proposedPrice ,
                optionalText = optionalText.ifBlank { null } , // Send null if optionalText is blank
                senderId = userId
            )

            result.fold(
                onSuccess = {
                    _currentOfferPrice.value = "" // Clear offer price input
                    _currentOfferOptionalText.value = "" // Clear optional text input
                    // _currentMessageText.value = "" // Also clear general message input if offer UI is part of it
                    Log.d("ConvVM" , "Offer message sent successfully to $convId.")
                    // Messages list will update automatically
                } ,
                onFailure = { e ->
                    _error.value = "Failed to send offer: ${e.message}"
                    Log.e("ConvVM" , "Error sending offer message to $convId" , e)
                }
            )
            _isSendingMessage.value = false
        }
    }

    /**
     * Called when the ViewModel is about to be destroyed.
     * Perform any cleanup here, such as cancelling ongoing coroutines.
     */
    override fun onCleared() {
        super.onCleared()
        Log.d("ConvVM" , "ViewModel cleared for conversation: $currentConversationId")
        // Cancel the messages listener job if it's active
        messagesListenerJob?.cancel()
        messagesListenerJob = null // Help garbage collection
        Log.d("ConvVM" , "Messages listener job cancelled.")
    }

    // --- Optional Helper Functions ---
    // You could add functions here to, for example, determine the
    // display name of the other participant based on conversationDetails and currentUserId.

    fun getOtherParticipantDisplayName(): String? {
        val details = _conversationDetails.value ?: return null
        val currentUid = auth.currentUser?.uid ?: return null

        return if (details.buyerId == currentUid) {
            // Fetch seller's display name (you'd need a way to get user profiles)
            details.sellerId // Placeholder, replace with actual name lookup
        } else if (details.sellerId == currentUid) {
            // Fetch buyer's display name
            details.buyerId // Placeholder, replace with actual name lookup
        } else {
            null // Or "Unknown Participant"
        }
    }
}


