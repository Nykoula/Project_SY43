package com.example.project_sy43

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF007782)
            ){
                LoginScreen()
            }
        }
    }

    @Composable
    fun LoginScreen() {

        var username by remember { mutableStateOf("") }// État pour gérer l'entrée utilisateur
        var password by remember { mutableStateOf("") }
        val image = painterResource(R.drawable.baseline_account_circle_24)

        val context = LocalContext.current

        if (context is ComponentActivity) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp) // Sets the image size
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Username") },
                    shape = RoundedCornerShape(16.dp), // Définit des coins arrondis
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {

                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, // Couleur d'arrière-plan du bouton
                        contentColor = Color(0xFF007782)    // Couleur du texte
                    ), shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth() // Le bouton occupe toute la largeur
                )
                {
                    Text(
                        text = "Login"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        context.finish()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, // Couleur d'arrière-plan du bouton
                        contentColor = Color(0xFF007782)    // Couleur du texte
                    ), shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth() // Le bouton occupe toute la largeur
                )
                {
                    Text(
                        text = "Go back"
                    )
                }
            }
        }
    }
}