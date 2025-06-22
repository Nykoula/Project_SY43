//package com.example.project_sy43.repository // Assurez-vous que le package est correct
//
//import android.util.Log
//import com.example.project_sy43.model.Conversation
//import com.example.project_sy43.model.Message // Assurez-vous d'importer votre classe Message
//import com.example.project_sy43.model.Product
//// import com.example.project_sy43.model.User // Si vous avez une classe User complète
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FieldPath
//import com.google.firebase.firestore.Query
//import com.google.firebase.firestore.ktx.toObject
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.tasks.await
//import kotlin.collections.remove
//
//class ConversationRepository(
//    private val db: FirebaseFirestore,
//    private val auth: FirebaseAuth
//) {
//
//    /**
//     * Fetches the raw list of conversations for the current user.
//     * Does not include other user's name or product image details yet.
//     */
//    suspend fun getConversationsForCurrentUser(): Result<List<Conversation>> {
//        val currentUserId = auth.currentUser?.uid
//        if (currentUserId == null) {
//            Log.w("ConvRepo", "User not logged in, cannot fetch conversations.")
//            return Result.failure(Exception("User not logged in."))
//        }
//
//        return try {
//            Log.d("ConvRepo", "Fetching conversations for user: $currentUserId")
//            val snapshot = db.collection("conversation")
//                .whereArrayContains("participant", currentUserId)
//                // Optionnel : vous pourriez trier ici par un timestamp de dernier message
//                // si vous le dénormalisez au niveau de la conversation.
//                // .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
//                .get()
//                .await()
//
//            if (snapshot.isEmpty) {
//                Log.d("ConvRepo", "No conversations found for user $currentUserId in Firestore. Snapshot Empty")
//            } else {
//                Log.d("ConvRepo", "${snapshot.size()} raw conversations documents found for user $currentUserId.")
//            }
//
//            val conversations = snapshot.documents.mapNotNull { doc ->
//                try {
//                    doc.toObject(Conversation::class.java)?.copy(id = doc.id)
//                } catch (e: Exception) {
//                    Log.e("ConvRepo", "Error parsing conversation document ${doc.id}", e)
//                    null
//                }
//            }
//            Result.success(conversations)
//        } catch (e: Exception) {
//            Log.e("ConvRepo", "Error fetching conversations from Firestore", e)
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * Fetches the username for a given user ID.
//     */
//    suspend fun getUserName(userId: String): Result<String?> {
//        if (userId.isBlank()) return Result.success(null) // Ou Result.failure si c'est une erreur
//        return try {
//            val userDoc = db.collection("users").document(userId).get().await()
//            if (userDoc.exists()) {
//                // Adaptez "username" au nom réel de votre champ dans Firestore
//                Result.success(userDoc.getString("username"))
//            } else {
//                Log.w("ConvRepo", "User document not found for ID: $userId")
//                Result.success(null) // Ou Result.failure si l'utilisateur doit exister
//            }
//        } catch (e: Exception) {
//            Log.e("ConvRepo", "Error fetching user name for $userId", e)
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * Fetches the first image URL for a given product ID.
//     */
//    suspend fun getProductImageUrl(productId: String): Result<String?> {
//        if (productId.isBlank()) return Result.success(null) // Ou Result.failure
//        return try {
//            // Adaptez "items" au nom réel de votre collection de produits
//            val productDoc = db.collection("items").document(productId).get().await()
//            if (productDoc.exists()) {
//                val product = productDoc.toObject(Product::class.java)
//                Result.success(product?.photos?.firstOrNull())
//            } else {
//                Log.w("ConvRepo", "Product document not found for ID: $productId")
//                Result.success(null) // Ou Result.failure
//            }
//        } catch (e: Exception) {
//            Log.e("ConvRepo", "Error fetching product image for $productId", e)
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * Fetches user names in batch for a list of user IDs.
//     * This is an example of batching to reduce N+1 queries if denormalization isn't fully applied.
//     * Firestore 'in' query supports up to 10 elements. For more, you'd need multiple queries.
//     * For simplicity, this example handles up to 30 (Firebase's new limit for 'in' queries).
//     */
//    suspend fun fetchUserNamesInBatch(userIds: List<String>): Result<Map<String, String?>> {
//        if (userIds.isEmpty()) return Result.success(emptyMap())
//        // Firestore 'in' query supports up to 30 comparison values.
//        // If you have more, you'll need to chunk userIds into lists of 30.
//        // For this example, assuming userIds.size <= 30
//        if (userIds.size > 30) {
//            Log.w("ConvRepo", "fetchUserNamesInBatch called with ${userIds.size} IDs. Firestore 'in' query might be limited. Consider chunking.")
//            // Implement chunking if necessary.
//        }
//
//        return try {
//            val usersMap = mutableMapOf<String, String?>()
//            val snapshot = db.collection("users")
//                .whereIn(FieldPath.documentId(), userIds.distinct())
//                .get()
//                .await()
//            snapshot.documents.forEach { doc ->
//                usersMap[doc.id] = doc.getString("username") // Adapt "username"
//            }
//            // For any IDs not found, they won't be in the map, which is fine for String?
//            Result.success(usersMap)
//        } catch (e: Exception) {
//            Log.e("ConvRepo", "Error batch fetching user names", e)
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * Fetches product image URLs in batch for a list of product IDs.
//     */
//    suspend fun fetchProductImagesInBatch(productIds: List<String>): Result<Map<String, String?>> {
//        if (productIds.isEmpty()) return Result.success(emptyMap())
//        if (productIds.size > 30) {
//            Log.w("ConvRepo", "fetchProductImagesInBatch called with ${productIds.size} IDs. Consider chunking.")
//        }
//
//        return try {
//            val productImagesMap = mutableMapOf<String, String?>()
//            val snapshot = db.collection("items") // Adapt "items"
//                .whereIn(FieldPath.documentId(), productIds.distinct())
//                .get()
//                .await()
//            snapshot.documents.forEach { doc ->
//                val product = doc.toObject(Product::class.java)
//                productImagesMap[doc.id] = product?.photos?.firstOrNull()
//            }
//            Result.success(productImagesMap)
//        } catch (e: Exception) {
//            Log.e("ConvRepo", "Error batch fetching product images", e)
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * Fetches messages for a specific conversation in real-time, ordered by timestamp.
//     */
//    fun getMessagesForConversation(conversationId: String): Flow<Result<List<Message>>> = callbackFlow {
//        if (conversationId.isBlank()) {
//            trySend(Result.failure(IllegalArgumentException("Conversation ID cannot be blank.")))
//            close()
//            return@callbackFlow
//        }
//
//        val currentUserId = auth.currentUser?.uid
//        if (currentUserId == null) {
//            trySend(Result.failure(Exception("User not logged in.")))
//            close()
//            return@callbackFlow
//        }
//
//        val messagesCollection = db.collection("conversations").document(conversationId)
//            .collection("messages")
//            .orderBy("timestamp", Query.Direction.ASCENDING) // ASC pour afficher les plus anciens en premier
//
//        val listenerRegistration = messagesCollection.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                Log.e("ConvRepo", "Error listening to messages for $conversationId", error)
//                trySend(Result.failure(error))
//                return@addSnapshotListener
//            }
//
//            if (snapshot == null) {
//                Log.w("ConvRepo", "Messages snapshot is null for $conversationId")
//                trySend(Result.success(emptyList())) // Ou une erreur spécifique
//                return@addSnapshotListener
//            }
//
//            val messages = snapshot.documents.mapNotNull { doc ->
//                try {
//                    val message = doc.toObject(Message::class.java)?.copy(id = doc.id)
//                    message?.isSentByCurrentUser = message?.senderId == currentUserId
//                    message
//                } catch (e: Exception) {
//                    Log.e("ConvRepo", "Error parsing message document ${doc.id} in $conversationId", e)
//                    null
//                }
//            }
//            Log.d("ConvRepo", "Fetched ${messages.size} messages for $conversationId")
//            trySend(Result.success(messages))
//        }
//
//        // Lorsque le Flow est annulé (le scope du collecteur est détruit),
//        // nous devons supprimer l'écouteur pour éviter les fuites de mémoire.
//        awaitClose {
//            Log.d("ConvRepo", "Closing messages listener for $conversationId")
//            listenerRegistration.remove()
//        }
//    }
//
//    /**
//     * Sends a new message to a conversation.
//     */
//    suspend fun sendMessage(conversationId: String, text: String, senderId: String, senderName: String): Result<Unit> {
//        if (conversationId.isBlank() || text.isBlank() || senderId.isBlank()) {
//            return Result.failure(IllegalArgumentException("Invalid message parameters."))
//        }
//
//        return try {
//            val messageData = hashMapOf(
//                "senderId" to senderId,
//                "senderName" to senderName, // Dénormaliser le nom de l'expéditeur
//                "text" to text,
//                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp() // Utiliser le timestamp du serveur
//                // "imageUrl" to null // si c'est un message texte
//            )
//
//            db.collection("conversations").document(conversationId)
//                .collection("messages")
//                .add(messageData)
//                .await() // Attendre la fin de l'écriture
//
//            // Optionnel : Mettre à jour le champ lastMessage de la conversation parente
//            // Ceci est important si vous triez vos conversations par le dernier message.
//            // Vous pouvez le faire ici ou via une Cloud Function pour plus de robustesse.
//            val conversationRef = db.collection("conversations").document(conversationId)
//            val lastMessageSummary = mapOf(
//                "text" to text,
//                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
//                "senderId" to senderId
//                // Ajoutez d'autres champs si votre objet lastMessage dans Conversation en a besoin
//            )
//            conversationRef.update("lastMessage", lastMessageSummary).await()
//
//
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Log.e("ConvRepo", "Error sending message to $conversationId", e)
//            Result.failure(e)
//        }
//    }
//
//}

// repository/ConversationRepository.kt
package com.example.project_sy43.repository

import android.util.Log
import com.example.project_sy43.model.Conversation // Your updated Conversation model
import com.example.project_sy43.model.Message    // Your updated Message model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Query // <--- Corrected this line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.get

// Remove these as they are not needed for the repository
// import kotlin.collections.get
// import kotlin.collections.remove
// import kotlin.text.get
// import kotlin.text.set

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
                // The 'id' field in your Conversation model is marked @Transient.
                // It should be populated with the document's ID here.
                conversation?.id = documentSnapshot.id
                Log.d("ConvRepo", "Conversation details fetched successfully for ID $conversationId: $conversation")
                Result.success(conversation)
            } else {
                Log.w("ConvRepo", "Conversation document not found for ID: $conversationId")
                Result.success(null) // Or Result.failure if the conversation must exist
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
            close() // Close the flow immediately
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
            .collection("messages") // Access the "messages" subcollection
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val listenerRegistration = messagesCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("ConvRepo", "Firestore listen failed for messages in $conversationId.", error)
                trySend(Result.failure(error)) // Send the error to the collector
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val messagesList = snapshots.documents.mapNotNull { doc ->
                    try {
                        val message = doc.toObject(Message::class.java)
                        message?.id = doc.id // Populate the @Transient id field
                        message?.isSentByCurrentUser = message?.senderId == currentUserId
                        // You could fetch senderName here if needed, or handle in ViewModel
                        message
                    } catch (e: Exception) {
                        Log.e("ConvRepo", "Error parsing message document ${doc.id} in $conversationId", e)
                        null // Skip faulty messages
                    }
                }
                Log.d("ConvRepo", "Successfully fetched/updated ${messagesList.size} messages for $conversationId.")
                trySend(Result.success(messagesList))
            } else {
                // This case might not be strictly necessary if Firestore always returns a snapshot,
                // even if empty, but good for robustness.
                Log.d("ConvRepo", "Messages snapshot is null for $conversationId (no error, but no data).")
                trySend(Result.success(emptyList()))
            }
        }

        // When the Flow is cancelled (e.g., ViewModel is cleared), remove the listener
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
        // You might consider passing senderName if you denormalize it into the message document
        // or fetch it from a user profile service before calling this.
    ): Result<Unit> {
        if (conversationId.isBlank() || text.isBlank() || senderId.isBlank()) {
            Log.w("ConvRepo", "sendTextMessage called with invalid parameters. ConvID: $conversationId, Text: '$text', SenderID: $senderId")
            return Result.failure(IllegalArgumentException("Invalid text message parameters: conversationId, text, and senderId cannot be blank."))
        }
        return try {
            Log.d("ConvRepo", "Attempting to send text message to $conversationId: '$text'")
            // Generate a new ID for the message document in the "messages" subcollection
            val messageDocumentRef = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document() // Creates a new document reference with a unique ID

            val messageData = hashMapOf(
                "senderId" to senderId,
                "timestamp" to FieldValue.serverTimestamp(), // Use server timestamp for consistency
                "type" to "text",
                "text" to text
                // "senderName" could be denormalized here for easier display later
            )

            messageDocumentRef.set(messageData).await() // Set the data for the new message document

            Log.d("ConvRepo", "Text message successfully sent to $conversationId. Message ID: ${messageDocumentRef.id}")

            // After sending the message, update the last message info on the parent conversation document
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
        optionalText: String?, // Optional accompanying text for the offer
        senderId: String
    ): Result<Unit> {
        if (conversationId.isBlank() || senderId.isBlank()) { // proposedPrice can be 0.0, so not checking it for blankness
            Log.w("ConvRepo", "sendOfferMessage called with invalid parameters. ConvID: $conversationId, SenderID: $senderId")
            return Result.failure(IllegalArgumentException("Invalid offer message parameters: conversationId and senderId cannot be blank."))
        }
        return try {
            Log.d("ConvRepo", "Attempting to send offer message to $conversationId. Price: $proposedPrice, Text: '$optionalText'")
            val messageDocumentRef = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document() // Creates a new document reference with a unique ID

            val messageData = hashMapOf<String, Any?>( // Use Any? for values as proposedPrice is Double
                "senderId" to senderId,
                "timestamp" to FieldValue.serverTimestamp(),
                "type" to "offer",
                "proposedPrice" to proposedPrice
            )
            // Add the optional text field only if it's not null or blank
            if (!optionalText.isNullOrBlank()) {
                messageData["text"] = optionalText
            }

            messageDocumentRef.set(messageData).await()
            Log.d("ConvRepo", "Offer message successfully sent to $conversationId. Message ID: ${messageDocumentRef.id}")

            // Determine the summary text for the last message field in the conversation document
            val lastMessageSummary = if (!optionalText.isNullOrBlank()) {
                optionalText // If text is provided, use it as the summary
            } else {
                "Offer: $${"%.2f".format(proposedPrice)}" // Default summary for an offer
            }
            updateLastMessage(conversationId, lastMessageSummary, senderId)

            // Update the currentNegotiatedPrice on the parent conversation document
            // This is a business logic decision. If every offer updates this, do it here.
            // If only accepted offers update it, this logic would be elsewhere.
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
     * Helper private function to update the last message summary on the parent conversation document.
     */
    private suspend fun updateLastMessage(conversationId: String, messageText: String, senderId: String) {
        // This function is private as it's only used internally by this repository.
        // 'senderId' is passed in case you decide to also store 'lastMessageSenderId' in the future.
        try {
            val conversationUpdateData = mapOf(
                "lastMessage" to messageText, // Your Firestore field name for the last message text
                "lastMessageTimestamp" to FieldValue.serverTimestamp() // Your Firestore field name for the timestamp
                // Optionally, if you also store who sent the last message at the top conversation level:
                // "lastMessageSenderId" to senderId
            )
            firestore.collection("conversations").document(conversationId)
                .set(conversationUpdateData, SetOptions.merge()) // Use SetOptions.merge() to only update specified fields
                .await()
            Log.d("ConvRepo", "Last message updated for conversation $conversationId: '$messageText'")
        } catch (e: Exception) {
            Log.e("ConvRepo", "Failed to update last message for conversation $conversationId", e)
            // Depending on your app's requirements, you might want to propagate this error.
            // For now, it's just logged.
        }
    }

    /**
     * Gets an existing conversation ID between a buyer and a seller for a specific product,
     * or creates a new one if it doesn't exist.
     *
     * Note: The logic for "finding" an existing conversation can be complex.
     * This example uses a query based on buyerId, sellerId, and productId.
     * You might need a more sophisticated approach or a predefined conversation ID format
     * if these queries become inefficient or if users can have multiple conversations
     * for the same product.
     *
     * @return Result containing the conversation ID (String).
     */
    suspend fun getOrCreateConversation(
        buyerId: String,
        sellerId: String,
        productId: String
        // You might want to pass initial user names too for denormalization
        // buyerName: String, sellerName: String
    ): Result<String> {
        if (buyerId.isBlank() || sellerId.isBlank() || productId.isBlank()) {
            return Result.failure(IllegalArgumentException("Buyer ID, Seller ID, and Product ID cannot be blank."))
        }
        if (buyerId == sellerId) {
            return Result.failure(IllegalArgumentException("Buyer ID and Seller ID cannot be the same."))
        }

        return try {
            // 1. Try to find an existing conversation
            // This query assumes you want only ONE conversation per buyer/seller/product.
            // Adjust if users can have multiple.
            val querySnapshot = firestore.collection("conversations")
                .whereEqualTo("type", "marketplace_chat") // Ensure it's the right type
                .whereEqualTo("productId", productId)
                // Firestore requires that array-contains/array-contains-any queries
                // are on a single field. So, we'll check participants by iterating results,
                // or ensure participants are always ordered the same way if querying directly.
                // A simpler way for two participants is to check both combinations or
                // ensure participants array is always sorted before storing.
                // For this example, let's query for product and then check participants in code,
                // assuming there won't be too many conversations for one product.
                .whereIn("participants", listOf(listOf(buyerId, sellerId), listOf(sellerId, buyerId)))
                .limit(1) // We only need one if it exists
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val existingConversationId = querySnapshot.documents[0].id
                Log.d("ConvRepo", "Found existing conversation: $existingConversationId")
                return Result.success(existingConversationId)
            }

            // 2. If no existing conversation, create a new one
            Log.d("ConvRepo", "No existing conversation found. Creating new one for product $productId between $buyerId and $sellerId.")
            val newConversationRef = firestore.collection("conversations").document()
            val newConversationId = newConversationRef.id

            // Ensure participants are always in a consistent order for querying, e.g., sorted.
            val participantsList = listOf(buyerId, sellerId).sorted()

            val conversationData = hashMapOf(
                // "id" field in document: some prefer to store it, some don't as it's the doc key.
                // If your Conversation model expects 'firestoreId' from the document, add it:
                // "id" to newConversationId,
                "type" to "marketplace_chat",
                "buyerId" to buyerId,
                "sellerId" to sellerId,
                "productId" to productId,
                "participants" to participantsList,
                "lastMessage" to null, // Or an initial message like "Conversation started"
                "lastMessageTimestamp" to FieldValue.serverTimestamp(), // Or null if no initial message
                "currentNegotiatedPrice" to 0.0 // Or an initial price from the product listing if applicable
                // Add any other fields from your Conversation model that need initial values
            )

            newConversationRef.set(conversationData).await()
            Log.d("ConvRepo", "Successfully created new conversation: $newConversationId")
            Result.success(newConversationId)

        } catch (e: Exception) {
            Log.e("ConvRepo", "Error getting or creating conversation", e)
            Result.failure(e)
        }
    }


    /**
     * Fetches all conversations where the current user is a participant.
     * Assumes conversations have a "participants" array field.
     */
    suspend fun getConversationsForCurrentUser(): Result<List<Conversation>> {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Log.w("MessagesViewModelBefore", "User not logged in. Cannot fetch conversations.")
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return try {
            val querySnapshot = firestore.collection("conversation")
                .whereArrayContains("participant", currentUserId)
                .orderBy("lastMessage.timestamp", Query.Direction.DESCENDING)
                .get()
                .await()


            Log.d("MessagesViewModelBefore", "Fetched ${querySnapshot.size()} conversations for user $currentUserId")

            val conversations = querySnapshot.documents.mapNotNull { document ->
                try {
                    val conversation = document.toObject(Conversation::class.java)
                    conversation?.id = document.id // Assuming your Conversation model has an id field
                    // If lastMessage is not directly part of Conversation object from Firestore,
                    // you might need to fetch it separately or ensure it's denormalized.
                    // For MessagesViewModel, having lastMessage (or at least its timestamp and a snippet)
                    // directly on the Conversation object is highly beneficial.
                    conversation
                } catch (e: Exception) {
                    Log.e("MessagesViewModelBefore", "Error converting conversation document ${document.id}", e)
                    null
                }
            }
            Log.d("MessagesViewModelBefore", "Fetched ${conversations.size} conversations for user $currentUserId")
            Result.success(conversations)
        } catch (e: Exception) {
            Log.e("MessagesViewModelBefore", "Error fetching conversations for user $currentUserId", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches display names for a list of user IDs.
     * Combines firstName and lastName.
     */
    suspend fun fetchUserNamesInBatch(userIds: List<String>): Result<Map<String, String?>> {
        if (userIds.isEmpty()) return Result.success(emptyMap())
        val usersMap = mutableMapOf<String, String?>()

        // Firestore 'in' query supports up to 30 equality clauses.
        // If userIds can be larger, you'll need to batch this further.
        // For simplicity, assuming userIds.size <= 30 here.
        // For larger lists, split userIds into chunks of 30.
        try {
            // Chunking example (if userIds can be > 30)
            userIds.chunked(30).forEach { chunk ->
                val usersSnapshot = firestore.collection("users")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()
                for (document in usersSnapshot.documents) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val fullName = when {
                        !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
                        //!firstName.isNullOrBlank() -> firstName
                        //!lastName.isNullOrBlank() -> lastName
                        else -> "Unknown User" // Fallback
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
     * Assumes products collection has a field like 'imageUrl' or 'mainImageUrl'.
     */
    suspend fun fetchProductImagesInBatch(productIds: List<String>): Result<Map<String, String?>> {
        if (productIds.isEmpty()) return Result.success(emptyMap())
        val productImagesMap = mutableMapOf<String, String?>()

        // Similar to userIds, chunk if productIds can be > 30
        try {
            productIds.chunked(30).forEach { chunk ->
                val productsSnapshot = firestore.collection("products") // Assuming "products" collection
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()
                for (document in productsSnapshot.documents) {
                    // Adjust "imageUrl" to your actual field name for the product image
                    val imageUrl = document.getString("imageUrl")
                    productImagesMap[document.id] = imageUrl
                }
            }
            return Result.success(productImagesMap)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching product images in batch", e)
            return Result.failure(e)
        }
    }
}