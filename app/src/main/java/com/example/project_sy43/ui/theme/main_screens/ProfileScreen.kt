package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Profile(
    personViewModel: PersonViewModel = viewModel(),
    navController: NavController,
    onCancel: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // États pour les informations utilisateur
    var userEmail by remember { mutableStateOf(currentUser?.email ?: "Email non disponible") }
    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf(0) }
    var userAddress by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userDate by remember { mutableStateOf("") }

    // Charger les informations utilisateur depuis Firestore
    LaunchedEffect(userId) {
        userId?.let { uid ->
            // ✅ Utilisation de la collection "Person" avec l'ID utilisateur
            db.collection("Person").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userFirstName = document.getString("firstName") ?: "Prénom non renseigné"
                        userLastName = document.getString("lastName") ?: "Nom non renseigné"
                        userAge = document.getLong("age")?.toInt() ?: 0
                        userAddress = document.getString("address") ?: "Adresse non renseignée"
                        userPhone = document.getString("phoneNumber") ?: "Téléphone non renseigné" // ✅ phoneNumber
                        userDate = document.getString("dateOfBirth") ?: "Date de naissance non renseigné"
                        // Email vient déjà de Firebase Auth
                        userEmail = document.getString("email") ?: currentUser?.email ?: "Email non disponible"
                    }
                }
                .addOnFailureListener { exception ->
                    // Fallback sur PersonViewModel si erreur
                    userFirstName = personViewModel.person?.firstName ?: "Prénom non disponible"
                    userLastName = personViewModel.person?.lastName ?: "Nom non disponible"
                    userAge = 0
                    userAddress = "Adresse non disponible"
                    userPhone = "Téléphone non disponible"
                    userDate = "Date de naissance non disponible"
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            VintedTopBar(title = "Profil", navController, false)
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

            // Section profil utilisateur
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Avatar et nom
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar placeholder
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF007782))
                                .clickable {
                                    navController.navigate("FlappyBirdGames")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(12.dp))

                        Column {
                            Text(
                                text = "$userFirstName $userLastName",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007782)
                            )
                            Text(
                                text = "Membre Vinted",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Informations détaillées
                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Prénom",
                        value = userFirstName
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Nom",
                        value = userLastName
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = userEmail
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Cake,
                        label = "Âge",
                        value = "$userAge ans"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Home,
                        label = "Adresse",
                        value = userAddress
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Téléphone",
                        value = userPhone
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bouton modifier profil
                    TextButton(
                        onClick = {
                            navController.navigate(VintedScreen.UpdateProfile.name)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF007782)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Modifier",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = "Modifier profil",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Section Actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Mon dressing
                    ProfileMenuItem(
                        icon = Icons.Outlined.AccountCircle,
                        title = "Mon dressing",
                        subtitle = "Voir mes articles en vente",
                        onClick = {
                            navController.navigate(VintedScreen.Dressing.name)
                        }
                    )

                    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))

                    // Paramètres
                    ProfileMenuItem(
                        icon = Icons.Outlined.Settings,
                        title = "Paramètres",
                        subtitle = "Gérer les préférences",
                        onClick = {
                            navController.navigate(VintedScreen.Setting.name)
                        }
                    )
                }
            }

            // Bouton de déconnexion
            Button(
                onClick = {
                    personViewModel.logout()
                    navController.navigate(VintedScreen.Accueil.name) {
                        popUpTo(VintedScreen.MonCompte.name) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.1f),
                    contentColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Se déconnecter",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ✅ Fonctions @Composable HORS de la fonction Profile
@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF007782),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
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

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}