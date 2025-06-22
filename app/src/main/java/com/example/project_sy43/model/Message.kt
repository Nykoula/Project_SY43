package com.example.project_sy43.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Message (
    var id: String = "", // Document ID

    @get:PropertyName("senderID") @set:PropertyName("senderID")
        var senderId: String = "",

    @PropertyName("senderName")
    var senderName: String? = null,

    @get:PropertyName("text") @set:PropertyName("text")
        var text: String? = null,

    @ServerTimestamp @get:PropertyName("timestamp") @set:PropertyName("timestamp")
        var timestamp: Timestamp? = null,

    @PropertyName("imageUrl")
    var imageUrl: String? = null,

    @get:PropertyName("type") @set:PropertyName("type")
        var type: String = "text", // "text" or "proposition"

    @get:PropertyName("proposedPrice") @set:PropertyName("proposedPrice")
        var proposedPrice: Double? = null,

    var isSentByCurrentUser: Boolean = false
) {
    constructor() : this("", "", null, null, null, null, "", null, false) // Constructeur sans argument pour Firestore
}
