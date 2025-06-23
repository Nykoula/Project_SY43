package com.example.project_sy43.ui.theme.main_screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberImagePainter
import com.example.project_sy43.viewmodel.ProductViewModel
import com.google.firebase.firestore.FirebaseFirestore

data class Post(
    val title: String = "",
    val taille: String = "",
    val price: Double = 0.0,
    val photos: List<String> = emptyList(),
    val state: String = "",
    val id: String = ""
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
            .whereEqualTo("available", true) // Filtrer les documents où "available" est true
            .get()
            .addOnSuccessListener { result ->
                val fetchedPosts = result.map { document ->
                    Post(
                        title = document.getString("title") ?: "",
                        taille = document.getString("size") ?: "",
                        state = document.getString("state") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        photos = document.get("photos") as? List<String> ?: emptyList(),
                        id = document.id
                    )
                }
                posts = fetchedPosts
            }
            .addOnFailureListener { exception ->
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Recommandations", style = MaterialTheme.typography.headlineSmall)
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(posts.take(10)) { post ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .height(350.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = {
                                navController.navigate("${VintedScreen.ArticleDetail.name}/${post.id}")
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (post.photos.isNotEmpty()) {
                                    Image(
                                        painter = rememberImagePainter(post.photos[0]),
                                        contentDescription = "Post Image",
                                        modifier = Modifier
                                            .width(150.dp)
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                if (post.title.isNotEmpty()) {
                                    Text(
                                        text = post.title,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (post.taille.isNotEmpty()) {
                                    Text(
                                        text = post.taille,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (post.state.isNotEmpty()) {
                                    Text(
                                        text = post.state,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Text(
                                    text = "${post.price}€",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text("You might be interested in", style = MaterialTheme.typography.headlineSmall)
            }
            items(posts.drop(10).take(displayCount)) { post ->
                Card(
                    modifier = Modifier
                        .width(400.dp)
                        .height(410.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    onClick = {
                        navController.navigate("${VintedScreen.ArticleDetail.name}/${post.id}")
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (post.photos.isNotEmpty()) {
                            Image(
                                painter = rememberImagePainter(post.photos[0]),
                                contentDescription = "Post Image",
                                modifier = Modifier
                                    .width(380.dp)
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (post.title.isNotEmpty()) {
                            Text(
                                text = post.title,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (post.taille.isNotEmpty()) {
                            Text(
                                text = post.taille,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (post.state.isNotEmpty()) {
                            Text(
                                text = post.state,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Text(
                            text = "${post.price}€",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item {
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
}
