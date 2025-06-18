package com.example.project_sy43.ui.theme.main_screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

@Composable
fun UpdatePassword(navController: NavController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var currentPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    fun validatePasswords(): Boolean {
        var isValid = true
        currentPasswordError = ""
        newPasswordError = ""
        confirmPasswordError = ""

        if (currentPassword.isEmpty()) {
            currentPasswordError = "Veuillez entrer votre mot de passe actuel"
            isValid = false
        }
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
        if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Veuillez confirmer votre mot de passe"
            isValid = false
        } else if (newPassword != confirmPassword) {
            confirmPasswordError = "Les mots de passe ne correspondent pas"
            isValid = false
        }
        return isValid
    }

    fun changePassword() {
        if (!validatePasswords()) return

        isLoading = true

        currentUser?.let { user ->
            val email = user.email
            if (email.isNullOrEmpty()) {
                isLoading = false
                Toast.makeText(context, "Impossible de récupérer l'email de l'utilisateur", Toast.LENGTH_LONG).show()
                return
            }

            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "Mot de passe modifié avec succès", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            Toast.makeText(context, "Erreur lors du changement de mot de passe: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        currentPasswordError = "Mot de passe actuel incorrect"
                    } else {
                        Toast.makeText(context, "Échec de la réauthentification: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
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
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            currentPasswordError = ""
                        },
                        label = { Text("Mot de passe actuel") },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
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

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newPasswordError = ""
                        },
                        label = { Text("Nouveau mot de passe") },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
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

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = ""
                        },
                        label = { Text("Confirmer le nouveau mot de passe") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
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
