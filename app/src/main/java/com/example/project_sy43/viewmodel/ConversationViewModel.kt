package com.example.project_sy43.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sy43.model.Message
import com.example.project_sy43.repository.ConversationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val conversationRepository: ConversationRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Récupérer l'ID de la conversation passé en argument de navigation
    val conversationId: String = savedStateHandle.get<String>("conversationId")
        ?: throw IllegalStateException("Conversation ID is required")

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Pour stocker le texte du message en cours de frappe par l'utilisateur
    private val _currentMessageText = MutableStateFlow("")
    val currentMessageText: StateFlow<String> = _currentMessageText.asStateFlow()

    // Optionnel: stocker le nom de l'autre utilisateur si vous le passez en argument ou le récupérez
    // val otherUserName: String? = savedStateHandle.get<String>("otherUserName")

    init {
        if (conversationId.isNotBlank()) {
            fetchMessages()
        } else {
            _error.value = "Conversation ID is missing."
            Log.e("ConvVM", "Conversation ID is blank in init.")
        }
    }

    private fun fetchMessages() {
        Log.d("ConvVM", "Fetching messages for conversation: $conversationId")
        viewModelScope.launch {
            conversationRepository.getMessagesForConversation(conversationId)
                .onStart {
                    _isLoading.value = true
                    _error.value = null
                    Log.d("ConvVM", "Message fetching started...")
                }
                .catch { e ->
                    Log.e("ConvVM", "Error fetching messages flow", e)
                    _error.value = "Failed to load messages: ${e.message}"
                    _isLoading.value = false
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { messageList ->
                            _messages.value = messageList
                            _isLoading.value = false // Peut être redondant si onStart le fait bien
                            Log.d("ConvVM", "Messages updated: ${messageList.size} messages")
                        },
                        onFailure = { e ->
                            Log.e("ConvVM", "Error collecting messages result", e)
                            _error.value = "Error processing messages: ${e.message}"
                            _isLoading.value = false
                        }
                    )
                }
        }
    }

    fun onCurrentMessageTextChanged(newText: String) {
        _currentMessageText.value = newText
    }

    fun sendMessage() {
        val textToSend = _currentMessageText.value.trim()
        if (textToSend.isBlank()) {
            _error.value = "Message cannot be empty." // Peut-être afficher un Toast ou un SnackBar
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null || currentUser.uid.isBlank()) {
            _error.value = "User not authenticated to send message."
            Log.w("ConvVM", "User not authenticated or UID is blank.")
            return
        }

        // Dénormaliser le nom de l'utilisateur.
        // Idéalement, le nom de l'utilisateur est déjà connu (profil, etc.)
        // Si ce n'est pas le cas, vous pourriez avoir besoin de le récupérer.
        // Pour cet exemple, supposons qu'il est disponible via currentUser.displayName
        // ou que vous l'avez récupéré et mis en cache.
        val senderName = currentUser.displayName ?: "Unknown User" // Ou une logique plus robuste pour obtenir le nom

        _isLoading.value = true // Indicateur de chargement pour l'envoi
        viewModelScope.launch {
            val result = conversationRepository.sendMessage(
                conversationId = conversationId,
                text = textToSend,
                senderId = currentUser.uid,
                senderName = senderName
            )

            result.fold(
                onSuccess = {
                    _currentMessageText.value = "" // Effacer le champ de texte après l'envoi
                    _error.value = null // Effacer les erreurs précédentes
                    Log.d("ConvVM", "Message sent successfully.")
                    // Le Flow de messages devrait se mettre à jour automatiquement
                },
                onFailure = { e ->
                    Log.e("ConvVM", "Error sending message", e)
                    _error.value = "Failed to send message: ${e.message}"
                }
            )
            _isLoading.value = false // Fin de l'indicateur de chargement
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Le `callbackFlow` dans le repository s'occupe de supprimer l'écouteur Firestore
        // grâce à `awaitClose`.
        Log.d("ConvVM", "ViewModel cleared for conversation $conversationId")
    }
}