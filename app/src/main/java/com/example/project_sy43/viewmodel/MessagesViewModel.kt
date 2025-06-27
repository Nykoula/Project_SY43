package com.example.project_sy43.viewmodel


// In com.example.project_sy43.ui.theme.screens or a viewmodel package
//package com.example.project_sy43.ui.messages // Or your viewmodel package

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


import com.google.firebase.auth.FirebaseAuth


import com.example.project_sy43.model.Conversation
import com.example.project_sy43.repository.ConversationRepository

import kotlinx.coroutines.async


class MessagesViewModel(
    private val conversationRepository: ConversationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    constructor() : this(
        ConversationRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()),
        FirebaseAuth.getInstance()
    )

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val usersNameCache = mutableMapOf<String, String?>()
    private val productImagesCache = mutableMapOf<String, String?>()


    suspend fun createConversation(otherUserId: String, productId: String): String {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        // ✅ Étape 1 : chercher une conversation existante
        val existingId = conversationRepository.findExistingConversation(otherUserId, productId)
        if (existingId != null) {
            Log.d("MessagesViewModel", "Conversation déjà existante: $existingId")
            return existingId
        }

        // Étape 2 : Récupérer les infos produit comme avant
        val db = FirebaseFirestore.getInstance()
        val document = try {
            db.collection("Post").document(productId).get().await()
        } catch (e: Exception) {
            Log.e("MessagesViewModel", "Erreur récupération post : $e")
            null
        }

        val photos = document?.get("photos") as? List<String> ?: emptyList()
        val firstPhoto = photos.firstOrNull()
        val currentPrice = document?.get("price") as? Double ?: 0.0

        // Étape 3 : Créer une nouvelle conversation
        val newConversation = Conversation(
            id = "",
            participants = listOf(currentUserId, otherUserId),
            buyerId = currentUserId,
            sellerId = otherUserId,
            productId = productId,
            currentNegotiatedPrice = currentPrice,
            lastMessageText = null,
            lastMessageTimestamp = null,
            otherUserName = null,
            productImageUrl = firstPhoto
        )
        val createdId = conversationRepository.createConversation(newConversation)
        return createdId
    }




    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                if (_conversations.value.isEmpty()) {
                    fetchConversations()
                }
            } else {
                _conversations.value = emptyList()
                _isLoading.value = false
                _error.value = null
                usersNameCache.clear()
                productImagesCache.clear()
            }
        }
        if(auth.currentUser != null) fetchConversations()
    }

    fun fetchConversations() {
        if (_isLoading.value) return
        Log.d("MessagesViewModel", "fetchConversations called")

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val currentUserId = auth.currentUser?.uid

            if (currentUserId == null) {
                _error.value = "User not logged in."
                _isLoading.value = false
                _conversations.value = emptyList()
                Log.w("MessagesViewModel", "User not logged in. Cannot fetch conversations.")
                return@launch
            }

            val conversationsResult = conversationRepository.getConversationsForCurrentUser()

            conversationsResult.fold(
                onSuccess = { rawConversations ->
                    Log.d("MessagesViewModel", "Successfully fetched ${rawConversations.size} raw conversations.")
                    if (rawConversations.isEmpty()) {
                        _conversations.value = emptyList()
                        _isLoading.value = false // Important de le mettre ici aussi
                        return@fold
                    }
                    val enrichedConversations = enrichConversationsList(rawConversations, currentUserId)

                    _conversations.value = enrichedConversations.sortedWith(
                        compareByDescending { it.lastMessageTimestamp }
                    )
                    Log.d("MessagesViewModel", "Conversations updated in StateFlow. Total: ${_conversations.value.size}")
                },
                onFailure = { exception ->
                    Log.e("MessagesViewModel", "Error fetching conversations", exception)
                    _error.value = "Failed to load conversations: ${exception.message}"
                    _conversations.value = emptyList()
                }
            )
            _isLoading.value = false
        }
    }

    private suspend fun enrichConversationsList(
        conversations: List<Conversation>,
        currentUserId: String
    ): List<Conversation> {
        Log.d("MessagesViewModel", "Starting enrichment for ${conversations.size} conversations.")
        // Collect all unique IDs needed for enrichment
        val otherUserIds = conversations.mapNotNull { it.participants.firstOrNull { p -> p != currentUserId } }.distinct()
        val productIdsToFetch = conversations.mapNotNull { if (it.productId.isNotBlank()) it.productId else null }.distinct()
        Log.d("MessagesViewModelDebug", "otherUserId ${otherUserIds} ")
        Log.d("MessagesViewModelDebug", "productIdsToFetch ${productIdsToFetch} ")

        val userNamesDeferred = viewModelScope.async {
            if (otherUserIds.isNotEmpty()) conversationRepository.fetchUserNamesInBatch(otherUserIds) else Result.success(emptyMap())
        }
        Log.d("MessagesViewModel", "User names fetch deferred : ${userNamesDeferred}")
        val productImagesDeferred = viewModelScope.async {
            if (productIdsToFetch.isNotEmpty()) conversationRepository.fetchProductImagesInBatch(productIdsToFetch) else Result.success(emptyMap())
        }
        Log.d("MessagesViewModel", "Product images fetch deferred : ${productImagesDeferred}")

        val userNamesResult = userNamesDeferred.await()
        val productImagesResult = productImagesDeferred.await()

        val userNamesMap = userNamesResult.getOrElse {
            Log.e("MessagesViewModel", "Failed to fetch user names in batch", it)
            emptyMap()
        }
        val productImagesMap = productImagesResult.getOrElse {
            Log.e("MessagesViewModel", "Failed to fetch product images in batch", it)
            emptyMap()
        }

        userNamesMap.forEach { (id, name) -> usersNameCache[id] = name }
        productImagesMap.forEach { (id, url) -> productImagesCache[id] = url }

        val enrichedList = conversations.map { conv ->
            val otherUserId = conv.participants.firstOrNull { it != currentUserId }
            val userName = otherUserId?.let { usersNameCache[it] }
            val imageUrl = if (conv.productId.isNotBlank()) productImagesCache[conv.productId] else null

            conv.copy(
                otherUserName = userName ?: conv.otherUserName, // Garde l'ancien si le nouveau est null (ou "Unknown User")
                productImageUrl = imageUrl ?: conv.productImageUrl // Garde l'ancien
            )
        }
        Log.d("MessagesViewModel", "Enrichment complete. Returning ${enrichedList.size} conversations.")
        return enrichedList
    }

    fun refreshConversations() {
        usersNameCache.clear()
        productImagesCache.clear()
        fetchConversations()
    }
}