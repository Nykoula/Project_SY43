package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.viewmodel.MessagesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.project_sy43.R
import com.example.project_sy43.model.Conversation
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Messages(
    messagesViewModel: MessagesViewModel = viewModel(),
    navController: NavController,
    onCancel: () -> Unit
) {
    val conversations by messagesViewModel.conversations.collectAsState(emptyList())
    val isLoading by messagesViewModel.isLoading.collectAsState()
    val error by messagesViewModel.error.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            VintedTopBar(title = "Messages", navController, false)
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.Messages)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF007782)
                        )
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Erreur de chargement",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {  },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF007782)
                                )
                            ) {
                                Text("Réessayer")
                            }
                        }
                    }
                }

                conversations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Store,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF007782).copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Aucune conversation",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Vous n'avez pas encore de conversations actives.\nAllez dans l'onglet Recherche pour commencer !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(conversations, key = { it.id }) { conversation ->
                            ConversationItem(
                                conversation = conversation,
                                onItemClick = { conversationId ->
                                    navController.navigate("${VintedScreen.Conversation.name}/$conversationId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(conversation.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image du produit ou icône par défaut
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!conversation.productImageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = conversation.productImageUrl),
                    contentDescription = "Image du produit",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF007782).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Store,
                        contentDescription = "Pas d'image produit",
                        tint = Color(0xFF007782),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Informations de la conversation
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Nom de l'autre utilisateur
            Text(
                text = conversation.otherUserName ?: "Utilisateur inconnu",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dernier message
            Text(
                text = conversation.lastMessageText ?: "Aucun message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Timestamp du dernier message (optionnel)
        if (conversation.lastMessageTimestamp != null) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatTimestamp(conversation.lastMessageTimestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Formate un timestamp en chaîne lisible
 */
private fun formatTimestamp(timestamp: Any?): String {
    return try {
        when (timestamp) {
            is com.google.firebase.Timestamp -> {
                val date = timestamp.toDate()
                val now = Date()
                val diff = now.time - date.time

                when {
                    diff < 60000 -> "À l'instant" // Moins d'1 minute
                    diff < 3600000 -> "${diff / 60000}min" // Moins d'1 heure
                    diff < 86400000 -> "${diff / 3600000}h" // Moins d'1 jour
                    else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                }
            }
            is Date -> {
                val now = Date()
                val diff = now.time - timestamp.time

                when {
                    diff < 60000 -> "À l'instant"
                    diff < 3600000 -> "${diff / 60000}min"
                    diff < 86400000 -> "${diff / 3600000}h"
                    else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(timestamp)
                }
            }
            else -> ""
        }
    } catch (e: Exception) {
        ""
    }
}