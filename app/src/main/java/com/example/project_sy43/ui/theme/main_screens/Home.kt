package com.example.project_sy43.ui.theme.main_screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.viewmodel.ProductViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class Post(
    val title: String = "" ,
    val taille: String = "" ,
    val price: Double = 0.0 ,
    val photos: List<String> = emptyList() ,
    val state: String = "" ,
    val id: String = ""
)

@Composable
fun MonCompte(
    navController: NavController
) {
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var displayCount by remember { mutableStateOf(10) }
    var isRefreshing by remember { mutableStateOf(false) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // Fonction pour charger les posts
    fun loadPosts() {
        isRefreshing = true
        db.collection("Post")
            .whereEqualTo("available" , true)
            .get()
            .addOnSuccessListener { result ->
                val fetchedPosts = result.mapNotNull { document ->
                    val userId = document.getString("userId")
                    if (userId != null && userId != currentUserId) {
                        Post(
                            title = document.getString("title") ?: "" ,
                            taille = document.getString("size") ?: "" ,
                            state = document.getString("state") ?: "" ,
                            price = document.getDouble("price") ?: 0.0 ,
                            photos = document.get("photos") as? List<String> ?: emptyList() ,
                            id = document.id
                        )
                    } else {
                        null
                    }
                }.shuffled()
                posts = fetchedPosts
                isRefreshing = false
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore" , "Erreur lors de la récupération des posts : $exception")
                isRefreshing = false
            }
    }

    // Chargement initial
    LaunchedEffect(Unit) {
        loadPosts()
    }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    Scaffold(
        modifier = Modifier.fillMaxSize() ,
        containerColor = Color.White ,
        bottomBar = {
            VintedBottomBar(navController , VintedScreen.MonCompte)
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState ,
            onRefresh = { loadPosts() } ,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Recommandations" , style = MaterialTheme.typography.headlineSmall)
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                        modifier = Modifier.fillMaxWidth() ,
                    ) {
                        items(posts.take(10)) { post ->
                            Card(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(350.dp) ,
                                elevation = CardDefaults.cardElevation(4.dp) ,
                                onClick = {
                                    navController.navigate("${VintedScreen.ArticleDetail.name}/${post.id}")
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp) ,
                                    verticalArrangement = Arrangement.Top ,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (post.photos.isNotEmpty()) {
                                        Image(
                                            painter = rememberImagePainter(post.photos[0]) ,
                                            contentDescription = "Post Image" ,
                                            modifier = Modifier
                                                .width(150.dp)
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(4.dp)) ,
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (post.title.isNotEmpty()) {
                                        Text(
                                            text = post.title ,
                                            textAlign = TextAlign.Center ,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    if (post.taille.isNotEmpty()) {
                                        Text(
                                            text = post.taille ,
                                            textAlign = TextAlign.Center ,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    if (post.state.isNotEmpty()) {
                                        Text(
                                            text = post.state ,
                                            textAlign = TextAlign.Center ,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Text(
                                        text = "${post.price}€" ,
                                        textAlign = TextAlign.Center ,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Text(
                        "You might be interested in" ,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                items(posts.drop(10).take(displayCount)) { post ->
                    Card(
                        modifier = Modifier
                            .width(400.dp)
                            .height(410.dp) ,
                        elevation = CardDefaults.cardElevation(4.dp) ,
                        onClick = {
                            navController.navigate("${VintedScreen.ArticleDetail.name}/${post.id}")
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp) ,
                            verticalArrangement = Arrangement.Top ,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (post.photos.isNotEmpty()) {
                                Image(
                                    painter = rememberImagePainter(post.photos[0]) ,
                                    contentDescription = "Post Image" ,
                                    modifier = Modifier
                                        .width(380.dp)
                                        .height(300.dp)
                                        .clip(RoundedCornerShape(10.dp)) ,
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (post.title.isNotEmpty()) {
                                Text(
                                    text = post.title ,
                                    textAlign = TextAlign.Center ,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (post.taille.isNotEmpty()) {
                                Text(
                                    text = post.taille ,
                                    textAlign = TextAlign.Center ,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (post.state.isNotEmpty()) {
                                Text(
                                    text = post.state ,
                                    textAlign = TextAlign.Center ,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Text(
                                text = "${post.price}€" ,
                                textAlign = TextAlign.Center ,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    Button(
                        onClick = { displayCount += 10 } ,
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
}
