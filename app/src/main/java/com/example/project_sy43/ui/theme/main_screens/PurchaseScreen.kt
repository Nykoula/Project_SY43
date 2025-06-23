package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar

@Composable
fun PurchaseScreenWithNegotiatedPrice(
    productId: String,
    negotiatedPrice: Double,
    navController: NavController,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            VintedTopBar(
                title = "Achat",
                navController = navController,
                canGoBack = true
            )
        },
        bottomBar = {
            VintedBottomBar(
                navController = navController,
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
                    navController = navController,
                    itemId = productId,
                    onCancel = onBackClick,
                    menuDeroulant = true,
                    topBar = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Prix négocié : ${negotiatedPrice}€",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    if (productId.isNotBlank()) {
                        db.collection("Post").document(productId)
                            .update("available", false)
                            .addOnSuccessListener {
                                navController.navigate(VintedScreen.MonCompte.name) {
                                    popUpTo(0)
                                }
                            }
                            .addOnFailureListener { e ->
                                android.util.Log.e("PurchaseScreen", "Erreur mise à jour disponibilité", e)
                            }
                    } else {
                        navController.navigate(VintedScreen.MonCompte.name) {
                            popUpTo(0)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Confirmer l'achat - ${negotiatedPrice}€")
            }
        }
    }
}

