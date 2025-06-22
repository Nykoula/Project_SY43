//package com.example.project_sy43.model
//
//import com.google.firebase.Timestamp
//import com.google.firebase.firestore.PropertyName
//import com.google.firebase.firestore.ServerTimestamp
//
//data class Message (
//    var id: String = "", // Document ID
//
//    @get:PropertyName("senderID") @set:PropertyName("senderID")
//        var senderId: String = "",
//
//    @PropertyName("senderName")
//    var senderName: String? = null,
//
//    @get:PropertyName("text") @set:PropertyName("text")
//        var text: String? = null,
//
//    @ServerTimestamp @get:PropertyName("timestamp") @set:PropertyName("timestamp")
//        var timestamp: Timestamp? = null,
//
//    @PropertyName("imageUrl")
//    var imageUrl: String? = null,
//
//    @get:PropertyName("type") @set:PropertyName("type")
//        var type: String = "text", // "text" or "proposition"
//
//    @get:PropertyName("proposedPrice") @set:PropertyName("proposedPrice")
//        var proposedPrice: Double? = null,
//
//    var isSentByCurrentUser: Boolean = false
//) {
//    constructor() : this("", "", null, null, null, null, "", null, false) // Constructeur sans argument pour Firestore
//}

// model/Message.kt
package com.example.project_sy43.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Message(
    @Transient var id: String = "", // Document ID of the message, populated after fetching

    @get:PropertyName("senderID") @set:PropertyName("senderID")
    var senderId: String = "",

    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Timestamp? = null,

    @get:PropertyName("type") @set:PropertyName("type")
    var type: String = "text", // "text" or "offer"

    @get:PropertyName("text") @set:PropertyName("text")
    var text: String? = null, // Optional for offer messages, present for text

    @get:PropertyName("proposedPrice") @set:PropertyName("proposedPrice")
    var proposedPrice: Double? = null, // Only for "offer" type messages

    // UI-specific, not in Firestore typically, populated by ViewModel/Repository
    @Transient var senderName: String? = null, // For displaying sender's name
    @Transient var isSentByCurrentUser: Boolean = false
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = "",
        senderId = "",
        timestamp = null,
        type = "text",
        text = null,
        proposedPrice = null
    )
}
