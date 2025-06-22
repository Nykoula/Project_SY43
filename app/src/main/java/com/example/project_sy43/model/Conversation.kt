//package com.example.project_sy43.model
//
////import android.os.Message
//import com.google.firebase.firestore.PropertyName
//import com.example.project_sy43.model.Message
//
//data class Conversation (
//    @PropertyName("buyerID")
//        var buyerId: String = "",
//    @PropertyName("sellerID")
//        var sellerId: String = "",
//    @PropertyName("productID")
//        var productId: String = "",
//    @PropertyName("currentNegotiatedPrice")
//        var currentNegotiatedPrice: Double = 0.0,
//    @PropertyName("participants")
//        var participants: List<String> = emptyList(),
//
//    @PropertyName("lastMessage")
//        var lastMessage: Message? = null,
//    @PropertyName("mostRecentMessageID")
//        var mostRecentMessageId: String = "",
//
//    // For UI purposes
//    var id: String = "", // Document ID
//    var otherUserName: String? = null,
//    var productImageUrl: String? = null
//)

// model/Conversation.kt
package com.example.project_sy43.model

import com.google.firebase.Timestamp // Assuming you use Firebase Timestamp
import com.google.firebase.firestore.PropertyName
import com.example.project_sy43.model.Message

data class Conversation(
//    @get:PropertyName("id") @set:PropertyName("id") // If "id" is a field in your Firestore doc
//    var firestoreId: String = "", // Using a different name to avoid conflict with UI 'id'

//    @get:PropertyName("type") @set:PropertyName("type")
//    var type: String = "marketplace_chat",

    @get:PropertyName("buyerID") @set:PropertyName("buyerID")
    var buyerId: String = "",

    @get:PropertyName("sellerID") @set:PropertyName("sellerID")
    var sellerId: String = "",

    @get:PropertyName("productID") @set:PropertyName("productID") // You had this before, ensure it's in Firestore or remove
    var productId: String = "",

    @get:PropertyName("currentNegotiatedPrice") @set:PropertyName("currentNegotiatedPrice")
    var currentNegotiatedPrice: Double = 0.0, // Ensure this field exists in Firestore or handle its absence

    @get:PropertyName("participant") @set:PropertyName("participant")
    var participants: List<String> = emptyList(),

    // lastMessage is a map with the fields senderID, text, timestamp
    // We'll use individual properties for these fields and construct the Message object manually if needed.

    @get:PropertyName("lastMessage.senderID") @set:PropertyName("lastMessage.senderID")
    var lastMessageSenderId: String = "", // Changed to avoid conflict if you have a senderId property elsewhere

    @get:PropertyName("lastMessage.text") @set:PropertyName("lastMessage.text")
    var lastMessageText: String? = null,

    @get:PropertyName("lastMessage.timestamp") @set:PropertyName("lastMessage.timestamp")
    var lastMessageTimestamp: Timestamp? = null,

    // For UI purposes - These are not directly from the main conversation document usually,
    // but populated by the ViewModel or Repository.
    // 'id' here is often used as the document ID for ViewModel state.
    @Transient var id: String = "", // Document ID, will be set from firestoreId
    @Transient var otherUserName: String? = null,
    @Transient var productImageUrl: String? = null,
    @Transient var lastMessageObject: Message? = null // If you want to hold the full last message object for UI
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this(
        //firestoreId = "", // Assuming firestoreId is not a primary constructor parameter
        //type = "marketplace_chat",
        buyerId = "",
        sellerId = "",
        productId = "",
        currentNegotiatedPrice = 0.0,
        participants = emptyList(),
        lastMessageSenderId = "",
        lastMessageText = null,
        lastMessageTimestamp = null,
        id = "", // Initialize transient property
        otherUserName = null, // Initialize transient property
        productImageUrl = null, // Initialize transient property
        lastMessageObject = null // Initialize transient property
    )
}
