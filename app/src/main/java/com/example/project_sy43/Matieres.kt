package com.example.project_sy43

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
class Matieres : ComponentActivity(){
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
                            title = { Text("Matière (recommandé)") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    if (context is ComponentActivity) {
                                        context.finish()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowBack,
                                        contentDescription = "Go back",
                                        tint = Color.White
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    displayMatiere(modifier = Modifier.padding(innerPadding))
                }
            }
        }

        @Composable
        fun displayMatiere(modifier: Modifier) {
            var selectedMatieres by remember { mutableStateOf(setOf<String>()) }

            LazyColumn(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxSize() // Prend tout l'espace disponible
            ) {
                item {
                    Text(text = "Sélectionne jusqu'à 3 options", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                }


                items(
                    listOf(
                        "Acier", "Acrylique", "Alpaga", "Argent", "Bambou", "Bois",
                        "Cachemire", "Caoutchouc", "Carton", "Coton", "Cuir", "Cuir synthétique",
                        "Cuir verni", "Céramique", "Daim", "Denim", "Dentelle", "Duvet",
                        "Élasthane", "Fausse fourrure", "Feutre", "Flanelle", "Jute",
                        "Laine", "Latex", "Lin", "Maille", "Mohair", "Mousse", "Mousseline",
                        "Mérinos", "Métal", "Nylon", "Néoprène", "Or", "Paille", "Papier",
                        "Peluche", "Pierre", "Plastique", "Polaire", "Polyester", "Porcelaine",
                        "Rotin", "Satin", "Sequin", "Silicone", "Soie", "Toile", "Tulle",
                        "Tweed", "Velours", "Velours côtelé", "Verre", "Viscose"
                    )
                ) { matiere ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = matiere, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = selectedMatieres.contains(matiere),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    if (selectedMatieres.size < 3) {
                                        selectedMatieres = selectedMatieres + matiere
                                    }
                                } else {
                                    selectedMatieres = selectedMatieres - matiere
                                }
                            }
                        )
                    }
                    Divider(thickness = 1.dp, color = Color.Gray)
                }

                item {

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center // Centre son contenu automatiquement
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF007782),
                                contentColor = Color.White
                            ),
                            onClick = {
                                val resultIntent = Intent()
                                resultIntent.putStringArrayListExtra(
                                    "selectedColors",
                                    ArrayList(selectedMatieres.toList())
                                ) //conversion en ArrayList<String>
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish() // Ferme l'activité et retourne le résultat
                            }) {
                            Text(text = "Terminé")
                        }
                    }
                }
            }
        }
}