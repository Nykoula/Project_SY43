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

import com.example.project_sy43.model.Conversation
import com.example.project_sy43.model.Message
import com.example.project_sy43.model.Product



class MessagesViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth = Firebase.auth

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _usersCache = mutableMapOf<String, String>() // Cache for user names (userId to userName)

    init {
        fetchConversations()
    }

    fun fetchConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val currentUserId = auth.currentUser?.uid
            if (currentUserId == null) {
                _error.value = "User not logged in."
                _isLoading.value = false
                _conversations.value = emptyList()
                return@launch
            }

            try {
                val snapshot = db.collection("conversations")
                    .whereArrayContains("participants", currentUserId)
                    // Consider ordering by mostRecentMessage's timestamp if you can/want to
                    // This might require adding a top-level lastMessageTimestamp to the Conversation doc
                    .get()
                    .await()

                val fetchedConversations = snapshot.documents.mapNotNull { doc ->
                    val conversation = doc.toObject(Conversation::class.java)?.copy(id = doc.id)
                    conversation?.let { conv ->
                        // Fetch other user's name
                        val otherUserId = conv.participants.firstOrNull { it != currentUserId }
                        if (otherUserId != null) {
                            conv.otherUserName = getUserName(otherUserId)
                        }

                        // Fetch product's first image
                        if (conv.productId.isNotBlank()) {
                            try {
                                val productDoc = db.collection("items").document(conv.productId).get().await()
                                val product = productDoc.toObject(Product::class.java)
                                conv.productImageUrl = product?.photos?.firstOrNull()
                            } catch (e: Exception) {
                                Log.e("MessagesViewModel", "Error fetching product ${conv.productId}", e)
                            }
                        }

                    }
                    conversation
                }
                // Sort by last message timestamp, descending
                _conversations.value = fetchedConversations.sortedWith(
                    compareByDescending { it.lastMessage?.timestamp }
                )

            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error fetching conversations", e)
                _error.value = "Failed to load conversations: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getUserName(userId: String): String? {
        if (_usersCache.containsKey(userId)) {
            return _usersCache[userId]
        }
        return try {
            val userDoc = db.collection("users").document(userId).get().await()
            val userName = userDoc.getString("username") // Assuming you have a 'username' field
            userName?.let { _usersCache[userId] = it }
            userName
        } catch (e: Exception) {
            Log.e("MessagesViewModel", "Error fetching user $userId", e)
            null
        }
    }
}