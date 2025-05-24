package com.example.project_sy43.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UpdatePassword(navController: NavController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance() // ✅ Ajout de Firestore

    // États pour les champs
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // États pour la visibilité des mots de passe
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // États pour les erreurs
    var currentPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    // État pour le bouton de chargement
    var isLoading by remember { mutableStateOf(false) }

    // Fonction de validation
    fun validatePasswords(): Boolean {
        var isValid = true

        // Reset des erreurs
        currentPasswordError = ""
        newPasswordError = ""
        confirmPasswordError = ""

        // Vérifier que le mot de passe actuel n'est pas vide
        if (currentPassword.isEmpty()) {
            currentPasswordError = "Veuillez entrer votre mot de passe actuel"
            isValid = false
        }

        // Vérifier que le nouveau mot de passe n'est pas vide
        if (newPassword.isEmpty()) {
            newPasswordError = "Veuillez entrer un nouveau mot de passe"
            isValid = false
        } else if (newPassword.length < 6) {
            newPasswordError = "Le mot de passe doit contenir au moins 6 caractères"
            isValid = false
        } else if (newPassword == currentPassword) {
            newPasswordError = "Le nouveau mot de passe doit être différent de l'actuel"
            isValid = false
        }

        // Vérifier la confirmation
        if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Veuillez confirmer votre mot de passe"
            isValid = false
        } else if (newPassword != confirmPassword) {
            confirmPasswordError = "Les mots de passe ne correspondent pas"
            isValid = false
        }

        return isValid
    }

    // ✅ Fonction pour mettre à jour le mot de passe dans Firestore
    fun updatePasswordInFirestore(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        currentUser?.uid?.let { userId ->
            db.collection("Person").document(userId)
                .update("password", newPassword)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }

    // ✅ Fonction pour changer le mot de passe (Firebase Auth + Firestore)
    fun changePassword() {
        if (!validatePasswords()) return

        isLoading = true

        currentUser?.let { user ->
            // Ré-authentifier l'utilisateur avec son mot de passe actuel
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // Ré-authentification réussie, changer le mot de passe dans Firebase Auth
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            // ✅ Mot de passe mis à jour dans Firebase Auth, maintenant dans Firestore
                            updatePasswordInFirestore(
                                newPassword = newPassword,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Mot de passe modifié avec succès dans Auth et base de données", Toast.LENGTH_LONG).show()
                                    navController.popBackStack() // Retourner à l'écran précédent
                                },
                                onFailure = { exception ->
                                    isLoading = false
                                    Toast.makeText(context, "Mot de passe mis à jour dans Auth mais erreur Firestore: ${exception.message}", Toast.LENGTH_LONG).show()
                                    // Optionnel : revenir quand même car Auth est mis à jour
                                    navController.popBackStack()
                                }
                            )
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            Toast.makeText(context, "Erreur lors de la modification Auth: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            currentPasswordError = "Mot de passe actuel incorrect"
                        }
                        else -> {
                            Toast.makeText(context, "Erreur d'authentification: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            VintedTopBar(title = "Modifier le mot de passe", navController, true)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // Mot de passe actuel
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            currentPasswordError = "" // Reset l'erreur
                        },
                        label = { Text("Mot de passe actuel") },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (currentPasswordVisible) "Masquer" else "Afficher"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        isError = currentPasswordError.isNotEmpty()
                    )
                    if (currentPasswordError.isNotEmpty()) {
                        Text(
                            text = currentPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Nouveau mot de passe
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newPasswordError = "" // Reset l'erreur
                        },
                        label = { Text("Nouveau mot de passe") },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (newPasswordVisible) "Masquer" else "Afficher"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        isError = newPasswordError.isNotEmpty()
                    )
                    if (newPasswordError.isNotEmpty()) {
                        Text(
                            text = newPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Confirmation du nouveau mot de passe
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = "" // Reset l'erreur
                        },
                        label = { Text("Confirmer le nouveau mot de passe") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Masquer" else "Afficher"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmPasswordError.isNotEmpty()
                    )
                    if (confirmPasswordError.isNotEmpty()) {
                        Text(
                            text = confirmPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bouton de modification
                    Button(
                        onClick = { changePassword() },
                        enabled = !isLoading && currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007782),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isLoading) "Modification en cours..." else "Modifier le mot de passe",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }


                }
            }
        }
    }
}