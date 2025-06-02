package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Setting(
    navController: NavController,
    onCancel: () -> Unit
){
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            VintedTopBar(title = "Paramètres", navController, true)
        },
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.Profile)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Section Sécurité
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Sécurité",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007782),
                        modifier = Modifier.padding(16.dp)
                    )

                    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))

                    // Modifier le mot de passe
                    SettingMenuItem(
                        icon = Icons.Outlined.Lock,
                        title = "Modifier le mot de passe",
                        subtitle = "Changer votre mot de passe",
                        onClick = {
                            navController.navigate(VintedScreen.UpdatePassword.name)
                        }
                    )
                }
            }

            // Section Notifications
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007782),
                        modifier = Modifier.padding(16.dp)
                    )

                    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))

                    // Gérer les notifications
                    SettingMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Gérer les notifications",
                        subtitle = "Personnaliser vos préférences de notification",
                        onClick = {
                            navController.navigate(VintedScreen.NotificationSettings.name)
                        }
                    )
                }
            }

            // Section Compte
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Compte",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007782),
                        modifier = Modifier.padding(16.dp)
                    )

                    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))

                    // Supprimer le compte
                    SettingMenuItem(
                        icon = Icons.Outlined.Delete,
                        title = "Supprimer le compte",
                        subtitle = "Supprimer définitivement votre compte",
                        onClick = {
                            showDeleteDialog = true
                        },
                        isDestructive = true
                    )
                }
            }
        }
    }

    // Dialog de confirmation pour la suppression
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Supprimer le compte",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible et toutes vos données seront perdues.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteUserAccount(navController)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun SettingMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val textColor = if (isDestructive) Color.Red else Color.Black
    val iconColor = if (isDestructive) Color.Red else Color(0xFF007782)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.padding(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDestructive) Color.Red.copy(alpha = 0.7f) else Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun deleteUserAccount(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    currentUser?.let { user ->
        val userId = user.uid

        // Supprimer les données utilisateur de Firestore
        db.collection("Person").document(userId).delete()
            .addOnSuccessListener {
                // Supprimer le compte d'authentification
                user.delete()
                    .addOnSuccessListener {
                        // Rediriger vers l'écran d'accueil
                        navController.navigate(VintedScreen.Accueil.name) {
                            popUpTo(0) { inclusive = true }
                        }
                    }

            }

    }
}