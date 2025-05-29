package com.example.project_sy43.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.isNotEmpty
import kotlin.text.isNotEmpty

@Composable
fun ClothingDetailView(
    personViewModel: PersonViewModel = viewModel(),
    navController: NavController,
    itemId: String?,
    onCancel: () -> Unit
){
    var clothing by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(itemId) {
        itemId?.let {
            FirebaseFirestore.getInstance()
                .collection("Post")
                .document(it)
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
            VintedTopBar(title = clothing?.title ?: "",navController, true, "", true)
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
            if (clothing?.photos?.isNotEmpty() ?: false) {
                LazyColumn(
                    modifier = Modifier.height(250.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clothing?.photos.orEmpty()) { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = clothing?.title,
                            modifier = Modifier
                                .width(400.dp)
                                .height(250.dp)
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
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
                    if (clothing?.taille?.isNotEmpty() ?: false) {
                        DetailRow("Taille", clothing?.taille.toString())
                    }
                    if (clothing?.state?.isNotEmpty() ?: false) {
                        DetailRow("État", clothing?.state.toString())
                    }
                    if (clothing?.colis?.isNotEmpty() ?: false) {
                        DetailRow("Format du colis", clothing?.colis.toString())
                    }
                    if (clothing?.couleur?.isNotEmpty() ?: false) {
                        DetailRow("Couleurs", clothing?.couleur?.joinToString(", ") ?: "")
                    }
                    if (clothing?.matieres?.isNotEmpty() ?: false) {
                        DetailRow("Matières", clothing?.matieres?.joinToString(", ") ?: "")
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
        }
    }
}