package com.example.project_sy43

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_sy43.ui.theme.Project_SY43Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project_SY43Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF007782)
                )
                {
                    Accueil()
                }
            }
        }
    }
    @Composable
    fun Accueil() {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Button(
                onClick = {
                    val intent = Intent(context, Login::class.java)
                    context.startActivity(intent)
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // Couleur d'arrière-plan du bouton
                    contentColor = Color(0xFF007782)    // Couleur du texte
                ), shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth() // Le bouton occupe toute la largeur
            )
            {
                Text(
                    text = "Connexion/Login"
                )
            }
        }
    }
}