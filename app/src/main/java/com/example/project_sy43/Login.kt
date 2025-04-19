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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.google.firebase.auth.FirebaseAuth

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ){
                LoginScreen()
            }
        }
    }

    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }// État pour gérer l'entrée utilisateur
        var password by remember { mutableStateOf("") }
        val image = painterResource(R.drawable.baseline_account_circle_24)
        var isPasswordVisible by remember { mutableStateOf(false) } // État pour basculer visibilité
        var errorMessage by remember { mutableStateOf("") }
        val authentification = FirebaseAuth.getInstance()

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
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFF007782)
                        )
                    },
                    placeholder = { Text("Username") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock, // Icône de cadenas
                            contentDescription = "Password Icon",
                            tint = Color(0xFF007782)
                        )
                    },
                    placeholder = { Text("Password") },
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible } // Bascule visibilité
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = Color.Gray
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(), // Texte masqué ou non
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Forgot password ?")

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Étape 1 : Vérifier que les champs ne sont pas vides
                        if (email.isEmpty() || password.isEmpty()) {
                            errorMessage = "Veuillez compléter les champs requis."
                            return@Button
                        }

                        // Étape 2 : Vérifier l'authentification Firebase
                        authentification.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Redirection vers la page Mon Compte
                                    errorMessage = "" // Réinitialise le message d'erreur
                                    val intent = Intent(context, MonCompte::class.java)
                                    context.startActivity(intent)
                                    context.finish()
                                } else {
                                    // Email ou mot de passe invalide
                                    errorMessage = "Email ou mot de passe invalide."
                                }
                            }
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
                Spacer(modifier = Modifier.height(16.dp))
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