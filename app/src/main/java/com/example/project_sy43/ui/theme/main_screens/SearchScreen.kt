package com.example.project_sy43.ui.theme.main_screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel
import com.example.project_sy43.viewmodel.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Search(
    navController: NavController ,
    searchViewModel: SellViewModel = viewModel() ,
    sharedViewModel: SharedViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var filterPriceAsc by remember { mutableStateOf(false) }
    var filterDateAsc by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedType by sharedViewModel.selectedType

    Log.d("SearchScreen" , "Search screen initialized with selected type: $selectedType")

    LaunchedEffect(selectedType) {
        if (selectedType.isNotEmpty() && searchQuery.isNotEmpty()) {
            Log.d("SearchScreen" , "Triggered search after type selection: $selectedType")
            isLoading = true
            performSearch(
                db ,
                searchQuery ,
                filterPriceAsc ,
                filterDateAsc ,
                filterCategory = selectedType ,
                sellViewModel = searchViewModel
            ) {
                isLoading = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize() ,
        containerColor = Color.White ,
        topBar = {
            VintedTopBar(title = "Search" , navController = navController , canGoBack = true)
        } ,
        bottomBar = {
            VintedBottomBar(navController = navController , currentScreen = VintedScreen.Search)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery ,
                onValueChange = { searchQuery = it } ,
                label = { Text("Search by title") } ,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search ,
                        contentDescription = "Search" ,
                        modifier = Modifier.clickable {
                            Log.d("SearchScreen" , "Search initiated with query: $searchQuery")
                            keyboardController?.hide()
                            if (searchQuery.isNotEmpty()) { // Fais la requête et appelle la fonction de recherche
                                isLoading = true
                                performSearch(
                                    db ,
                                    searchQuery ,
                                    filterPriceAsc ,
                                    filterDateAsc ,
                                    filterCategory = "" ,
                                    searchViewModel
                                ) {
                                    isLoading = false
                                    Log.d("SearchScreen" , "Search completed")
                                }
                            }
                        }
                    )
                } ,
                modifier = Modifier.fillMaxWidth() ,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text ,
                    imeAction = ImeAction.Search
                ) ,
                keyboardActions = KeyboardActions(
                    onSearch = {
                        Log.d("SearchScreen" , "Search initiated with query: $searchQuery")
                        keyboardController?.hide()
                        if (searchQuery.isNotEmpty()) {
                            isLoading = true
                            performSearch(
                                db ,
                                searchQuery ,
                                filterPriceAsc ,
                                filterDateAsc ,
                                filterCategory = "" ,
                                searchViewModel
                            ) {
                                isLoading = false
                                Log.d("SearchScreen" , "Search completed")
                            }
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth() ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        Log.d("SearchScreen" , "Filter by Type button clicked")
                        navController.navigate("CategorySelectionScreen")
                        selectedType = searchViewModel.selectedType.value
                        if (searchQuery.isNotEmpty()) {
                            isLoading = true
                            performSearch(
                                db ,
                                searchQuery ,
                                filterPriceAsc ,
                                filterDateAsc ,
                                searchViewModel.selectedType.value ,
                                searchViewModel
                            ) {
                                isLoading = false
                                Log.d("SearchScreen" , "Search completed")
                            }
                        }
                    } ,
                    modifier = Modifier.weight(1f)
                ) {
                    Log.d("FilterSearch" , "Selected Type: $selectedType")
                    Text(text = "Selected Type: ${selectedType ?: "None"}")
                }
                Button(
                    onClick = {
                        filterPriceAsc = !filterPriceAsc
                        Log.d(
                            "SearchScreen" ,
                            "Price filter toggled: ${if (filterPriceAsc) "Low to High" else "High to Low"}"
                        )
                        if (searchQuery.isNotEmpty()) {
                            isLoading = true
                            performSearch(
                                db ,
                                searchQuery ,
                                filterPriceAsc ,
                                filterDateAsc ,
                                filterCategory = "" ,
                                searchViewModel
                            ) {
                                isLoading = false
                                Log.d("SearchScreen" , "Search completed")
                            }
                        }
                    } ,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterPriceAsc) "Price: Low to High" else "Price: High to Low")
                }
                Button(
                    onClick = {
                        filterDateAsc = !filterDateAsc
                        Log.d(
                            "SearchScreen" ,
                            "Date filter toggled: ${if (filterDateAsc) "Old to New" else "New to Old"}"
                        )
                        if (searchQuery.isNotEmpty()) {
                            isLoading = true
                            performSearch(
                                db ,
                                searchQuery ,
                                filterPriceAsc ,
                                filterDateAsc ,
                                filterCategory = "" ,
                                searchViewModel
                            ) {
                                isLoading = false
                                Log.d("SearchScreen" , "Search completed")
                            }
                        }
                    } ,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterDateAsc) "Date: Old to New" else "Date: New to Old")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (searchViewModel.searchResults.isNotEmpty()) {
                    PostItemsGrid(
                        viewModels = searchViewModel.searchResults ,
                        navController = navController
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                        Text("Aucun résultat trouvé")
                    }
                }
            }
        }
    }
}

// Fonction utilitaire pour convertir le format de date personnalisé en Date
fun parseCustomDateFormat(dateString: String): Date? {
    return try {
        // Format: "2025-06-22-11-42-53"
        val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss" , Locale.getDefault())
        formatter.parse(dateString)
    } catch (e: Exception) {
        Log.e("DateParsing" , "Error parsing date: $dateString" , e)
        null
    }
}

// Fonction pour rechercher et mettre les filtres
fun performSearch(
    db: FirebaseFirestore ,
    searchQuery: String ,
    filterPriceAsc: Boolean ,
    filterDateAsc: Boolean ,
    filterCategory: String ,
    sellViewModel: SellViewModel ,
    onComplete: () -> Unit
) {
    val formattedSearchQuery = formatTitle(searchQuery)
    var query = db.collection("Post")
        .orderBy("title")
        .startAt(formattedSearchQuery)
        .endAt("$formattedSearchQuery\uf8ff")

    query.get()
        .addOnSuccessListener { documents ->
            Log.d("SearchFunction" , "Number of documents retrieved: ${documents.size()}")
            val results = documents.mapNotNull { document ->
                try {
                    val title = document.getString("title") ?: ""
                    val formattedTitle = formatTitle(title)
                    val isAvailable = document.getBoolean("available") != false

                    if (formattedTitle.contains(formattedSearchQuery) && isAvailable) {
                        val vm = SellViewModel()

                        vm.setProductTitle(title)
                        vm.setProductPrice(document.getDouble("price")?.toString() ?: "0.0")
                        vm.setProductDescription(document.getString("description") ?: "")
                        vm.setProductState(document.getString("state") ?: "")
                        vm.productId.value = document.id
                        vm.setdateCreation(document.getString("dateCreation") ?: "")

                        val photoUrls = document.get("photos") as? List<String> ?: emptyList()
                        vm.setProductPhotoUrls(photoUrls)

                        Log.d(
                            "performSearch" ,
                            "Post ${document.id} loaded with ${photoUrls.size} photos"
                        )

                        vm
                    } else null
                } catch (e: Exception) {
                    Log.e("SearchFunction" , "Error mapping document to SellViewModel" , e)
                    null
                }
            }

            // Tri avec prix prioritaire, puis date
            val filteredResults = results.sortedWith(
                compareBy<SellViewModel> {
                    if (filterPriceAsc) it.productPrice.value.toDoubleOrNull() ?: 0.0
                    else -(it.productPrice.value.toDoubleOrNull() ?: 0.0)
                }.thenBy {
                    if (filterDateAsc) parseCustomDateFormat(it.dateCreation.value) ?: Date(0)
                    else parseCustomDateFormat(it.dateCreation.value)?.let { date -> Date(Long.MAX_VALUE - date.time) }
                        ?: Date(Long.MAX_VALUE)
                }
            )

            Log.d("SearchFunction" , "Number of results after filtering: ${filteredResults.size}")
            sellViewModel.setSearchResults(filteredResults)
            onComplete()
        }
        .addOnFailureListener { exception ->
            Log.e("SearchFunction" , "Error fetching documents" , exception)
            onComplete()
        }
}


@Composable
fun PostItemsGrid(viewModels: List<SellViewModel> , navController: NavController) {
    if (viewModels.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2) ,
            modifier = Modifier.fillMaxSize() ,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(viewModels.size) { index ->
                PostItem(item = viewModels[index] , navController = navController)
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
            Text("Aucun article à afficher")
        }
    }
}

@Composable
fun PostItem(item: SellViewModel , navController: NavController) {
    Log.d("PostItem" , "Rendering post item: ${item.productTitle.value}")
    val photoUrls = item.productPhotoUrls.value.map { it.toString() }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.75f) ,
        elevation = CardDefaults.cardElevation(4.dp) ,
        onClick = {
            navController.navigate("${VintedScreen.ArticleDetail.name}/${item.productId.value}")
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = item.productTitle.value , style = MaterialTheme.typography.bodyMedium)
            Text(text = "${item.productPrice.value} €" , style = MaterialTheme.typography.bodySmall)
            Text(text = "${item.selectedState.value}" , style = MaterialTheme.typography.bodySmall)

            if (photoUrls.isNotEmpty()) {
                PhotoCarousel(photos = photoUrls)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.LightGray) ,
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image Available" , color = Color.Gray)
                }
            }
            Log.d(
                "SearchFunction" ,
                "state and price : ${item.selectedState.value} ${item.productPrice.value}"
            )
        }
    }
}