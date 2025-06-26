
package com.example.project_sy43.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Conversation(
    @PropertyName("buyerID") var buyerId: String = "",
    @PropertyName("sellerID") var sellerId: String = "",
    @PropertyName("productID") var productId: String = "",
    @PropertyName("currentNegotiatedPrice") var currentNegotiatedPrice: Double = 0.0,
    @PropertyName("participant") var participants: List<String> = emptyList(),

    @PropertyName("lastMessage.senderID") var lastMessageSenderId: String = "",
    @PropertyName("lastMessage.text") var lastMessageText: String? = null,
    @PropertyName("lastMessage.timestamp") var lastMessageTimestamp: Timestamp? = null,

    var id: String = "",
    var otherUserName: String? = null,
    var productImageUrl: String? = null,
    var lastMessageObject: Message? = null
) {

    constructor() : this(
        buyerId = "",
        sellerId = "",
        productId = "",
        currentNegotiatedPrice = 0.0,
        participants = emptyList(),
        lastMessageSenderId = "",
        lastMessageText = null,
        lastMessageTimestamp = null,
        id = "",
        otherUserName = null,
        productImageUrl = null,
        lastMessageObject = null
    )
}