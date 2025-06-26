package com.example.project_sy43.repository

import android.util.Log
import com.example.project_sy43.model.Conversation
import com.example.project_sy43.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldPath

class ConversationRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Fetches the main conversation document details.
     */
    suspend fun getConversationDetails(conversationId: String): Result<Conversation?> {
        if (conversationId.isBlank()) {
            Log.w("ConvRepo", "getConversationDetails called with blank conversationId.")
            return Result.failure(IllegalArgumentException("Conversation ID cannot be blank."))
        }
        return try {
            Log.d("ConvRepo", "Fetching conversation details for ID: $conversationId")
            val documentSnapshot = firestore.collection("conversations")
                .document(conversationId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val conversation = documentSnapshot.toObject(Conversation::class.java)
                conversation?.id = documentSnapshot.id
                Log.d("ConvRepo", "Conversation details fetched successfully for ID $conversationId: $conversation")
                Result.success(conversation)
            } else {
                Log.w("ConvRepo", "Conversation document not found for ID: $conversationId")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching conversation details for ID $conversationId", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches messages for a specific conversation in real-time, ordered by timestamp.
     */
    fun getMessagesForConversation(conversationId: String): Flow<Result<List<Message>>> =
        callbackFlow {
            if (conversationId.isBlank()) {
                Log.w("ConvRepo", "getMessagesForConversation called with blank conversationId.")
                trySend(Result.failure(IllegalArgumentException("Conversation ID cannot be blank.")))
                close()
                return@callbackFlow
            }

            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                Log.w("ConvRepo", "User not authenticated, cannot fetch messages.")
                trySend(Result.failure(IllegalStateException("User not authenticated.")))
                close()
                return@callbackFlow
            }

            Log.d("ConvRepo", "Setting up messages listener for conversation: $conversationId")
            val messagesCollection = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)

            val listenerRegistration = messagesCollection.addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ConvRepo", "Firestore listen failed for messages in $conversationId.", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val messagesList = snapshots.documents.mapNotNull { doc ->
                        try {
                            val message = doc.toObject(Message::class.java)
                            message?.id = doc.id
                            message?.isSentByCurrentUser = message?.senderId == currentUserId
                            message
                        } catch (e: Exception) {
                            Log.e("ConvRepo", "Error parsing message document ${doc.id} in $conversationId", e)
                            null
                        }
                    }
                    Log.d("ConvRepo", "Successfully fetched/updated ${messagesList.size} messages for $conversationId.")
                    trySend(Result.success(messagesList))
                } else {
                    Log.d("ConvRepo", "Messages snapshot is null for $conversationId (no error, but no data).")
                    trySend(Result.success(emptyList()))
                }
            }

            awaitClose {
                Log.d("ConvRepo", "Closing Firestore messages listener for $conversationId.")
                listenerRegistration.remove()
            }
        }

    /**
     * Sends a new text message to a conversation.
     */
    suspend fun sendTextMessage(
        conversationId: String,
        text: String,
        senderId: String
    ): Result<Unit> {
        if (conversationId.isBlank() || text.isBlank() || senderId.isBlank()) {
            Log.w("ConvRepo", "sendTextMessage called with invalid parameters. ConvID: $conversationId, Text: '$text', SenderID: $senderId")
            return Result.failure(IllegalArgumentException("Invalid text message parameters: conversationId, text, and senderId cannot be blank."))
        }
        return try {
            Log.d("ConvRepo", "Attempting to send text message to $conversationId: '$text'")
            val messageDocumentRef = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document()

            val messageData = hashMapOf(
                "senderId" to senderId,
                "timestamp" to FieldValue.serverTimestamp(),
                "type" to "text",
                "text" to text
            )

            messageDocumentRef.set(messageData).await()
            Log.d("ConvRepo", "Text message successfully sent to $conversationId. Message ID: ${messageDocumentRef.id}")

            // Update last message info
            updateLastMessage(conversationId, text, senderId)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error sending text message to $conversationId", e)
            Result.failure(e)
        }
    }

    /**
     * Sends a new offer message to a conversation.
     */
    suspend fun sendOfferMessage(
        conversationId: String,
        proposedPrice: Double,
        optionalText: String?,
        senderId: String
    ): Result<Unit> {
        if (conversationId.isBlank() || senderId.isBlank()) {
            Log.w("ConvRepo", "sendOfferMessage called with invalid parameters. ConvID: $conversationId, SenderID: $senderId")
            return Result.failure(IllegalArgumentException("Invalid offer message parameters: conversationId and senderId cannot be blank."))
        }
        return try {
            Log.d("ConvRepo", "Attempting to send offer message to $conversationId. Price: $proposedPrice, Text: '$optionalText'")
            val messageDocumentRef = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document()

            val messageData = hashMapOf<String, Any?>(
                "senderId" to senderId,
                "timestamp" to FieldValue.serverTimestamp(),
                "type" to "offer",
                "proposedPrice" to proposedPrice
            )

            if (!optionalText.isNullOrBlank()) {
                messageData["text"] = optionalText
            }

            messageDocumentRef.set(messageData).await()
            Log.d("ConvRepo", "Offer message successfully sent to $conversationId. Message ID: ${messageDocumentRef.id}")

            val lastMessageSummary = if (!optionalText.isNullOrBlank()) {
                optionalText
            } else {
                "Offer: $${"%.2f".format(proposedPrice)}"
            }
            updateLastMessage(conversationId, lastMessageSummary, senderId)

            // Update negotiated price
            firestore.collection("conversations").document(conversationId)
                .update("currentNegotiatedPrice", proposedPrice)
                .await()
            Log.d("ConvRepo", "Updated currentNegotiatedPrice to $proposedPrice for $conversationId")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error sending offer message to $conversationId", e)
            Result.failure(e)
        }
    }

    /**
     * Helper function to update the last message summary on the parent conversation document.
     */
    private suspend fun updateLastMessage(conversationId: String, messageText: String, senderId: String) {
        try {
            val conversationUpdateData = mapOf(
                "lastMessageText" to messageText,
                "lastMessageTimestamp" to FieldValue.serverTimestamp(),
                "lastMessageSenderId" to senderId
            )
            firestore.collection("conversations").document(conversationId)
                .set(conversationUpdateData, SetOptions.merge())
                .await()
            Log.d("ConvRepo", "Last message updated for conversation $conversationId: '$messageText'")
        } catch (e: Exception) {
            Log.e("ConvRepo", "Failed to update last message for conversation $conversationId", e)
        }
    }

    /**
     * Fetches all conversations where the current user is a participant.
     * AMÉLIORATION : Collection "Person" au lieu de "users"
     */
    suspend fun getConversationsForCurrentUser(): Result<List<Conversation>> {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Log.w("ConvRepo", "User not logged in. Cannot fetch conversations.")
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return try {
            val querySnapshot = firestore.collection("conversations")
                .whereArrayContains("participants", currentUserId)
                .get()
                .await()

            Log.d("ConvRepo", "Fetched ${querySnapshot.size()} conversations for user $currentUserId")

            val conversations = querySnapshot.documents.mapNotNull { document ->
                try {
                    val conversation = document.toObject(Conversation::class.java)
                    conversation?.id = document.id

                    // Déterminer l'autre utilisateur
                    val participants = document.get("participants") as? List<String>
                    val otherUserId = participants?.find { it != currentUserId }

                    if (otherUserId != null) {
                        // CORRECTION : Utiliser la collection "Person" au lieu de "users"
                        try {
                            val userDoc = firestore.collection("Person").document(otherUserId).get().await()
                            val firstName = userDoc.getString("firstName")
                            val lastName = userDoc.getString("lastName")
                            conversation?.otherUserName = when {
                                !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
                                !firstName.isNullOrBlank() -> firstName
                                !lastName.isNullOrBlank() -> lastName
                                else -> "Utilisateur inconnu"
                            }
                        } catch (e: Exception) {
                            Log.e("ConvRepo", "Error fetching user name for $otherUserId", e)
                            conversation?.otherUserName = "Utilisateur inconnu"
                        }
                    }

                    // Récupérer l'image du produit
                    val productId = document.getString("productId")
                    if (!productId.isNullOrBlank()) {
                        try {
                            val productDoc = firestore.collection("products").document(productId).get().await()
                            conversation?.productImageUrl = productDoc.getString("imageUrl")
                        } catch (e: Exception) {
                            Log.e("ConvRepo", "Error fetching product image for $productId", e)
                        }
                    }

                    conversation
                } catch (e: Exception) {
                    Log.e("ConvRepo", "Error converting conversation document ${document.id}", e)
                    null
                }
            }

            // Trier par timestamp du dernier message (les plus récents en premier)
            val sortedConversations = conversations.sortedByDescending { conversation ->
                conversation.lastMessageTimestamp
            }

            Log.d("ConvRepo", "Successfully processed ${sortedConversations.size} conversations for user $currentUserId")
            Result.success(sortedConversations)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching conversations for user $currentUserId", e)
            Result.failure(e)
        }
    }

    /**
     * AMÉLIORATION : Utilise la collection "Person" au lieu de "users"
     */
    suspend fun fetchUserNamesInBatch(userIds: List<String>): Result<Map<String, String?>> {
        if (userIds.isEmpty()) return Result.success(emptyMap())
        val usersMap = mutableMapOf<String, String?>()
        try {
            userIds.chunked(30).forEach { chunk ->
                val usersSnapshot = firestore.collection("Person") // CHANGEMENT ICI
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()
                for (document in usersSnapshot.documents) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val fullName = when {
                        !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
                        !firstName.isNullOrBlank() -> firstName
                        !lastName.isNullOrBlank() -> lastName
                        else -> "Utilisateur inconnu"
                    }
                    usersMap[document.id] = fullName
                }
            }
            return Result.success(usersMap)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching user names in batch", e)
            return Result.failure(e)
        }
    }

    /**
     * Fetches product image URLs for a list of product IDs.
     */
    suspend fun fetchProductImagesInBatch(productIds: List<String>): Result<Map<String, String?>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val imagesMap = mutableMapOf<String, String?>()

            // Firestore ne supporte pas les requêtes "in" avec plus de 10 éléments, donc batch si nécessaire
            val batches = productIds.chunked(10)

            for (batch in batches) {
                val querySnapshot = db.collection("Post")
                    .whereIn(FieldPath.documentId(), batch)
                    .get()
                    .await()

                for (doc in querySnapshot.documents) {
                    val photos = doc.get("photos") as? List<String> ?: emptyList()
                    val firstPhoto = photos.firstOrNull()
                    imagesMap[doc.id] = firstPhoto
                }
            }
            Result.success(imagesMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
        Log.d("ConversationRepository", "Fetching images for product IDs: $productIds")
    }


    /**
     * NOUVELLE FONCTION : Récupère le nom d'un utilisateur spécifique
     */
    suspend fun getUserName(userId: String): Result<String> {
        return try {
            val userDoc = firestore.collection("Person").document(userId).get().await()
            if (userDoc.exists()) {
                val firstName = userDoc.getString("firstName")
                val lastName = userDoc.getString("lastName")
                val fullName = when {
                    !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
                    !firstName.isNullOrBlank() -> firstName
                    !lastName.isNullOrBlank() -> lastName
                    else -> "Utilisateur inconnu"
                }
                Result.success(fullName)
            } else {
                Result.success("Utilisateur inconnu")
            }
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching user name for $userId", e)
            Result.failure(e)
        }
    }

    suspend fun createConversation(conversation: Conversation): String {
        val docRef = firestore.collection("conversations").add(conversation).await()
        return docRef.id
    }

    suspend fun acceptOffer(
        conversationId: String,
        messageId: String,
        acceptingUserId: String
    ): Result<Unit> {
        return try {
            val firestore = FirebaseFirestore.getInstance()

            // Marquer l'offre comme acceptée dans la collection des messages
            firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document(messageId)
                .update("isAccepted", true)
                .await()

            // Ajouter l'offre acceptée dans une sous-collection pour un suivi facile
            firestore.collection("conversations")
                .document(conversationId)
                .collection("acceptedOffers")
                .document(messageId)
                .set(mapOf(
                    "messageId" to messageId,
                    "acceptedBy" to acceptingUserId,
                    "acceptedAt" to FieldValue.serverTimestamp()
                ))
                .await()

            Log.d("ConversationRepository", "Offer $messageId accepted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ConversationRepository", "Error accepting offer $messageId", e)
            Result.failure(e)
        }
    }

    suspend fun getAcceptedOffers(conversationId: String): Result<List<String>> {
        return try {
            val firestore = FirebaseFirestore.getInstance()

            val snapshot = firestore.collection("conversations")
                .document(conversationId)
                .collection("acceptedOffers")
                .get()
                .await()

            val acceptedOfferIds = snapshot.documents.mapNotNull { it.getString("messageId") }

            Log.d("ConversationRepository", "Retrieved ${acceptedOfferIds.size} accepted offers")
            Result.success(acceptedOfferIds)
        } catch (e: Exception) {
            Log.e("ConversationRepository", "Error getting accepted offers", e)
            Result.failure(e)
        }
    }

    // Modifiez également la méthode sendOfferMessage pour inclure l'image du produit :
    suspend fun sendOfferMessage(
        conversationId: String,
        proposedPrice: Double,
        optionalText: String?,
        senderId: String,
        productImageUrl: String? = null
    ): Result<Unit> {
        return try {
            val firestore = FirebaseFirestore.getInstance()

            val messageData = mutableMapOf<String, Any>(
                "senderID" to senderId,
                "timestamp" to FieldValue.serverTimestamp(),
                "type" to "offer",
                "proposedPrice" to proposedPrice,
                "isAccepted" to false
            )

            // Ajouter le texte optionnel s'il existe
            optionalText?.let { messageData["text"] = it }

            // Ajouter l'URL de l'image du produit s'elle existe
            productImageUrl?.let { messageData["productImageUrl"] = it }

            // Créer le message d'offre
            firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .add(messageData)
                .await()

            // Mettre à jour les informations de dernière conversation
            updateLastMessage(conversationId,  "Offre: ${String.format("%.2f", proposedPrice)}€", senderId)

            Log.d("ConversationRepository", "Offer message sent successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ConversationRepository", "Error sending offer message", e)
            Result.failure(e)
        }
    }


}