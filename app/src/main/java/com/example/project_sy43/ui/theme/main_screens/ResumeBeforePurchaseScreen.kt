package com.example.project_sy43.ui.theme.main_screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.model.Product
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PurchaseScreenWithNegotiatedPrice(
    productId: String ,
    negotiatedPrice: Double ,
    navController: NavController
) {
    var clothing by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(productId) {
        productId?.let { id ->
            FirebaseFirestore.getInstance()
                .collection("Post")
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Convertir directement en data class
                        val item = document.toObject(Product::class.java)
                        clothing = item?.copy(id = document.id) // on met l'id du document
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ResumeBeforePurchase" , "Erreur Firestore" , e)
                }
        }
    }

    Scaffold(
        topBar = {
            VintedTopBar(
                title = "Achat" ,
                navController = navController ,
                canGoBack = true
            )
        } ,
        bottomBar = {
            VintedBottomBar(
                navController = navController ,
                currentScreen = VintedScreen.MonCompte
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Donne un poids à ClothingDetailView pour qu'il prenne l'espace restant
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ClothingDetailView(
                    navController = navController ,
                    itemId = productId ,
                    menuDeroulant = true ,
                    topBar = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Prix négocié : ${negotiatedPrice}€" ,
                style = MaterialTheme.typography.headlineMedium ,
                color = MaterialTheme.colorScheme.primary ,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("${VintedScreen.PurchaseScreen.name}/${productId}/${clothing?.title ?: ""}")
                } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp , vertical = 8.dp)
            ) {
                Text("Confirmer l'achat - ${negotiatedPrice}€")
            }
        }
    }
}

