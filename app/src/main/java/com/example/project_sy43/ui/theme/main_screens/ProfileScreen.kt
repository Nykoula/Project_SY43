package com.example.project_sy43.ui.theme.main_screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.Image

@Composable
fun Profile(
    personViewModel: PersonViewModel = viewModel(),
    navController: NavController,
    onCancel: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    var userEmail by remember { mutableStateOf(currentUser?.email ?: "Email non disponible") }
    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf(0) }
    var userAddress by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userDate by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(userId) {
        userId?.let { uid ->
            db.collection("Person").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userFirstName = document.getString("firstName") ?: "Prénom non renseigné"
                        userLastName = document.getString("lastName") ?: "Nom non renseigné"
                        userAge = document.getLong("age")?.toInt() ?: 0
                        userAddress = document.getString("address") ?: "Adresse non renseignée"
                        userPhone = document.getString("phoneNumber") ?: "Téléphone non renseigné"
                        userDate = document.getString("dateOfBirth") ?: "Date de naissance non renseigné"
                        userEmail = document.getString("email") ?: currentUser?.email ?: "Email non disponible"
                        userPhotoUrl = document.getString("photo") ?: ""
                    }
                }
                .addOnFailureListener {
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
        topBar = { VintedTopBar(title = "Profil", navController, false) },
        bottomBar = { VintedBottomBar(navController, VintedScreen.Profile) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .clickable {
                                    navController.navigate("FlappyBirdGames")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (userPhotoUrl.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(userPhotoUrl),
                                    contentDescription = "Photo de profil",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = "Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
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

                    ProfileInfoRow(Icons.Outlined.Person, "Prénom", userFirstName)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Person, "Nom", userLastName)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Email, "Email", userEmail)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Cake, "Âge", "$userAge ans")
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Home, "Adresse", userAddress)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Phone, "Téléphone", userPhone)
                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { navController.navigate(VintedScreen.UpdateProfile.name) },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF007782))
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Modifier", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Modifier profil", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuItem(Icons.Outlined.AccountCircle, "Mon dressing", "Voir mes articles en vente") {
                        navController.navigate(VintedScreen.Dressing.name)
                    }
                    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(Icons.Outlined.Settings, "Modifications du compte", "Modifier mon compte") {
                        navController.navigate(VintedScreen.Setting.name)
                    }
                    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(Icons.Outlined.Settings, "Paramètres", "Gérer les préférences") {
                        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            }

            Button(
                onClick = {
                    personViewModel.logout()
                    navController.navigate(VintedScreen.Accueil.name) {
                        popUpTo(VintedScreen.MonCompte.name) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.1f),
                    contentColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Se déconnecter", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
    }
}

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
        Icon(icon, contentDescription = label, tint = Color(0xFF007782), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
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
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = Color(0xFF007782), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.padding(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = "Arrow", tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}
