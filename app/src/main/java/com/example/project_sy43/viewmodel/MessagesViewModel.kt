package com.example.project_sy43.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sy43.model.Conversation
import com.example.project_sy43.repository.ConversationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessagesViewModel(
    private val conversationRepository: ConversationRepository ,
    private val auth: FirebaseAuth
) : ViewModel() {

    // Secondary constructor initializing repository and auth with default Firebase instances
    constructor() : this(
        ConversationRepository(FirebaseFirestore.getInstance() , FirebaseAuth.getInstance()) ,
        FirebaseAuth.getInstance()
    )

    // StateFlow holding the list of conversations
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // StateFlow indicating if conversations are loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow holding error messages for the UI
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Cache for user names to avoid repeated fetches
    private val usersNameCache = mutableMapOf<String , String?>()
    // Cache for product images URLs to avoid repeated fetches
    private val productImagesCache = mutableMapOf<String , String?>()

    // Create a new conversation or return existing one for given user and product
    suspend fun createConversation(otherUserId: String , productId: String): String {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        // Step 1: Check if a conversation already exists
        val existingId = conversationRepository.findExistingConversation(otherUserId , productId)
        if (existingId != null) {
            Log.d("MessagesViewModel" , "Conversation déjà existante: $existingId")
            return existingId
        }

        // Step 2: Retrieve product info from Firestore
        val db = FirebaseFirestore.getInstance()
        val document = try {
            db.collection("Post").document(productId).get().await()
        } catch (e: Exception) {
            Log.e("MessagesViewModel" , "Erreur récupération post : $e")
            null
        }

        val photos = document?.get("photos") as? List<String> ?: emptyList()
        val firstPhoto = photos.firstOrNull()
        val currentPrice = document?.get("price") as? Double ?: 0.0

        // Step 3: Create a new conversation object
        val newConversation = Conversation(
            id = "" ,
            participants = listOf(currentUserId , otherUserId) ,
            buyerId = currentUserId ,
            sellerId = otherUserId ,
            productId = productId ,
            currentNegotiatedPrice = currentPrice ,
            lastMessageText = null ,
            lastMessageTimestamp = null ,
            otherUserName = null ,
            productImageUrl = firstPhoto
        )
        val createdId = conversationRepository.createConversation(newConversation)
        return createdId
    }

    // Initialization block to listen for auth state changes and fetch conversations accordingly
    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                if (_conversations.value.isEmpty()) {
                    fetchConversations()
                }
            } else {
                // Clear state and caches when user logs out
                _conversations.value = emptyList()
                _isLoading.value = false
                _error.value = null
                usersNameCache.clear()
                productImagesCache.clear()
            }
        }
        if (auth.currentUser != null) fetchConversations()
    }

    // Fetch conversations for the current user
    fun fetchConversations() {
        if (_isLoading.value) return
        Log.d("MessagesViewModel" , "fetchConversations called")

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val currentUserId = auth.currentUser?.uid

            if (currentUserId == null) {
                _error.value = "User not logged in."
                _isLoading.value = false
                _conversations.value = emptyList()
                Log.w("MessagesViewModel" , "User not logged in. Cannot fetch conversations.")
                return@launch
            }

            val conversationsResult = conversationRepository.getConversationsForCurrentUser()

            conversationsResult.fold(
                onSuccess = { rawConversations ->
                    Log.d(
                        "MessagesViewModel" ,
                        "Successfully fetched ${rawConversations.size} raw conversations."
                    )
                    if (rawConversations.isEmpty()) {
                        _conversations.value = emptyList()
                        _isLoading.value = false // Important to set loading false here as well
                        return@fold
                    }
                    // Enrich conversations with user names and product images
                    val enrichedConversations =
                        enrichConversationsList(rawConversations , currentUserId)

                    // Sort conversations by last message timestamp descending
                    _conversations.value = enrichedConversations.sortedWith(
                        compareByDescending { it.lastMessageTimestamp }
                    )
                    Log.d(
                        "MessagesViewModel" ,
                        "Conversations updated in StateFlow. Total: ${_conversations.value.size}"
                    )
                } ,
                onFailure = { exception ->
                    Log.e("MessagesViewModel" , "Error fetching conversations" , exception)
                    _error.value = "Failed to load conversations: ${exception.message}"
                    _conversations.value = emptyList()
                }
            )
            _isLoading.value = false
        }
    }

    // Enrich conversations by fetching other user names and product images in batch
    private suspend fun enrichConversationsList(
        conversations: List<Conversation> ,
        currentUserId: String
    ): List<Conversation> {
        Log.d("MessagesViewModel" , "Starting enrichment for ${conversations.size} conversations.")
        // Extract unique other user IDs excluding current user
        val otherUserIds =
            conversations.mapNotNull { it.participants.firstOrNull { p -> p != currentUserId } }
                .distinct()
        // Extract unique product IDs that are not blank
        val productIdsToFetch =
            conversations.mapNotNull { if (it.productId.isNotBlank()) it.productId else null }
                .distinct()
        Log.d("MessagesViewModelDebug" , "otherUserId ${otherUserIds} ")
        Log.d("MessagesViewModelDebug" , "productIdsToFetch ${productIdsToFetch} ")

        // Asynchronously fetch user names in batch
        val userNamesDeferred = viewModelScope.async {
            if (otherUserIds.isNotEmpty()) conversationRepository.fetchUserNamesInBatch(otherUserIds) else Result.success(
                emptyMap()
            )
        }
        Log.d("MessagesViewModel" , "User names fetch deferred : ${userNamesDeferred}")
        // Asynchronously fetch product images in batch
        val productImagesDeferred = viewModelScope.async {
            if (productIdsToFetch.isNotEmpty()) conversationRepository.fetchProductImagesInBatch(
                productIdsToFetch
            ) else Result.success(emptyMap())
        }
        Log.d("MessagesViewModel" , "Product images fetch deferred : ${productImagesDeferred}")

        // Await results of both fetches
        val userNamesResult = userNamesDeferred.await()
        val productImagesResult = productImagesDeferred.await()

        // Extract maps or empty maps on failure
        val userNamesMap = userNamesResult.getOrElse {
            Log.e("MessagesViewModel" , "Failed to fetch user names in batch" , it)
            emptyMap()
        }
        val productImagesMap = productImagesResult.getOrElse {
            Log.e("MessagesViewModel" , "Failed to fetch product images in batch" , it)
            emptyMap()
        }

        // Update caches with fetched data
        userNamesMap.forEach { (id , name) -> usersNameCache[id] = name }
        productImagesMap.forEach { (id , url) -> productImagesCache[id] = url }

        // Map each conversation to enriched version with user name and product image
        val enrichedList = conversations.map { conv ->
            val otherUserId = conv.participants.firstOrNull { it != currentUserId }
            val userName = otherUserId?.let { usersNameCache[it] }
            val imageUrl =
                if (conv.productId.isNotBlank()) productImagesCache[conv.productId] else null

            conv.copy(
                otherUserName = userName
                    ?: conv.otherUserName , // Keep old value if new is null
                productImageUrl = imageUrl ?: conv.productImageUrl // Keep old value if new is null
            )
        }
        Log.d(
            "MessagesViewModel" ,
            "Enrichment complete. Returning ${enrichedList.size} conversations."
        )
        return enrichedList
    }

    // Clear caches and refresh conversations
    fun refreshConversations() {
        usersNameCache.clear()
        productImagesCache.clear()
        fetchConversations()
    }
}
