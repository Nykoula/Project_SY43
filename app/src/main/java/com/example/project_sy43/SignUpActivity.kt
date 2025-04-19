package com.example.project_sy43

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.White,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007782),
                            titleContentColor = Color.White,
                        ),
                        title = { Text("Sign Up") },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (context is ComponentActivity) {
                                    context.finish()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Go back",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                SignUpScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }

    @Composable
    fun SignUpScreen(modifier: Modifier = Modifier) {
        var lastName by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var influencer by remember { mutableStateOf("") }
        //var dateCreation by remember { mutableStateOf("") }
        val calendar = Calendar.getInstance() // Récupère la date et l'heure actuelles
        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // Format désiré : "2025-04-19"
        val currentDate = dateFormat.format(calendar.time) // Convertit en chaîne formatée

        var status by remember { mutableStateOf("") }
        val context = LocalContext.current
        val authentification = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        println("Firebase connecté avec succès : ${authentification.app?.name}")

        if (context is ComponentActivity) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()) // Permet de scroller si le contenu dépasse
                    .padding(top = 80.dp) // padding de la top bar
                    .padding(16.dp), // padding local
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Last Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text(text = "First Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Age") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Email") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Password") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Phone Number") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Address") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Étape 1 : Créer l'utilisateur avec Firebase Authentication
                        authentification.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Étape 2 : Stocker les informations supplémentaires dans Firestore
                                    val userId = authentification.currentUser?.uid ?: return@addOnCompleteListener
                                    val user = mapOf(
                                        "idPerson" to userId,
                                        "lastName" to lastName,
                                        "firstName" to firstName,
                                        "age" to age,
                                        "email" to email,
                                        "password" to password,
                                        "phoneNumber" to phoneNumber,
                                        "address" to address,
                                        "dateCreation" to currentDate
                                    )

                                    db.collection("Person").document(userId)
                                        .set(user)
                                        .addOnSuccessListener {
                                            // Rediriger vers la page de connexion
                                            context.finish()
                                        }
                                        .addOnFailureListener { exception ->
                                            // Gérer l'erreur
                                            println("Erreur lors de l'enregistrement : ${exception.message}")
                                        }
                                        Toast.makeText(context, "Utilisateur enregistré avec succès !", Toast.LENGTH_LONG).show()

                                } else {
                                    // Afficher un message d'erreur
                                    println("Erreur : ${task.exception?.message}")
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()

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
                        text = "Register"
                    )
                }
            }
        }
    }
}