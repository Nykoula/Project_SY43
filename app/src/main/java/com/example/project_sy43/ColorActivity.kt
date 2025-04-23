package com.example.project_sy43

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
class ColorActivity : ComponentActivity() {
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
                        title = { Text("Color") },
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
                displayColor(modifier = Modifier.padding(innerPadding))
            }
        }
    }

    @Composable
    fun displayColor(modifier: Modifier) {
        var selectedColors by remember { mutableStateOf(setOf<String>()) }

        LazyColumn(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize() // Prend tout l'espace disponible
        ) {
            item {
                Text(text = "Sélectionne 2 couleurs maximum")
                Spacer(modifier = Modifier.height(16.dp))
            }


            items(
                listOf(
                    "Black" to Color.Black,
                    "Marron" to Color(0xFF8B4513),
                    "Gris" to Color.Gray,
                    "Beige" to Color(0xFFF5DEB3),
                    "Fushia" to Color.Magenta,
                    "Purple" to Color(0xFF800080),
                    "Red" to Color.Red,
                    "Yellow" to Color.Yellow,
                    "Blue" to Color.Blue,
                    "Green" to Color.Green,
                    "Orange" to Color(0xFFFFA500),
                    "White" to Color.White,
                    "Silver" to Color(0xFFC0C0C0),
                    "Gold" to Color(0xFFFFD700),
                    "Multicolored" to Color.Unspecified,
                    "Khaki" to Color(0xFFF0E68C),
                    "Turquoise" to Color(0xFF40E0D0),
                    "Cream" to Color(0xFFFFFDD0),
                    "Apricot" to Color(0xFFFFDAB9),
                    "Coral" to Color(0xFFFF7F50),
                    "Burgundy" to Color(0xFF800020),
                    "Pink" to Color(0xFFFFC0CB),
                    "Lilac" to Color(0xFFC8A2C8),
                    "Light blue" to Color(0xFFADD8E6),
                    "Navy blue" to Color(0xFF000080),
                    "Dark green" to Color(0xFF006400),
                    "Mustard yellow" to Color(0xFFD4AF37),
                    "Menthe" to Color(0xFF98FF98)
                )
            ) { (couleur, code) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Affichage du rond coloré devant le nom de la couleur
                    Box(
                        modifier = Modifier
                            .size(24.dp) // Taille du cercle
                            .clip(CircleShape) // Rend la forme circulaire
                            .background(code) // Change en fonction de la couleur
                            .then(
                                if (code == Color.White) {
                                    Modifier.border(2.dp, Color.Black, CircleShape)
                                } else if (couleur == "Multicolored") {
                                    Modifier.background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color.Red,
                                                Color.Yellow,
                                                Color.Blue,
                                                Color.Green
                                            )
                                        )
                                    )
                                } else {
                                    Modifier
                                }
                            ) // Ajoute une bordure seulement si la couleur est blanche
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = couleur, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = selectedColors.contains(couleur),
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (selectedColors.size < 2) {
                                    selectedColors = selectedColors + couleur
                                }
                            } else {
                                selectedColors = selectedColors - couleur
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
                                ArrayList(selectedColors.toList())
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