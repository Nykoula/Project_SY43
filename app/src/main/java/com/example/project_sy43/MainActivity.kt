package com.example.project_sy43

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.project_sy43.navigation.VintedNavGraph
import com.example.project_sy43.ui.theme.theme.Project_SY43Theme
import com.example.project_sy43.viewmodel.PersonViewModel
import com.example.project_sy43.viewmodel.ProductViewModel
import com.example.project_sy43.viewmodel.SellViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project_SY43Theme {
                val navController = rememberNavController()
                val productViewModel: ProductViewModel = viewModel()
                val sellViewModel: SellViewModel = viewModel()
                val personViewModel: PersonViewModel = viewModel()
                VintedNavGraph(navController, productViewModel, sellViewModel, personViewModel)
            }
        }
    }
}
/*class MainActivity : ComponentActivity() {
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
}*/