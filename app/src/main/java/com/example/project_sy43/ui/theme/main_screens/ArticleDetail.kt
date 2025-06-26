package com.example.project_sy43.ui.theme.main_screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_sy43.model.Product
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.isNotEmpty
import kotlin.text.isNotEmpty
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.project_sy43.viewmodel.MessagesViewModel


@Composable
fun ClothingDetailView(
    personViewModel: PersonViewModel = viewModel(),
    messagesViewModel: MessagesViewModel = viewModel(), // Injection du ViewModel Messages
    navController: NavController,
    itemId: String?,
    onCancel: () -> Unit,
    menuDeroulant: Boolean = false,
    topBar: Boolean? = true
) {
    var clothing by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isCreatingConversation by remember { mutableStateOf(false) }


    LaunchedEffect(itemId) {
        itemId?.let {id ->
            Log.d("ClothingDetailView", "Fetching details for itemId: $id")
            FirebaseFirestore.getInstance()
                .collection("Post")
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Convertir directement en data class
                        val item = document.toObject(Product::class.java)
                        clothing = item?.copy(id = document.id) // on met l'id du document
                    } else {
                        Log.d("ClothingDetailView", "Pas de document trouvé")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ClothingDetailView", "Erreur Firestore", e)
                }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            if (topBar == true) {
                VintedTopBar(
                    title = clothing?.title ?: "" ,
                    navController ,
                    true ,
                    "" ,
                    menuDeroulant = menuDeroulant ?: false ,
                    onEditClick = { navController.navigate("${VintedScreen.Sell.name}?itemId=${clothing?.id}") } ,
                    onDeleteClick = {
                        showDeleteDialog = true
                    }
                )
            }
        }
    ) { innerPadding ->

        // Contenu des détails
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Images du produit
            clothing?.let { nonNullClothing ->
                if (nonNullClothing.photos.isNotEmpty()) {
                    PhotoCarousel(photos = nonNullClothing.photos)
                } else {
                    // Placeholder si pas de photos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF007782).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Store,
                            contentDescription = null,
                            tint = Color(0xFF007782),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Informations principales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = clothing?.title.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${clothing?.price}€",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007782)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Détails du produit
                        if (clothing?.category?.isNotEmpty() ?: false) {
                            DetailRow("Catégorie", clothing?.category.toString())
                        }
                        if (clothing?.type?.isNotEmpty() ?: false) {
                            DetailRow("Type de vêtement", clothing?.type.toString())
                        }
                        if (clothing?.size?.isNotEmpty() ?: false) {
                            DetailRow("Taille", clothing?.size.toString())
                        }
                        if (clothing?.state?.isNotEmpty() ?: false) {
                            DetailRow("État", clothing?.state.toString())
                        }
                        if (clothing?.colis?.isNotEmpty() ?: false) {
                            DetailRow("Format du colis", clothing?.colis.toString())
                        }
                        if (clothing?.color?.isNotEmpty() ?: false) {
                            DetailRow("Couleurs", clothing?.color?.joinToString(", ") ?: "")
                        }
                        if (clothing?.material?.isNotEmpty() ?: false) {
                            DetailRow("Matières", clothing?.material?.joinToString(", ") ?: "")
                        }

                        if (clothing?.description?.isNotEmpty() ?: false) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = clothing?.description.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
                if (menuDeroulant == false && clothing?.available == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp , vertical = 8.dp) ,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                        onClick = {
                            navController.navigate("${VintedScreen.ResumeBeforePurchaseScreen.name}/${clothing?.id}/${clothing?.price}")
                        } ,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Acheter")
                    }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                clothing?.let { product ->
                                    if (!isCreatingConversation) {
                                        isCreatingConversation = true
                                        coroutineScope.launch {
                                            try {
                                                // Vérifie ou crée la conversation
                                                val conversationId = messagesViewModel.createConversation(
                                                    otherUserId = product.userId,
                                                    productId = product.id
                                                )

                                                // Recharge les conversations (utile si nouvellement créée)
                                                messagesViewModel.refreshConversations()

                                                // Navigation vers la conversation
                                                navController.navigate("${VintedScreen.Conversation.name}/$conversationId")
                                            } catch (e: Exception) {
                                                Log.e("ClothingDetailView", "Erreur conversation", e)
                                            } finally {
                                                isCreatingConversation = false
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isCreatingConversation
                        ) {
                            if (isCreatingConversation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Négocier")
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cet article ?") },
            confirmButton = {
                Button(
                    onClick = {
                        clothing?.id?.let { postId ->
                            deletePost(
                                postId = postId,
                                onSuccess = {
                                    navController.popBackStack()
                                },
                                onFailure = { exception ->
                                    Log.e("DeletePost", "Erreur lors de la suppression du post", exception)
                                }
                            )
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Oui", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007782))
                ) {
                    Text("Non")
                }
            }
        )
    }
}

private val db = FirebaseFirestore.getInstance()

fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("Post").document(postId)
        .delete()
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

@Composable
fun PhotoCarousel(photos: List<String>) {
    var currentPhotoIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (photos.isNotEmpty()) {
            AsyncImage(
                model = photos[currentPhotoIndex],
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            if (photos.size > 1) {
                // Flèche gauche
                IconButton(
                    onClick = {
                        currentPhotoIndex = if (currentPhotoIndex > 0) currentPhotoIndex - 1 else photos.size - 1
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }

                // Flèche droite
                IconButton(
                    onClick = {
                        currentPhotoIndex = if (currentPhotoIndex < photos.size - 1) currentPhotoIndex + 1 else 0
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


