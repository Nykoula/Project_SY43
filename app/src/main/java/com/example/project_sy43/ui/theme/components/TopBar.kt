package com.example.project_sy43.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen

@OptIn(ExperimentalMaterial3Api::class)//pour la top bar
@Composable
fun VintedTopBar(
    title: String,
    navController: NavController,
    canGoBack: Boolean = true, // si false, pas d'icône
    description: String = ""
) {

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF007782),
            titleContentColor = Color.White,
        ),
        /*Column(
            modifier = Modifier.fillMaxWidth()
        ){
            title = { Text(title) },
            Spacer(modifier = WatchEvent.Modifier.padding(8.dp))
            description = { Text(description) },
        }*/
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title)
                if (description.isNotEmpty()){
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },

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
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }
            }
        }
    )
}