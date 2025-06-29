package com.example.project_sy43.ui.theme.main_screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Data class pour les vêtements vendus
data class SoldClothingItem(
    val id: String = "" ,
    val title: String = "" ,
    val description: String = "" ,
    val price: Double = 0.0 ,
    val size: String = "" ,
    val category: String = "" ,
    val type: String = "" ,
    val state: String = "" ,
    val colis: String = "" ,
    val color: List<String> = emptyList() ,
    val material: List<String> = emptyList() ,
    val photos: List<String> = emptyList() ,
    val userId: String = "" ,
    val dateCreation: String = "" ,
    val isAvailable: Boolean = true
)

@Composable
fun Dressing(
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // États pour les données
    var soldItems by remember { mutableStateOf<List<SoldClothingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Charger les vêtements vendus depuis la collection Post
    LaunchedEffect(userId) {
        userId?.let { uid ->
            db.collection("Post")
                .whereEqualTo("userId" , uid)
                .get()
                .addOnSuccessListener { documents ->
                    soldItems = documents.map { document ->
                        SoldClothingItem(
                            id = document.id ,
                            title = document.getString("title") ?: "" ,
                            description = document.getString("description") ?: "" ,
                            price = document.getDouble("price") ?: 0.0 ,
                            size = document.getString("size") ?: "" ,
                            category = document.getString("category") ?: "" ,
                            type = document.getString("type") ?: "" ,
                            state = document.getString("state") ?: "" ,
                            colis = document.getString("colis") ?: "" ,
                            color = (document.get("color") as? List<*>)?.mapNotNull { it as? String }
                                ?: emptyList() ,
                            material = (document.get("material") as? List<*>)?.mapNotNull { it as? String }
                                ?: emptyList() ,
                            photos = (document.get("photos") as? List<*>)?.mapNotNull { it as? String }
                                ?: emptyList() ,
                            userId = document.getString("userId") ?: "" ,
                            dateCreation = document.getString("dateCreation") ?: "" ,
                            isAvailable = document.getBoolean("available") != false
                        )
                    }.sortedByDescending { it.dateCreation }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    // Gérer l'erreur
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize() ,
        containerColor = Color.White ,
        topBar = {
            VintedTopBar(title = "Mon dressing" , navController , true)
        } ,
        bottomBar = {
            VintedBottomBar(navController , VintedScreen.Profile)
        }
    ) { innerPadding ->


        // Vue principale des vêtements vendus
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Titre avec nombre d'articles
            Text(
                text = "Mon dressing (${soldItems.size})" ,
                style = MaterialTheme.typography.titleLarge ,
                fontWeight = FontWeight.Bold ,
                color = Color(0xFF007782) ,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Contenu principal
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize() ,
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF007782))
                }
            } else {
                SoldClothingList(
                    items = soldItems ,
                    onItemClick = {
                        if (it.isAvailable) {
                            navController.navigate("${VintedScreen.ArticleDetail.name}/${it.id}?menuDeroulant=true")
                        } else {
                            navController.navigate("${VintedScreen.ArticleDetail.name}/${it.id}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SoldClothingList(
    items: List<SoldClothingItem> ,
    onItemClick: (SoldClothingItem) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize() ,
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Store ,
                    contentDescription = null ,
                    modifier = Modifier.size(64.dp) ,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Vous n'avez encore vendu aucun vêtement" ,
                    style = MaterialTheme.typography.bodyLarge ,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Commencez à vendre vos articles !" ,
                    style = MaterialTheme.typography.bodyMedium ,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize() ,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                SoldClothingListItem(
                    item = item ,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun SoldClothingListItem(
    item: SoldClothingItem ,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } ,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) ,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image du vêtement ou placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (item.photos.isNotEmpty()) {
                    AsyncImage(
                        model = item.photos.first() ,
                        contentDescription = item.title ,
                        modifier = Modifier.fillMaxSize() ,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF007782).copy(alpha = 0.1f)) ,
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Store ,
                            contentDescription = null ,
                            tint = Color(0xFF007782) ,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title ,
                    style = MaterialTheme.typography.bodyLarge ,
                    fontWeight = FontWeight.Medium ,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (item.category.isNotEmpty() || item.type.isNotEmpty()) {
                    Text(
                        text = "${item.category}${if (item.category.isNotEmpty() && item.type.isNotEmpty()) " • " else ""}${item.type}" ,
                        style = MaterialTheme.typography.bodyMedium ,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (item.size.isNotEmpty()) {
                    Text(
                        text = "${item.size}" ,
                        style = MaterialTheme.typography.bodyMedium ,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "${item.price}€" ,
                    style = MaterialTheme.typography.bodyMedium ,
                    fontWeight = FontWeight.Bold ,
                    color = Color(0xFF007782)
                )

                Spacer(modifier = Modifier.height(2.dp))
                Log.d("SoldClothingListItem" , "isAvailable: ${item.isAvailable} + ${item.title}")
                Text(
                    text = if (item.isAvailable) "À vendre" else "Vendu" ,
                    style = MaterialTheme.typography.bodySmall ,
                    color = if (item.isAvailable) Color.Green else Color.Red ,
                    fontWeight = FontWeight.Bold
                )

                if (item.state.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.state ,
                        style = MaterialTheme.typography.bodySmall ,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight ,
                contentDescription = "Voir détails" ,
                tint = Color.Gray ,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun DetailRow(label: String , value: String) {
    if (value.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp) ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label ,
                style = MaterialTheme.typography.bodyMedium ,
                color = Color.Gray ,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value ,
                style = MaterialTheme.typography.bodyMedium ,
                fontWeight = FontWeight.Medium ,
                color = Color.Black ,
                modifier = Modifier.weight(1f)
            )
        }
    }
}