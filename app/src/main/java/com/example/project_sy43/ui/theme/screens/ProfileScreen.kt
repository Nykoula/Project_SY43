package com.example.project_sy43.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            VintedTopBar(title = "Profil", navController)
        },
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.Profile)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val db = FirebaseFirestore.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            //ligne pour le dressing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(VintedScreen.Dressing.name)
                    }
            ) {
                //ajouter photo de profil
                Column {
                    //text avec nom user
                    Text(
                        text = personViewModel.person?.firstName ?: "Nom inconnu",
                        style = MaterialTheme.typography.bodyLarge
                    )
//                    Text(
//                        "${personViewModel.person?.firstName}",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
                    Text(
                        text = "Voir mon dressing",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))
            //ligne pour les paramètres
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(VintedScreen.Setting.name)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Setting"
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Text(text = "Setting")
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
            }

            Button(onClick = {
                personViewModel.logout()
                navController.navigate(VintedScreen.Accueil.name) {
                    popUpTo(VintedScreen.MonCompte.name) { inclusive = true }
                }
            }) {
                Text("Se déconnecter")
            }
        }
    }
}