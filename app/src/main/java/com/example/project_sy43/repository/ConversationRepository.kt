package com.example.project_sy43.repository // Assurez-vous que le package est correct

import android.util.Log
import com.example.project_sy43.model.Conversation
import com.example.project_sy43.model.Message // Assurez-vous d'importer votre classe Message
import com.example.project_sy43.model.Product
// import com.example.project_sy43.model.User // Si vous avez une classe User complète
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.collections.remove

class ConversationRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Fetches the raw list of conversations for the current user.
     * Does not include other user's name or product image details yet.
     */
    suspend fun getConversationsForCurrentUser(): Result<List<Conversation>> {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Log.w("ConvRepo", "User not logged in, cannot fetch conversations.")
            return Result.failure(Exception("User not logged in."))
        }

        return try {
            Log.d("ConvRepo", "Fetching conversations for user: $currentUserId")
            val snapshot = db.collection("conversation")
                .whereArrayContains("participant", currentUserId)
                // Optionnel : vous pourriez trier ici par un timestamp de dernier message
                // si vous le dénormalisez au niveau de la conversation.
                // .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d("ConvRepo", "No conversations found for user $currentUserId in Firestore. Snapshot Empty")
            } else {
                Log.d("ConvRepo", "${snapshot.size()} raw conversations documents found for user $currentUserId.")
            }

            val conversations = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Conversation::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("ConvRepo", "Error parsing conversation document ${doc.id}", e)
                    null
                }
            }
            Result.success(conversations)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching conversations from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches the username for a given user ID.
     */
    suspend fun getUserName(userId: String): Result<String?> {
        if (userId.isBlank()) return Result.success(null) // Ou Result.failure si c'est une erreur
        return try {
            val userDoc = db.collection("users").document(userId).get().await()
            if (userDoc.exists()) {
                // Adaptez "username" au nom réel de votre champ dans Firestore
                Result.success(userDoc.getString("username"))
            } else {
                Log.w("ConvRepo", "User document not found for ID: $userId")
                Result.success(null) // Ou Result.failure si l'utilisateur doit exister
            }
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching user name for $userId", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches the first image URL for a given product ID.
     */
    suspend fun getProductImageUrl(productId: String): Result<String?> {
        if (productId.isBlank()) return Result.success(null) // Ou Result.failure
        return try {
            // Adaptez "items" au nom réel de votre collection de produits
            val productDoc = db.collection("items").document(productId).get().await()
            if (productDoc.exists()) {
                val product = productDoc.toObject(Product::class.java)
                Result.success(product?.photos?.firstOrNull())
            } else {
                Log.w("ConvRepo", "Product document not found for ID: $productId")
                Result.success(null) // Ou Result.failure
            }
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error fetching product image for $productId", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches user names in batch for a list of user IDs.
     * This is an example of batching to reduce N+1 queries if denormalization isn't fully applied.
     * Firestore 'in' query supports up to 10 elements. For more, you'd need multiple queries.
     * For simplicity, this example handles up to 30 (Firebase's new limit for 'in' queries).
     */
    suspend fun fetchUserNamesInBatch(userIds: List<String>): Result<Map<String, String?>> {
        if (userIds.isEmpty()) return Result.success(emptyMap())
        // Firestore 'in' query supports up to 30 comparison values.
        // If you have more, you'll need to chunk userIds into lists of 30.
        // For this example, assuming userIds.size <= 30
        if (userIds.size > 30) {
            Log.w("ConvRepo", "fetchUserNamesInBatch called with ${userIds.size} IDs. Firestore 'in' query might be limited. Consider chunking.")
            // Implement chunking if necessary.
        }

        return try {
            val usersMap = mutableMapOf<String, String?>()
            val snapshot = db.collection("users")
                .whereIn(FieldPath.documentId(), userIds.distinct())
                .get()
                .await()
            snapshot.documents.forEach { doc ->
                usersMap[doc.id] = doc.getString("username") // Adapt "username"
            }
            // For any IDs not found, they won't be in the map, which is fine for String?
            Result.success(usersMap)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error batch fetching user names", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches product image URLs in batch for a list of product IDs.
     */
    suspend fun fetchProductImagesInBatch(productIds: List<String>): Result<Map<String, String?>> {
        if (productIds.isEmpty()) return Result.success(emptyMap())
        if (productIds.size > 30) {
            Log.w("ConvRepo", "fetchProductImagesInBatch called with ${productIds.size} IDs. Consider chunking.")
        }

        return try {
            val productImagesMap = mutableMapOf<String, String?>()
            val snapshot = db.collection("items") // Adapt "items"
                .whereIn(FieldPath.documentId(), productIds.distinct())
                .get()
                .await()
            snapshot.documents.forEach { doc ->
                val product = doc.toObject(Product::class.java)
                productImagesMap[doc.id] = product?.photos?.firstOrNull()
            }
            Result.success(productImagesMap)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error batch fetching product images", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches messages for a specific conversation in real-time, ordered by timestamp.
     */
    fun getMessagesForConversation(conversationId: String): Flow<Result<List<Message>>> = callbackFlow {
        if (conversationId.isBlank()) {
            trySend(Result.failure(IllegalArgumentException("Conversation ID cannot be blank.")))
            close()
            return@callbackFlow
        }

        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            trySend(Result.failure(Exception("User not logged in.")))
            close()
            return@callbackFlow
        }

        val messagesCollection = db.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING) // ASC pour afficher les plus anciens en premier

        val listenerRegistration = messagesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ConvRepo", "Error listening to messages for $conversationId", error)
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            if (snapshot == null) {
                Log.w("ConvRepo", "Messages snapshot is null for $conversationId")
                trySend(Result.success(emptyList())) // Ou une erreur spécifique
                return@addSnapshotListener
            }

            val messages = snapshot.documents.mapNotNull { doc ->
                try {
                    val message = doc.toObject(Message::class.java)?.copy(id = doc.id)
                    message?.isSentByCurrentUser = message?.senderId == currentUserId
                    message
                } catch (e: Exception) {
                    Log.e("ConvRepo", "Error parsing message document ${doc.id} in $conversationId", e)
                    null
                }
            }
            Log.d("ConvRepo", "Fetched ${messages.size} messages for $conversationId")
            trySend(Result.success(messages))
        }

        // Lorsque le Flow est annulé (le scope du collecteur est détruit),
        // nous devons supprimer l'écouteur pour éviter les fuites de mémoire.
        awaitClose {
            Log.d("ConvRepo", "Closing messages listener for $conversationId")
            listenerRegistration.remove()
        }
    }

    /**
     * Sends a new message to a conversation.
     */
    suspend fun sendMessage(conversationId: String, text: String, senderId: String, senderName: String): Result<Unit> {
        if (conversationId.isBlank() || text.isBlank() || senderId.isBlank()) {
            return Result.failure(IllegalArgumentException("Invalid message parameters."))
        }

        return try {
            val messageData = hashMapOf(
                "senderId" to senderId,
                "senderName" to senderName, // Dénormaliser le nom de l'expéditeur
                "text" to text,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp() // Utiliser le timestamp du serveur
                // "imageUrl" to null // si c'est un message texte
            )

            db.collection("conversations").document(conversationId)
                .collection("messages")
                .add(messageData)
                .await() // Attendre la fin de l'écriture

            // Optionnel : Mettre à jour le champ lastMessage de la conversation parente
            // Ceci est important si vous triez vos conversations par le dernier message.
            // Vous pouvez le faire ici ou via une Cloud Function pour plus de robustesse.
            val conversationRef = db.collection("conversations").document(conversationId)
            val lastMessageSummary = mapOf(
                "text" to text,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "senderId" to senderId
                // Ajoutez d'autres champs si votre objet lastMessage dans Conversation en a besoin
            )
            conversationRef.update("lastMessage", lastMessageSummary).await()


            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ConvRepo", "Error sending message to $conversationId", e)
            Result.failure(e)
        }
    }

}