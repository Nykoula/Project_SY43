package com.example.project_sy43.ui.theme.main_screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
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


/**
 * Composable function to display the user's profile screen.
 * It fetches user data from Firestore and displays profile information,
 * menu options, and a logout button.
 *
 * @param personViewModel ViewModel to manage person data and logout functionality.
 * @param navController Navigation controller to handle screen navigation.
 */
@Composable
fun Profile(
    personViewModel: PersonViewModel = viewModel() ,
    navController: NavController
) {
    // Get Firestore instance
    val db = FirebaseFirestore.getInstance()
    // Get current Firebase authenticated user
    val currentUser = FirebaseAuth.getInstance().currentUser
    // Get user ID if available
    val userId = currentUser?.uid

    // Mutable states to hold user profile data
    var userEmail by remember { mutableStateOf(currentUser?.email ?: "Email non disponible") }
    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf(0) }
    var userAddress by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userDate by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf("") }

    // Get current context for launching intents
    val context = LocalContext.current

    // Side effect to fetch user data from Firestore when userId changes
    LaunchedEffect(userId) {
        userId?.let { uid ->
            db.collection("Person").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Populate user data from Firestore document
                        userFirstName = document.getString("firstName") ?: "Prénom non renseigné"
                        userLastName = document.getString("lastName") ?: "Nom non renseigné"
                        userAge = document.getLong("age")?.toInt() ?: 0
                        userAddress = document.getString("address") ?: "Adresse non renseignée"
                        userPhone = document.getString("phoneNumber") ?: "Téléphone non renseigné"
                        userDate =
                            document.getString("dateOfBirth") ?: "Date de naissance non renseigné"
                        userEmail = document.getString("email") ?: currentUser?.email
                                ?: "Email non disponible"
                        userPhotoUrl = document.getString("photo") ?: ""
                    }
                }
                .addOnFailureListener {
                    // Fallback to ViewModel data or default values if Firestore fetch fails
                    userFirstName = personViewModel.person?.firstName ?: "Prénom non disponible"
                    userLastName = personViewModel.person?.lastName ?: "Nom non disponible"
                    userAge = 0
                    userAddress = "Adresse non disponible"
                    userPhone = "Téléphone non disponible"
                    userDate = "Date de naissance non disponible"
                }
        }
    }

    // Scaffold layout with top bar, bottom bar and content
    Scaffold(
        modifier = Modifier.fillMaxSize() ,
        containerColor = Color.White ,
        topBar = { VintedTopBar(title = "Profil" , navController , false) } ,
        bottomBar = { VintedBottomBar(navController , VintedScreen.Profile) }
    ) { innerPadding ->
        // Main content column with vertical scroll and padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp) ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card displaying user profile information
            Card(
                modifier = Modifier.fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) ,
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Row with user photo/avatar and name
                    Row(
                        modifier = Modifier.fillMaxWidth() ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Box for profile photo or default avatar icon
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray) ,
                            contentAlignment = Alignment.Center
                        ) {
                            if (userPhotoUrl.isNotEmpty()) {
                                // Load and display user photo from URL
                                Image(
                                    painter = rememberAsyncImagePainter(userPhotoUrl) ,
                                    contentDescription = "Photo de profil" ,
                                    modifier = Modifier.fillMaxSize() ,
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Display default avatar icon if no photo URL
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle ,
                                    contentDescription = "Avatar" ,
                                    tint = Color.White ,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(12.dp))

                        // Column with user full name and membership label
                        Column {
                            Text(
                                text = "$userFirstName $userLastName" ,
                                style = MaterialTheme.typography.headlineSmall ,
                                fontWeight = FontWeight.Bold ,
                                color = Color(0xFF007782)
                            )
                            Text(
                                text = "Membre Vinted" ,
                                style = MaterialTheme.typography.bodyMedium ,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display user profile details using ProfileInfoRow composable
                    ProfileInfoRow(Icons.Outlined.Person , "Prénom" , userFirstName)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Person , "Nom" , userLastName)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Email , "Email" , userEmail)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Cake , "Âge" , "$userAge ans")
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Home , "Adresse" , userAddress)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Outlined.Phone , "Téléphone" , userPhone)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Button to navigate to profile update screen
                    TextButton(
                        onClick = { navController.navigate(VintedScreen.UpdateProfile.name) } ,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF007782))
                    ) {
                        Icon(
                            Icons.Outlined.Edit ,
                            contentDescription = "Modifier" ,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            "Modifier profil" ,
                            style = MaterialTheme.typography.bodyMedium ,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Card displaying profile menu items
            Card(
                modifier = Modifier.fillMaxWidth() ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) ,
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Menu item to navigate to user's dressing (items for sale)
                    ProfileMenuItem(
                        Icons.Outlined.AccountCircle ,
                        "Mon dressing" ,
                        "Voir mes articles en vente"
                    ) {
                        navController.navigate(VintedScreen.Dressing.name)
                    }
                    Divider(
                        color = Color.LightGray ,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    // Menu item to navigate to account modification screen
                    ProfileMenuItem(
                        Icons.Outlined.Settings ,
                        "Modifications du compte" ,
                        "Modifier mon compte"
                    ) {
                        navController.navigate(VintedScreen.Setting.name)
                    }
                    Divider(
                        color = Color.LightGray ,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    // Menu item to open app settings in system settings
                    ProfileMenuItem(
                        Icons.Outlined.Settings ,
                        "Paramètres" ,
                        "Gérer les préférences"
                    ) {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package" , context.packageName , null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                }
            }

            // Logout button with red styling
            Button(
                onClick = {
                    // Call logout function in ViewModel
                    personViewModel.logout()
                    // Navigate to MonCompte screen and clear back stack
                    navController.navigate(VintedScreen.MonCompte.name) {
                        popUpTo(VintedScreen.MonCompte.name) { inclusive = true }
                    }
                } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.1f) ,
                    contentColor = Color.Red
                ) ,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Se déconnecter" ,
                    style = MaterialTheme.typography.bodyLarge ,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Composable to display a row with an icon, label and value for profile information.
 *
 * @param icon Icon to display.
 * @param label Label text describing the information.
 * @param value The actual value to display.
 */
@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector ,
    label: String ,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth() ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon representing the type of information
        Icon(
            icon ,
            contentDescription = label ,
            tint = Color(0xFF007782) ,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        // Column with label and value texts
        Column {
            Text(label , style = MaterialTheme.typography.bodySmall , color = Color.Gray)
            Text(value , style = MaterialTheme.typography.bodyLarge , color = Color.Black)
        }
    }
}

/**
 * Composable to display a clickable menu item with icon, title, subtitle and arrow.
 *
 * @param icon Icon to display.
 * @param title Title text of the menu item.
 * @param subtitle Subtitle or description of the menu item.
 * @param onClick Lambda to execute when the item is clicked.
 */
@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector ,
    title: String ,
    subtitle: String ,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp) ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon for the menu item
        Icon(
            icon ,
            contentDescription = title ,
            tint = Color(0xFF007782) ,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.padding(12.dp))
        // Column with title and subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title ,
                style = MaterialTheme.typography.bodyLarge ,
                fontWeight = FontWeight.Medium ,
                color = Color.Black
            )
            Text(subtitle , style = MaterialTheme.typography.bodyMedium , color = Color.Gray)
        }
        // Arrow icon indicating navigation
        Icon(
            Icons.Outlined.KeyboardArrowRight ,
            contentDescription = "Arrow" ,
            tint = Color.Gray ,
            modifier = Modifier.size(20.dp)
        )
    }
}