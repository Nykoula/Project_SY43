package com.example.project_sy43.viewmodel


import androidx.compose.animation.core.copy

// In com.example.project_sy43.ui.theme.screens or a viewmodel package
//package com.example.project_sy43.ui.messages // Or your viewmodel package

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


import com.example.project_sy43.viewmodel.MessagesViewModel
import com.example.project_sy43.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth


import androidx.lifecycle.viewModelScope
import com.example.project_sy43.model.Conversation
import com.example.project_sy43.model.Message // Si vous avez besoin de trier par timestamp ou autre
import com.example.project_sy43.repository.ConversationRepository

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

// Assurez-vous d'avoir une manière d'injecter ou d'initialiser vos dépendances
// Pour cet exemple, je vais les initialiser directement, mais l'injection de dépendances (Hilt, Koin) est recommandée.
class MessagesViewModel(
    private val conversationRepository: ConversationRepository, // Injection de dépendance est préférable
    private val auth: FirebaseAuth // Peut aussi être injecté ou obtenu via le repo si nécessaire
) : ViewModel() {

    // Constructeur secondaire pour une initialisation plus simple si vous n'utilisez pas d'injection de dépendances pour le moment
    // Dans une vraie app, privilégiez l'injection de dépendances.
    constructor() : this(
        ConversationRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()),
        FirebaseAuth.getInstance()
    )

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false) // Initialement false, devient true pendant le chargement
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Cache simple pour les noms d'utilisateur et images pour éviter des appels répétés si non dénormalisé
    // Si vos données sont bien dénormalisées dans Conversation, ce cache est moins critique.
    private val usersNameCache = mutableMapOf<String, String?>()
    private val productImagesCache = mutableMapOf<String, String?>()


    init {
        // Observer les changements d'état d'authentification pour charger/vider les conversations
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                if (_conversations.value.isEmpty()) { // Ne recharge que si c'est vide pour éviter rechargement à chaque changement mineur d'auth state
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
        // Ligne ci-dessous pour charger immédiatement si l'utilisateur est déjà connecté au démarrage du ViewModel
        if(auth.currentUser != null) fetchConversations()
    }

    fun fetchConversations() {
        if (_isLoading.value) return // Empêche les appels multiples si déjà en chargement
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

                    // Enrichir les conversations (noms d'utilisateurs, images)
                    // Cette partie est cruciale si vos documents Conversation dans Firestore
                    // ne contiennent PAS déjà otherUserName et productImageUrl.
                    // Si elles les contiennent, vous pouvez simplifier grandement cette section.

                    val enrichedConversations = enrichConversationsList(rawConversations, currentUserId)

                    // Trier par timestamp du dernier message (si disponible et pertinent)
                    _conversations.value = enrichedConversations.sortedWith(
                        compareByDescending { it.lastMessage?.timestamp }
                    )
                    Log.d("MessagesViewModel", "Conversations updated in StateFlow. Total: ${_conversations.value.size}")
                },
                onFailure = { exception ->
                    Log.e("MessagesViewModel", "Error fetching conversations", exception)
                    _error.value = "Failed to load conversations: ${exception.message}"
                    _conversations.value = emptyList() // Vider en cas d'erreur
                }
            )
            _isLoading.value = false
        }
    }

    /**
     * Enriches a list of conversations with other user's name and product image.
     * Uses batching for efficiency if data is not denormalized in Conversation documents.
     */
    private suspend fun enrichConversationsList(
        conversations: List<Conversation>,
        currentUserId: String
    ): List<Conversation> {
        Log.d("MessagesViewModel", "Starting enrichment for ${conversations.size} conversations.")
        // Collect all unique IDs needed for enrichment
        val otherUserIds = conversations.mapNotNull { it.participants.firstOrNull { p -> p != currentUserId } }.distinct()
        val productIdsToFetch = conversations.mapNotNull { if (it.productId.isNotBlank()) it.productId else null }.distinct()

        // Fetch user names and product images in batch
        // Using async/awaitAll to run these fetches concurrently
        val userNamesDeferred = viewModelScope.async {
            if (otherUserIds.isNotEmpty()) conversationRepository.fetchUserNamesInBatch(otherUserIds) else Result.success(emptyMap())
        }
        val productImagesDeferred = viewModelScope.async {
            if (productIdsToFetch.isNotEmpty()) conversationRepository.fetchProductImagesInBatch(productIdsToFetch) else Result.success(emptyMap())
        }

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

        // Populate caches
        userNamesMap.forEach { (id, name) -> usersNameCache[id] = name }
        productImagesMap.forEach { (id, url) -> productImagesCache[id] = url }

        // Map raw conversations to enriched conversations
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

    // Appeler cette fonction si l'utilisateur effectue une action "pull-to-refresh"
    fun refreshConversations() {
        // Vider le cache pour forcer la récupération si nécessaire, ou être plus intelligent
        usersNameCache.clear()
        productImagesCache.clear()
        fetchConversations()
    }
}