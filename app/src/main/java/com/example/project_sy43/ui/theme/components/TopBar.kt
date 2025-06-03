package com.example.project_sy43.ui.theme.components

import android.R.attr.tint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sy43.viewmodel.SellViewModel

@OptIn(ExperimentalMaterial3Api::class)//pour la top bar
@Composable
fun VintedTopBar(
    title: String,
    navController: NavController,
    canGoBack: Boolean = true, // si false, pas d'icône
    description: String = "",
    menuDeroulant: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF007782),
            titleContentColor = Color.White,
        ),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title)
                if (description.isNotEmpty()){
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (menuDeroulant) {
                //affiche le bouton trois points
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert ,
                        contentDescription = "Menu" ,
                        tint = Color.White
                    )
                }

                //quand on clique ca affiche de dropmenu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Modifier") },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Supprimer") },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    )
}