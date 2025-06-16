package com.example.project_sy43.model

//import android.os.Message
import com.google.firebase.firestore.PropertyName
import com.example.project_sy43.model.Message

data class Conversation (
    @PropertyName("buyerID")
        var buyerId: String = "",
    @PropertyName("sellerID")
        var sellerId: String = "",
    @PropertyName("productID")
        var productId: String = "",
    @PropertyName("currentNegotiatedPrice")
        var currentNegotiatedPrice: Double = 0.0,
    @PropertyName("participants")
        var participants: List<String> = emptyList(),

    @PropertyName("lastMessage")
        var lastMessage: Message? = null,
    @PropertyName("mostRecentMessageID")
        var mostRecentMessageId: String = "",

    // For UI purposes
    var id: String = "", // Document ID
    var otherUserName: String? = null,
    var productImageUrl: String? = null
)