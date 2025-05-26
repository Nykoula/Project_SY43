package com.example.project_sy43.ui.theme.components

import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun VintedBottomBar(
    navController: NavController,
    currentScreen: VintedScreen
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            onglet(Icons.Filled.Home, "Home", VintedScreen.MonCompte, navController, currentScreen)
            onglet(Icons.Filled.Search, "Search", VintedScreen.Search, navController, currentScreen, currentUser)
            onglet(Icons.Filled.AddCircleOutline, "Sell", VintedScreen.Sell, navController, currentScreen, currentUser)
            onglet(Icons.Filled.MailOutline, "Messages", VintedScreen.Messages, navController, currentScreen, currentUser)
            onglet(Icons.Filled.PersonOutline, "Profile", VintedScreen.Profile, navController, currentScreen, currentUser)
        }
    }
}

@Composable
fun onglet(
    icon: ImageVector,
    text: String,
    destination: VintedScreen,
    navController: NavController,
    currentScreen: VintedScreen,
    currentUser: FirebaseUser? = null
) {
    val context = LocalContext.current
    val isSelected = destination == currentScreen
    val color = if (isSelected) Color(0xFF007782) else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = {
            if (currentUser != null || destination == VintedScreen.MonCompte) {
                navController.navigate(destination.name) {
                    popUpTo(VintedScreen.Accueil.name)
                    launchSingleTop = true
                }
            } else {
                navController.navigate(VintedScreen.Login.name) {
                    popUpTo(VintedScreen.Accueil.name)
                    Toast.makeText(context, "You have to be connected to use this feature", Toast.LENGTH_LONG).show()
                    launchSingleTop = true
                }
            }
        }) {
            Icon(
                imageVector = icon,
                contentDescription = "$text icon",
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(text = text, color = color)
    }
}
