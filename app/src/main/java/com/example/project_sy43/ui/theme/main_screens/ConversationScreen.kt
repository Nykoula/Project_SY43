package com.example.project_sy43.ui.theme.main_screens

//import DateFormatter
import android.annotation.SuppressLint
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

import com.example.project_sy43.ui.theme.components.MessageInputRow


// Factory pour ConversationViewModel - à placer dans un fichier séparé ou un graphe d'injection
// Ceci est un exemple simple. Utilisez Hilt ou Koin pour une meilleure gestion des dépendances.
class ConversationViewModelFactory(
    private val conversationRepository: ConversationRepository,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T { // Ensure 'extras' type is CreationExtras
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle() // Correct way to get SavedStateHandle
            @Suppress("UNCHECKED_CAST")
            return ConversationViewModel(conversationRepository, auth, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// Fonction pour fournir la factory (à remplacer par une injection de dépendances)
@Composable
fun provideConversationViewModelFactory(): ConversationViewModelFactory {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val repository = ConversationRepository(firestore, auth)
    return ConversationViewModelFactory(repository, auth)
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Padding géré manuellement
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    navController: NavController,
    conversationId: String, // Doit être passé lors de la navigation
    otherUserName: String?, // Optionnel, peut être passé ou récupéré
    // viewModel: ConversationViewModel = viewModel() // Si factory globale
    viewModel: ConversationViewModel = viewModel(
        factory = provideConversationViewModelFactory(), // Utilisation de la factory locale
        // La clé n'est pas nécessaire ici car SavedStateHandle s'occupe de la portée par ID
    )
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentMessageText by viewModel.currentMessageText.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Faire défiler vers le bas lorsque de nouveaux messages arrivent ou que le clavier apparaît
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(otherUserName ?: "Conversation") }, // Afficher le nom de l'autre utilisateur
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Appliquer le padding du Scaffold
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isLoading && messages.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        MessageBubble(message = message)
                    }
                }

                MessageInputRow(
                    text = currentMessageText,
                    onTextChanged = { viewModel.onCurrentMessageTextChanged(it) },
                    onSendClicked = {
                        if (currentMessageText.isNotBlank()) {
                            viewModel.sendMessage()
                        }
                    }
                )
            }
        }
    )
}

object DateFormatter {
    // Consider using a specific Locale if consistency is important
    // across different user device settings.
    private val messageTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun formatMessageTime(date: Date): String {
        return messageTimeFormatter.format(date)
    }
}

@Composable
fun MessageBubble(message: Message) {
    val bubbleColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSecondaryContainer
    val bubbleAlignment = if (message.isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (message.isSentByCurrentUser) 16.dp else 0.dp,
        bottomEnd = if (message.isSentByCurrentUser) 0.dp else 16.dp
    )
    // Formatteur de date simple
    //val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = bubbleAlignment as Alignment.Horizontal
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isSentByCurrentUser) 16.dp else 0.dp,
                        bottomEnd = if (message.isSentByCurrentUser) 0.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                if (!message.isSentByCurrentUser && message.senderName != null) {
                    Text(
                        text = message.senderName!!,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                message.text?.let {
                    Text(
                        text = it,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                message.imageUrl?.let {
                    // TODO: Afficher l'image

                    Text("Image: $it", color = textColor) // Placeholder
                }
            }
        }
        message.timestamp?.toDate()?.let { date ->
            Text(
                text = DateFormatter.formatMessageTime(date), // Use the utility
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}