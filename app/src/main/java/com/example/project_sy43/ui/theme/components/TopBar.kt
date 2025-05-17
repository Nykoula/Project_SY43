package com.example.project_sy43.ui.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen

@OptIn(ExperimentalMaterial3Api::class)//pour la top bar
@Composable
fun VintedTopBar(
    title: String,
    //onBackClick: (() -> Unit)? = null // si null, pas d'icône
    navController: NavController,
    canGoBack: Boolean = true
) {

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF007782),
            titleContentColor = Color.White,
        ),
        title = { Text(title) },
        navigationIcon = {
//            onBackClick?.let {//redirige vers l'écran de connexion -> petit problème
//                IconButton(onClick = it) {
//                    Icon(
//                        imageVector = Icons.Filled.Close,
//                        contentDescription = "Go back",
//                        tint = Color.White
//                    )
//                }
//            }
            if (canGoBack) {
                IconButton(onClick = {
                    // Navigation vers "mon_compte"
//                    navController.navigate(VintedScreen.MonCompte.name) {
//                        // Optionnel : enlève l'écran actuel de la pile
//                        popUpTo(navController.currentDestination?.route ?: "") { inclusive = true }
//                    }
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }
            }
        }
    )
}