package com.example.project_sy43.ui.theme.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.BottomAppBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun VintedBottomBar(
    navController: NavController,
    currentScreen: VintedScreen
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            onglet(Icons.Filled.Home, "Home", VintedScreen.MonCompte, navController, currentScreen)
            onglet(Icons.Filled.Search, "Search", VintedScreen.Search, navController, currentScreen)
            onglet(Icons.Filled.AddCircleOutline, "Sell", VintedScreen.Sell, navController, currentScreen)
            onglet(Icons.Filled.MailOutline, "Messages", VintedScreen.Messages, navController, currentScreen)
            onglet(Icons.Filled.PersonOutline, "Profile", VintedScreen.Profile, navController, currentScreen)
        }
    }
}

@Composable
fun onglet(
    icon: ImageVector,
    text: String,
    destination: VintedScreen,
    navController: NavController,
    currentScreen: VintedScreen
) {
    val isSelected = destination == currentScreen
    val color = if (isSelected) Color(0xFF007782) else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = {
            navController.navigate(destination.name) {
                // éviter d’empiler plusieurs fois la même page
                popUpTo(VintedScreen.Accueil.name)
                launchSingleTop = true
            }
        }) {
            Icon(
                imageVector = icon,
                contentDescription = "$text icon",
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, color = color)
    }
}