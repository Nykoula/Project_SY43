package com.example.project_sy43.ui.theme.main_screens

//import DateFormatter
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_sy43.model.Message
import com.example.project_sy43.repository.ConversationRepository
import com.example.project_sy43.viewmodel.ConversationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.util.Date
import kotlin.text.format
import androidx.compose.runtime.getValue
import com.example.project_sy43.model.Product
import com. example. project_sy43.model. Conversation
import com.example.project_sy43.navigation.VintedScreen

import com. example. project_sy43.ui. theme. components. MessageInputSection
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = viewModel(),
    navController: NavController,
    conversationId: String,
    onCancel: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid
    val messages by viewModel.messages.collectAsState()
    val currentMessageText by viewModel.currentMessageText.collectAsState()
    // val conversationDetails by viewModel.conversationDetails.collectAsState() // If needed directly
    val otherParticipantName by viewModel.otherParticipantDisplayName.collectAsState()
    val isLoadingMessages by viewModel.isLoadingMessages.collectAsState()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsState() // For initial details load
    val isSendingMessage by viewModel.isSendingMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    val currentOfferPrice by viewModel.currentOfferPrice.collectAsState()
    val currentOfferOptionalText by viewModel.currentOfferOptionalText.collectAsState()

    LaunchedEffect(key1 = conversationId) {
        if (conversationId.isNotBlank()) {
            viewModel.initialize(conversationId)
        }
    }

    Scaffold(
        topBar = {
            VintedTopBar(
                title = otherParticipantName ?: "Conversation",
                navController = navController,
                canGoBack = true
            )
        },
        bottomBar = {
            VintedBottomBar(
                navController = navController,
                currentScreen = VintedScreen.Conversation // Ajoute Conversation dans ton enum si besoin
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
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Loading indicator for initial messages/details
            if (isLoadingDetails || (messages.isEmpty() && isLoadingMessages)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // 3. Messages List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        val isCurrentUser = message.senderId == currentUserId
                        MessageBubble(message = message, isCurrentUserSender = isCurrentUser)
                    }
                }
            }

            MessageInputSection(
                currentMessageText = currentMessageText,
                onMessageChange = { viewModel.onCurrentMessageTextChanged(it) },
                onSendMessage = { viewModel.sendTextMessage() },
                isSending = isSendingMessage,
                currentOfferPrice = "",
                onOfferPriceChange = {},
                currentOfferOptionalText = "",
                onOfferOptionalTextChange = {},
                onSendOffer = {}
            )
        }
    }
}

// --- Placeholder for Message Item Composable ---
@Composable
fun MessageItem(message: Message) {
    // Determine if the message is sent by the current user to align it differently
    val alignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = bubbleColor)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Text(text = message.senderName ?: "Unknown", style = MaterialTheme.typography.labelSmall) // If you have senderName
                if (message.type == "offer" && message.proposedPrice != null) {
                    Text("Offer: $${"%.2f".format(message.proposedPrice)}", style = MaterialTheme.typography.bodyMedium)
                    message.text?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Text(message.text ?: "", style = MaterialTheme.typography.bodyMedium)
                }
                // Text(text = message.timestamp?.toDate()?.toString() ?: "sending...", style = MaterialTheme.typography.labelSmall) // Format timestamp
            }
        }
    }
}
@Composable
fun MessageBubble(
    message: Message ,
    isCurrentUserSender: Boolean
) {
    val bubbleColor =
        if (isCurrentUserSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isCurrentUserSender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    // Alignement horizontal : à droite si message de l'utilisateur, à gauche sinon
    val horizontalArrangement = if (isCurrentUserSender) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp , vertical = 4.dp) ,
        horizontalArrangement = horizontalArrangement
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp) // Limite la largeur max de la bulle
                .background(
                    color = bubbleColor ,
                    shape = RoundedCornerShape(
                        topStart = 16.dp ,
                        topEnd = 16.dp ,
                        bottomStart = if (isCurrentUserSender) 16.dp else 0.dp ,
                        bottomEnd = if (isCurrentUserSender) 0.dp else 16.dp
                    )
                )
                .padding(horizontal = 12.dp , vertical = 8.dp)
        ) {
            Text(
                text = message.text ?: "" ,
                color = textColor ,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}