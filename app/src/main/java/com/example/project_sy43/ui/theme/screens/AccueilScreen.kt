package com.example.project_sy43.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Accueil(
    viewModel: ProductViewModel,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    val products = viewModel.products
    var lastPurchaseCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("historique").document(userId).get()
                .addOnSuccessListener { document ->
                    val lastPurchase = document.getString("lastPurchaseCategory")
                    lastPurchaseCategory = lastPurchase
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Button(
            onClick = {
                navController.navigate(VintedScreen.Login.name)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007782),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Connexion/Login"
            )
        }

        if (userId != null && lastPurchaseCategory != null) {
            // Afficher 5 vêtements aléatoires de la même catégorie que le dernier achat
            val categoryProducts = products.filter { it.category == lastPurchaseCategory }
            if (categoryProducts.isNotEmpty()) {
                val randomProducts = categoryProducts.shuffled().take(5)
                LazyRow {
                    items(randomProducts) { product ->
                        // Afficher chaque produit
                        Text(text = product.title)
                    }
                }
            }
        } else {
            // Afficher 2 autres catégories
            val categories = listOf("Category1", "Category2")
            categories.forEach { category ->
                val categoryProducts = products.filter { it.category == category }
                if (categoryProducts.isNotEmpty()) {
                    val randomProducts = categoryProducts.shuffled().take(5)
                    LazyRow {
                        items(randomProducts) { product ->
                            // Afficher chaque produit
                            Text(text = product.title)
                        }
                    }
                }
            }
        }

        // Afficher la catégorie "recommandé"
        val recommendedProducts = products.shuffled().take(30)
        LazyRow {
            items(recommendedProducts) { product ->
                // Afficher chaque produit
                Text(text = product.title)
            }
        }
    }
}
