package com.example.project_sy43.ui.theme.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun VintedBottomBar(
    navController: NavController ,
    currentScreen: VintedScreen
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth() ,
            verticalAlignment = Alignment.CenterVertically ,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            onglet(
                modifier = Modifier.weight(1f) ,
                icon = Icons.Filled.Home ,
                text = "Home" ,
                destination = VintedScreen.MonCompte ,
                navController = navController ,
                currentScreen = currentScreen
            )
            onglet(
                modifier = Modifier.weight(1f) ,
                icon = Icons.Filled.Search ,
                text = "Search" ,
                destination = VintedScreen.Search ,
                navController = navController ,
                currentScreen = currentScreen ,
                currentUser = currentUser
            )
            onglet(
                modifier = Modifier.weight(1f) ,
                icon = Icons.Filled.AddCircleOutline ,
                text = "Sell" ,
                destination = VintedScreen.Sell ,
                navController = navController ,
                currentScreen = currentScreen ,
                currentUser = currentUser
            )
            onglet(
                modifier = Modifier.weight(1f) ,
                icon = Icons.Filled.MailOutline ,
                text = "Inbox" ,
                destination = VintedScreen.Messages ,
                navController = navController ,
                currentScreen = currentScreen ,
                currentUser = currentUser
            )
            onglet(
                modifier = Modifier.weight(1f) ,
                icon = Icons.Filled.PersonOutline ,
                text = "Profile" ,
                destination = VintedScreen.Profile ,
                navController = navController ,
                currentScreen = currentScreen ,
                currentUser = currentUser
            )
        }
    }
}

@Composable
fun onglet(
    modifier: Modifier = Modifier ,
    icon: ImageVector ,
    text: String ,
    destination: VintedScreen ,
    navController: NavController ,
    currentScreen: VintedScreen ,
    currentUser: FirebaseUser? = null
) {
    val context = LocalContext.current
    val isSelected = destination == currentScreen
    val color = if (isSelected) Color(0xFF007782) else Color.Black

    Column(
        modifier = modifier ,
        horizontalAlignment = Alignment.CenterHorizontally ,
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
                    Toast.makeText(
                        context ,
                        "You have to be connected to use this feature" ,
                        Toast.LENGTH_LONG
                    ).show()
                    launchSingleTop = true
                }
            }
        }) {
            Icon(
                imageVector = icon ,
                contentDescription = "$text icon" ,
                tint = color ,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(text = text , color = color)
    }
}
