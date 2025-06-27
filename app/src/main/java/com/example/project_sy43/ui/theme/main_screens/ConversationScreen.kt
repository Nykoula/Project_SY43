package com.example.project_sy43.ui.theme.main_screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_sy43.model.Message
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.MessageInputSection
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.ConversationViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = viewModel() ,
    navController: NavController ,
    conversationId: String
) {
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid
    val messages by viewModel.messages.collectAsState()
    val currentMessageText by viewModel.currentMessageText.collectAsState()
    val otherParticipantName by viewModel.otherParticipantDisplayName.collectAsState()
    val isLoadingMessages by viewModel.isLoadingMessages.collectAsState()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsState()
    val isSendingMessage by viewModel.isSendingMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    val currentOfferPrice by viewModel.currentOfferPrice.collectAsState()
    val currentOfferOptionalText by viewModel.currentOfferOptionalText.collectAsState()
    val showOfferSection by viewModel.showOfferSection.collectAsState()

    LaunchedEffect(key1 = conversationId) {
        if (conversationId.isNotBlank()) {
            viewModel.initialize(conversationId)
        }
    }

    Scaffold(
        topBar = {
            VintedTopBar(
                title = otherParticipantName ?: "Conversation" ,
                navController = navController ,
                canGoBack = true
            )
        } ,
        bottomBar = {
            VintedBottomBar(
                navController = navController ,
                currentScreen = VintedScreen.Conversation
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display error if any
            error?.let {
                Text(
                    text = "Error: $it" ,
                    color = MaterialTheme.colorScheme.error ,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Loading indicator for initial messages/details
            if (isLoadingDetails || (messages.isEmpty() && isLoadingMessages)) {
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f) ,
                    contentPadding = PaddingValues(16.dp) ,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(messages) { index , message ->
                        val isCurrentUser = message.senderId == currentUserId
                        val showDateSeparator = shouldShowDateSeparator(messages , index)

                        if (showDateSeparator) {
                            DateSeparator(message.timestamp)
                        }

                        MessageBubble(
                            message = message ,
                            isCurrentUserSender = isCurrentUser ,
                            onAcceptOffer = { messageId , price ->
                                viewModel.acceptOffer(messageId , price)
                            } ,
                            canAcceptOffer = !isCurrentUser && !viewModel.isOfferAccepted(message.id) ,
                            isOfferAccepted = viewModel.isOfferAccepted(message.id) ,
                            showBuyButton = viewModel.isOfferAccepted(message.id) && viewModel.isCurrentUserBuyer() ,
                            onBuy = {
                                val productId = viewModel.conversationDetails.value?.productId
                                val acceptedPrice = message.proposedPrice

                                if (!productId.isNullOrBlank() && acceptedPrice != null) {
                                    navController.navigate("ResumeBeforePurchaseScreen/$productId/$acceptedPrice")
                                }
                            }
                        )
                    }
                }
            }

            MessageInputSection(
                currentMessageText = currentMessageText ,
                onMessageChange = { viewModel.onCurrentMessageTextChanged(it) } ,
                onSendMessage = { viewModel.sendTextMessage() } ,
                isSending = isSendingMessage ,
                currentOfferPrice = currentOfferPrice ,
                onOfferPriceChange = { viewModel.onOfferPriceChanged(it) } ,
                currentOfferOptionalText = currentOfferOptionalText ,
                onOfferOptionalTextChange = { viewModel.onOfferOptionalTextChanged(it) } ,
                onSendOffer = { viewModel.sendOfferMessage() } ,
                showOfferSection = showOfferSection ,
                onToggleOfferSection = { viewModel.toggleOfferSection() }
            )
        }
    }
}

@Composable
fun DateSeparator(timestamp: Timestamp?) {
    timestamp?.let {
        val date = it.toDate()
        val dateFormat = SimpleDateFormat("dd MMM yyyy" , Locale.getDefault())
        val dateString = dateFormat.format(date)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f) ,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            Text(
                text = dateString ,
                modifier = Modifier.padding(horizontal = 16.dp) ,
                style = MaterialTheme.typography.labelSmall ,
                color = MaterialTheme.colorScheme.onSurfaceVariant ,
                fontWeight = FontWeight.Medium
            )
            Divider(
                modifier = Modifier.weight(1f) ,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        }
    }
}

fun shouldShowDateSeparator(messages: List<Message> , currentIndex: Int): Boolean {
    if (currentIndex == 0) return true

    val currentMessage = messages[currentIndex]
    val previousMessage = messages[currentIndex - 1]

    val currentDate = currentMessage.timestamp?.toDate()
    val previousDate = previousMessage.timestamp?.toDate()

    if (currentDate == null || previousDate == null) return false

    val currentCalendar = Calendar.getInstance().apply { time = currentDate }
    val previousCalendar = Calendar.getInstance().apply { time = previousDate }

    return currentCalendar.get(Calendar.DAY_OF_YEAR) != previousCalendar.get(Calendar.DAY_OF_YEAR) ||
            currentCalendar.get(Calendar.YEAR) != previousCalendar.get(Calendar.YEAR)
}

@Composable
fun MessageBubble(
    message: Message ,
    isCurrentUserSender: Boolean ,
    onAcceptOffer: (String , Double) -> Unit = { _ , _ -> } ,
    canAcceptOffer: Boolean = false ,
    isOfferAccepted: Boolean = false ,
    showBuyButton: Boolean = false , // <-- Ajouté
    onBuy: () -> Unit = {}
) {
    val bubbleColor =
        if (isCurrentUserSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isCurrentUserSender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    val horizontalArrangement = if (isCurrentUserSender) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp , vertical = 2.dp) ,
        horizontalArrangement = horizontalArrangement
    ) {
        Column(
            horizontalAlignment = if (isCurrentUserSender) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = bubbleColor ,
                        shape = RoundedCornerShape(
                            topStart = 16.dp ,
                            topEnd = 16.dp ,
                            bottomStart = if (isCurrentUserSender) 16.dp else 4.dp ,
                            bottomEnd = if (isCurrentUserSender) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp , vertical = 8.dp)
            ) {
                if (message.type == "offer" && message.proposedPrice != null) {
                    OfferMessageContent(
                        message = message ,
                        textColor = textColor ,
                        onAcceptOffer = onAcceptOffer ,
                        canAcceptOffer = canAcceptOffer ,
                        isOfferAccepted = isOfferAccepted ,
                        showBuyButton = showBuyButton , // <-- Transmis ici
                        onBuy = onBuy
                    )
                } else {
                    Text(
                        text = message.text ?: "" ,
                        color = textColor ,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Time display
            message.timestamp?.let { timestamp ->
                val timeFormat = SimpleDateFormat("HH:mm" , Locale.getDefault())
                val timeString = timeFormat.format(timestamp.toDate())

                Text(
                    text = timeString ,
                    style = MaterialTheme.typography.labelSmall ,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) ,
                    modifier = Modifier.padding(
                        top = 2.dp ,
                        start = if (isCurrentUserSender) 0.dp else 12.dp ,
                        end = if (isCurrentUserSender) 12.dp else 0.dp
                    )
                )
            }
        }
    }
}


@Composable
fun OfferMessageContent(
    message: Message ,
    textColor: androidx.compose.ui.graphics.Color ,
    onAcceptOffer: (String , Double) -> Unit ,
    canAcceptOffer: Boolean ,
    isOfferAccepted: Boolean ,
    showBuyButton: Boolean ,
    onBuy: () -> Unit
) {
    Column {
        // Prix proposé
        Text(
            text = "Offre: ${String.format("%.2f" , message.proposedPrice)}€" ,
            color = textColor ,
            style = MaterialTheme.typography.bodyMedium ,
            fontWeight = FontWeight.Bold
        )

        // Message optionnel
        message.text?.takeIf { it.isNotBlank() }?.let { optionalText ->
            Text(
                text = optionalText ,
                color = textColor ,
                style = MaterialTheme.typography.bodySmall ,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Bouton d'acceptation ou statut
        if (isOfferAccepted) {
            Text(
                text = "✓ Offre acceptée" ,
                color = textColor.copy(alpha = 0.8f) ,
                style = MaterialTheme.typography.labelSmall ,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (showBuyButton) {
                Button(
                    onClick = onBuy ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp) ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Acheter")
                }
            }
        } else if (canAcceptOffer) {
            Button(
                onClick = {
                    message.proposedPrice?.let { price ->
                        onAcceptOffer(message.id , price)
                    }
                } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp) ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    Icons.Default.Check ,
                    contentDescription = null ,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Accepter")
            }
        }
    }
}