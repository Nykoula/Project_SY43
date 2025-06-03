package com.example.project_sy43.model

//import android.os.Message
import com.google.firebase.firestore.PropertyName
import com.example.project_sy43.model.Message

data class Conversation (
    @get:PropertyName("buyerID") @set:PropertyName("buyerId")
        var buyerId: String = "",
    @get:PropertyName("sellerID") @set:PropertyName("sellerId")
        var sellerId: String = "",
    @get:PropertyName("productID") @set:PropertyName("productId")
        var productId: String = "",
    @get:PropertyName("currentNegotiatedPrice") @set:PropertyName("currentNegotiatedPrice")
        var currentNegotiatedPrice: Double = 0.0,
    @get:PropertyName("participants") @set:PropertyName("participants")
        var participants: List<String> = emptyList(),

    @get:PropertyName("lastMessage") @set:PropertyName("lastMessage")
        var lastMessage: Message? = null,
    @get:PropertyName("mostRecentMessageID") @set:PropertyName("mostRecentMessageID")
        var mostRecentMessageId: String = "",

    // For UI purposes
    var id: String = "", // Document ID
    var otherUserName: String? = null,
    var productImageUrl: String? = null
)