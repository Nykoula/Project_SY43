package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.viewmodel.ProductViewModel

@Composable
fun Accueil(
    viewModel: ProductViewModel,
    navController: NavController
) {

    val products = viewModel.products
    //afficher des vêtements meme si on est pas connecté

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Button(
            onClick = {
                navController.navigate(VintedScreen.Login.name)
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007782), // Couleur d'arrière-plan du bouton
                contentColor = Color.White    // Couleur du texte
            ), shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth() // Le bouton occupe toute la largeur
        )
        {
            Text(
                text = "Connexion/Login"
            )
        }
    }
}