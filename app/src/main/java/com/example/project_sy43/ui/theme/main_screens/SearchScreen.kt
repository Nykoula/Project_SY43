package com.example.project_sy43.ui.theme.main_screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_sy43.model.Product
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun Search(
    navController: NavController,
    onCancel: () -> Unit,
    sellViewModel: SellViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var filterPriceAsc by remember { mutableStateOf(false) }
    var filterDateAsc by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val keyboardController = LocalSoftwareKeyboardController.current

    Log.d("SearchScreen", "Search screen initialized")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            VintedTopBar(title = "Search", navController, true)
        },
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.Search)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        Log.d("SearchScreen", "Search initiated with query: $searchQuery")
                        keyboardController?.hide()
                        if (searchQuery.isNotEmpty()) {
                            isLoading = true
                            performSearch(db, searchQuery, filterPriceAsc, filterDateAsc, sellViewModel) {
                                isLoading = false
                                Log.d("SearchScreen", "Search completed")
                            }
                        }
                        Log.d("SearchScreen", "Search initiated")
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        Log.d("SearchScreen", "Filter by Type button clicked")
                        navController.navigate(VintedScreen.TypeClothe.name)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Filter by Type")
                }

                Button(
                    onClick = {
                        filterPriceAsc = !filterPriceAsc
                        Log.d("SearchScreen", "Price filter toggled: ${if (filterPriceAsc) "Low to High" else "High to Low"}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterPriceAsc) "Price: Low to High" else "Price: High to Low")
                }

                Button(
                    onClick = {
                        filterDateAsc = !filterDateAsc
                        Log.d("SearchScreen", "Date filter toggled: ${if (filterDateAsc) "Old to New" else "New to Old"}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterDateAsc) "Date: Old to New" else "Date: New to Old")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sellViewModel.searchResults.value) { item ->
                        PostItemsGrid(items = listOf(item), navController = navController)
                    }
                }
            }
        }
    }
}

fun performSearch(
    db: FirebaseFirestore,
    searchQuery: String,
    filterPriceAsc: Boolean,
    filterDateAsc: Boolean,
    sellViewModel: SellViewModel,
    onComplete: () -> Unit
) {
    Log.d("SearchFunction", "Starting search with query: $searchQuery")

    var query = db.collection("Post")
        .orderBy("title")
        .startAt(searchQuery)
        .endAt(searchQuery + "\uf8ff")

    query.get()
        .addOnSuccessListener { documents ->
            Log.d("SearchFunction", "Search successful, ${documents.size()} documents found")
            val results = documents.mapNotNull { document ->
                try {
                    val viewModel = SellViewModel().apply {
                        productTitle.value = document.getString("title") ?: ""
                        productPrice.value = document.getDouble("price")?.toString() ?: ""
                        productDescription.value = document.getString("description") ?: ""
                        productId.value = document.id

                        val dateCreationValue = document.get("dateCreation")
                        dateCreation.value = when (dateCreationValue) {
                            is com.google.firebase.Timestamp -> dateCreationValue.toDate().toString()
                            is String -> dateCreationValue
                            else -> ""
                        }

                        val photos = document.get("photos") as? List<String> ?: emptyList()
                        setProductPhotoUris(photos.map { Uri.parse(it) })
                        Log.d("SearchFunction", "Test pass")
                    }
                    viewModel
                } catch (e: Exception) {
                    Log.e("SearchFunction", "Error mapping document to SellViewModel", e)
                    null
                }
            }
            Log.d("SearchFunction", "Results size: ${results.size}")
            sellViewModel.setSearchResults(results)
            Log.d("SearchFunction", "Search results set")
            onComplete()
            Log.d("SearchFunction", "Search completed 1")
        }
        .addOnFailureListener { exception ->
            Log.e("SearchFunction", "Error fetching documents", exception)
            onComplete()
        }
    Log.d("SearchFunction", "Search completed 2")
}


@Composable
fun PostItemsGrid(items: List<SellViewModel>, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Deux colonnes
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items) { item ->
            PostItem(item = item, navController = navController)
        }
    }
}

@Composable
fun PostItem(item: SellViewModel, navController: NavController) {
    Log.d("PostItem", "Rendering post item: ${item.productTitle.value}")

    val photoUrls = item.productPhotoUri.value.map { it.toString() }
    Log.d("PostItem", "Photo URLs: $photoUrls")

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.75f), // Ajustez l'aspect ratio selon vos besoins
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            navController.navigate("${VintedScreen.ArticleDetail.name}/${item.productId.value}")
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = item.productTitle.value, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))

            if (photoUrls.isNotEmpty()) {
                PhotoCarousel(photos = photoUrls)
            } else {
                Log.d("PostItem", "No photos to display")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Price: ${item.productPrice.value}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Description: ${item.productDescription.value}", style = MaterialTheme.typography.bodySmall)
        }
    }
}