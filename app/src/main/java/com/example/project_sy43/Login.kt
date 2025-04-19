package com.example.project_sy43

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                //color = Color(0xFF007782)
                color = Color.White
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
                    modifier = Modifier.size(120.dp), // Sets the image size
                    colorFilter = ColorFilter.tint(Color(0xFF007782))
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Username") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Password") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Forgot password ?")

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {

                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007782), // Couleur d'arrière-plan du bouton
                        contentColor = Color.White    // Couleur du texte
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
                        containerColor = Color(0xFF007782), // Couleur d'arrière-plan du bouton
                        contentColor = Color.White // Couleur du texte
                    ), shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth() // Le bouton occupe toute la largeur
                )
                {
                    Text(
                        text = "Go back"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Don't have an account ? "
                    )
                    Text(
                        text = "Sign up",
                        fontWeight = FontWeight.Bold, // Définit le texte en gras
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            // Naviguer vers la page d'inscription
                            val intent = Intent(context, SignUpActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}