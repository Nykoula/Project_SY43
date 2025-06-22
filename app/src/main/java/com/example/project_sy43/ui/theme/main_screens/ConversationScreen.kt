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

    val messages by viewModel.messages.collectAsState()
    val currentMessageText by viewModel.currentMessageText.collectAsState()
    // val conversationDetails by viewModel.conversationDetails.collectAsState() // If needed directly
    val otherParticipantName by viewModel.otherParticipantDisplayName.collectAsState()
    val isLoadingMessages by viewModel.isLoadingMessages.collectAsState()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsState() // For initial details load
    val isSendingMessage by viewModel.isSendingMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    // Offer states (if you have a separate UI for offers)
    val currentOfferPrice by viewModel.currentOfferPrice.collectAsState()
    val currentOfferOptionalText by viewModel.currentOfferOptionalText.collectAsState()

//    val isLoading by viewModel.isLoading.collectAsState()
//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//    val otherUserName = "John Doe"
//    var conv by remember { mutableStateOf<Conversation?>(null) }

    LaunchedEffect(key1 = conversationId) {
        if (conversationId.isNotBlank()) {
            viewModel.initialize(conversationId)
        }
        // else: Handle error state - conversationId is missing.
        // The ViewModel already logs an error if conversationId is blank during initialize.
        // You might want to show a UI error here or navigate back.
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
                    modifier = Modifier.weight(1f), // Takes up available space
                    reverseLayout = true // New messages appear at the bottom and list starts from bottom
                ) {
                    items(messages.reversed()) { message -> // Reverse for chronological order if reverseLayout is true
                        MessageItem(message = message) // You'll need to create this Composable
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



//    LaunchedEffect(conversationId) {
//        conversationId?.let {
//            FirebaseFirestore.getInstance()
//                .collection("conversation")
//                .document(it)
//                .get()
//                .addOnSuccessListener { document ->
//                    if (document != null && document.exists()) {
//                        // Convertir directement en data class
//                        val conversation = document.toObject(Conversation::class.java)
//                        conv = conversation?.copy(id = document.id) // on met l'id du document
//                    } else {
//                        Log.d("ConversationView", "Pas de document trouvé")
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("ConversationView", "Erreur Firestore", e)
//                }
//        }
//    }
//
//    // Faire défiler vers le bas lorsque de nouveaux messages arrivent ou que le clavier apparaît
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            coroutineScope.launch {
//                listState.animateScrollToItem(messages.size - 1)
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(otherUserName ?: "Conversation") }, // Afficher le nom de l'autre utilisateur
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF007782),
//                    titleContentColor = Color.White
//                )
//            )
//        },
//        content = { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues) // Appliquer le padding du Scaffold
//                    .background(MaterialTheme.colorScheme.background)
//            ) {
//                if (isLoading && messages.isEmpty()) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                } else if (error != null) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
//                    }
//                }
//
//                LazyColumn(
//                    state = listState,
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 8.dp),
//                    contentPadding = PaddingValues(vertical = 8.dp)
//                ) {
//                    items(messages, key = { it.id }) { message ->
//                        MessageBubble(message = message)
//                    }
//                }
//
//                MessageInputRow(
//                    text = currentMessageText,
//                    onTextChanged = { viewModel.onCurrentMessageTextChanged(it) },
//                    onSendClicked = {
//                        if (currentMessageText.isNotBlank()) {
//                            viewModel.sendMessage()
//                        }
//                    }
//                )
//            }
//        }
//    )
//}
//
//object DateFormatter {
//    // Consider using a specific Locale if consistency is important
//    // across different user device settings.
//    private val messageTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
//
//    fun formatMessageTime(date: Date): String {
//        return messageTimeFormatter.format(date)
//    }
//}

//@Composable
//fun MessageBubble(message: Message) {
//    val bubbleColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.primaryContainer
//    else MaterialTheme.colorScheme.secondaryContainer
//    val textColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer
//    else MaterialTheme.colorScheme.onSecondaryContainer
//    val bubbleAlignment = if (message.isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
//
//    val bubbleShape = RoundedCornerShape(
//        topStart = 16.dp,
//        topEnd = 16.dp,
//        bottomStart = if (message.isSentByCurrentUser) 16.dp else 0.dp,
//        bottomEnd = if (message.isSentByCurrentUser) 0.dp else 16.dp
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        horizontalAlignment = bubbleAlignment as Alignment.Horizontal
//    ) {
//        Box(
//            modifier = Modifier
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp,
//                        bottomStart = if (message.isSentByCurrentUser) 16.dp else 0.dp,
//                        bottomEnd = if (message.isSentByCurrentUser) 0.dp else 16.dp
//                    )
//                )
//                .background(bubbleColor)
//                .padding(horizontal = 12.dp, vertical = 8.dp)
//        ) {
//            Column {
//                if (!message.isSentByCurrentUser && message.senderName != null) {
//                    Text(
//                        text = message.senderName!!,
//                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
//                        color = textColor.copy(alpha = 0.8f),
//                        modifier = Modifier.padding(bottom = 2.dp)
//                    )
//                }
//                message.text?.let {
//                    Text(
//                        text = it,
//                        color = textColor,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
////                message.imageUrl?.let {
////                    // TODO: Afficher l'image
////
////                    Text("Image: $it", color = textColor) // Placeholder
////                }
//            }
//        }
//        message.timestamp?.toDate()?.let { date ->
//            Text(
//                text = DateFormatter.formatMessageTime(date),
//                style = MaterialTheme.typography.labelSmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
//            )
//        }
//    }
//}