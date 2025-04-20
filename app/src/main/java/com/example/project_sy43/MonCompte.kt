package com.example.project_sy43

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

class MonCompte : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.White,
                bottomBar = {
                    BottomAppBar(
                        //série d'icône placé à gauche
                        actions = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ){
                                onglet(Icons.Filled.Home, "Home", "Home icon", Color(0xFF007782), MonCompte::class.java)
                                onglet(Icons.Filled.Search, "Search", "Search icon", Color.Black, Sell::class.java)//A CHANGER
                                onglet(Icons.Filled.AddCircleOutline, "Sell", "Sell icon", Color.Black, Sell::class.java)//A CHANGER
                                onglet(Icons.Filled.MailOutline, "Message", "Message icon", Color.Black, Sell::class.java)//A CHANGER
                                onglet(Icons.Filled.PersonOutline, "Profile", "Profil icon", Color.Black, MainActivity::class.java)//A CHANGER
                            }
                        }
                    )
                }

            ) { innerPadding ->
                MonCompteScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }

    @Composable
    fun onglet(icon: ImageVector, text: String, description: String, color: Color, activityClass: Class<*>) {
        val context = LocalContext.current
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            IconButton(onClick = {
                val intent = Intent(context, activityClass)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = color
            )
        }
    }

    @Composable
    fun MonCompteScreen(modifier: Modifier = Modifier) {

    }
}