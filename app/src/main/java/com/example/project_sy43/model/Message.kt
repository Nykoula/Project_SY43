package com.example.project_sy43.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Message (
    @get:PropertyName("senderID") @set:PropertyName("senderID")
        var senderId: String = "",
    @get:PropertyName("text") @set:PropertyName("text")
        var text: String = "",
    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
        var timestamp: Timestamp = Timestamp.now(),
    @get:PropertyName("type") @set:PropertyName("type")
        var type: String = "text", // "text" or "proposition"
    @get:PropertyName("proposedPrice") @set:PropertyName("proposedPrice")
        var proposedPrice: Double? = null,
    var id: String = "" // Document ID
)
