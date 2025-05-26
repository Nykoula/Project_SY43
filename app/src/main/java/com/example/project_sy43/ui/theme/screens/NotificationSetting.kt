package com.example.project_sy43.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun NotificationSetting(
    navController: NavController,
    onCancel: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // États pour les préférences de notification (par défaut toutes activées)
    var newMessagesNotifications by remember { mutableStateOf(true) }
    var vintedNewsEmails by remember { mutableStateOf(true) }
    var commercialEmails by remember { mutableStateOf(true) }

    // État pour la sauvegarde
    var isSaving by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    // Charger les préférences depuis Firestore
    LaunchedEffect(userId) {
        userId?.let { uid ->
            db.collection("NotificationPreferences").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        newMessagesNotifications = document.getBoolean("newMessages") ?: true
                        vintedNewsEmails = document.getBoolean("vintedNews") ?: true
                        commercialEmails = document.getBoolean("commercial") ?: true
                    }
                }
                .addOnFailureListener {
                    // En cas d'erreur, garder les valeurs par défaut
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            VintedTopBar(title = "Notifications", navController, true)
        },
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.Setting)
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

            // Section Notifications push
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Notifications push",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007782)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Recevez des notifications directement sur votre appareil",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Nouveaux messages
                    NotificationSettingItem(
                        icon = Icons.Outlined.Message,
                        title = "Nouveaux messages",
                        subtitle = "Être notifié lors de nouveaux messages",
                        isChecked = newMessagesNotifications,
                        onCheckedChange = { newMessagesNotifications = it }
                    )
                }
            }

            // Section Notifications par email
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Notifications par email",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007782)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Gérez vos préférences de réception d'emails",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Nouveautés Vinted
                    NotificationSettingItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Nouveautés Vinted",
                        subtitle = "Recevoir les actualités et nouveautés par email",
                        isChecked = vintedNewsEmails,
                        onCheckedChange = { vintedNewsEmails = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Communications commerciales
                    NotificationSettingItem(
                        icon = Icons.Outlined.Store,
                        title = "Communications commerciales",
                        subtitle = "Recevoir des offres promotionnelles par email",
                        isChecked = commercialEmails,
                        onCheckedChange = { commercialEmails = it }
                    )
                }
            }

            // Bouton de sauvegarde
            Button(
                onClick = {
                    saveNotificationPreferences(
                        userId = userId,
                        db = db,
                        newMessages = newMessagesNotifications,
                        vintedNews = vintedNewsEmails,
                        commercial = commercialEmails,
                        onSaving = { isSaving = it },
                        onSuccess = { saveSuccess = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007782),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                Text(
                    text = if (isSaving) "Sauvegarde..." else if (saveSuccess) "Sauvegardé ✓" else "Sauvegarder les préférences",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            // Message de succès
            if (saveSuccess) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    saveSuccess = false
                }
            }
        }
    }
}

@Composable
fun NotificationSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF007782),
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
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF007782),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

private fun saveNotificationPreferences(
    userId: String?,
    db: FirebaseFirestore,
    newMessages: Boolean,
    vintedNews: Boolean,
    commercial: Boolean,
    onSaving: (Boolean) -> Unit,
    onSuccess: () -> Unit
) {
    userId?.let { uid ->
        onSaving(true)

        val preferences = hashMapOf(
            "newMessages" to newMessages,
            "vintedNews" to vintedNews,
            "commercial" to commercial,
            "lastUpdated" to com.google.firebase.Timestamp.now()
        )

        db.collection("NotificationPreferences").document(uid)
            .set(preferences)
            .addOnSuccessListener {
                onSaving(false)
                onSuccess()
            }
            .addOnFailureListener {
                onSaving(false)
                // TODO: Gérer l'erreur de sauvegarde
            }
    }
}