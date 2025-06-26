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

    @get:PropertyName("isAccepted") @set:PropertyName("isAccepted")
    var isAccepted: Boolean = false, // Pour les offres acceptées

    @get:PropertyName("productImageUrl") @set:PropertyName("productImageUrl")
    var productImageUrl: String? = null, // URL de l'image du produit pour les offres

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
        proposedPrice = null,
        isAccepted = false,
        productImageUrl = null
    )
}