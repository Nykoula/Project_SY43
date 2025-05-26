package com.example.project_sy43.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.items
import coil.compose.rememberImagePainter
import com.example.project_sy43.viewmodel.ProductViewModel
import com.google.firebase.firestore.FirebaseFirestore

data class Post(
    val title: String = "",
    val photos: List<String> = emptyList(),
    val state: String = ""
)

@Composable
fun MonCompte(
    viewModel: ProductViewModel,
    navController: NavController,
    onCancel: () -> Unit
) {
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var displayCount by remember { mutableStateOf(10) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Post")
            .get()
            .addOnSuccessListener { result ->
                val fetchedPosts = result.map { document ->
                    Post(
                        title = document.getString("title") ?: "",
                        state = document.getString("state") ?: "",
                        photos = document.get("photos") as? List<String> ?: emptyList()
                    )
                }
                posts = fetchedPosts
            }
            .addOnFailureListener { exception ->
                // Gérer l'erreur
                Log.e("Firestore", "Erreur lors de la récupération des posts : $exception")
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.MonCompte)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Recommandations", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Affichage latéral des recommandations
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(posts.take(10)) { post ->
                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .height(200.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(post.title)
                            Text(post.state)
                            if (post.photos.isNotEmpty()) {
                                Image(
                                    painter = rememberImagePainter(post.photos[0]),
                                    contentDescription = "Post Image",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Maybe Interesting You", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Affichage vertical des articles "Maybe Interesting You"
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(posts.drop(10).take(displayCount)) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(post.title)
                            Text(post.state)
                            if (post.photos.isNotEmpty()) {
                                Image(
                                    painter = rememberImagePainter(post.photos[0]),
                                    contentDescription = "Post Image",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            // Bouton "More" pour afficher plus d'articles
            Button(
                onClick = { displayCount += 10 },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("More")
            }
        }
    }
}
