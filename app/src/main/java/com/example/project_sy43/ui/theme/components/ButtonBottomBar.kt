package com.example.project_sy43.ui.theme.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.BottomAppBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import com.example.project_sy43.navigation.VintedScreen
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.project_sy43.viewmodel.SellViewModel

@Composable
fun ButtonBottomBar(
    navController: NavController,
    currentScreen: VintedScreen,
    onSaveClicked: () -> Unit  // callback à appeler quand on clique
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // Centre son contenu automatiquement
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007782),
                        contentColor = Color.White
                    ),
                    onClick = {
                        // fonction appelée quand on clique sur le bouton
                        onSaveClicked()
                        // Retourner à l'écran précédent
                        navController.popBackStack()
                    }) {
                    Text(text = "Terminé")
                }
            }
        }
    }
}