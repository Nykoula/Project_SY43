package com.example.project_sy43.ui.theme.children_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel

@Composable
fun BabyShoesScreen(navController: NavController, sellViewModel: SellViewModel) {

    var selectedType by sellViewModel.selectedType

    Scaffold(
        topBar = {
            VintedTopBar(title = "Bébé", navController, true)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(
                listOf(
                    "Chaussures bébé",
                    "Chaussures habillées",
                    "Chaussures à talons",
                    "Chaussons et pantoufles",
                    "Chaussures de danse",
                    "Chaussures de foot",
                    "Chaussures et bottes de rabdonnées",
                    "Chaussures de ski",
                    "Baskets à scratch",
                    "Baskets à lacets",
                    "Baskets sans lacets",
                    "Bottes",
                    "Bottes mi-hautes",
                    "Bottes de neige",
                    "Bottes de pluie",
                    "Mules et sabots",
                    "Patins à glace",
                    "Patins à roulettes et rollers",
                    "Tongs",
                    "Sandales",
                    "Claquettes"
                )
            ) { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            selectedType = type
                            sellViewModel.setProductType(type)
                            navController.popBackStack("Sell", inclusive = false)
                        }
                ) {
                    Text(text = type, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = selectedType == type,
                        onClick = null // on désactive onClick
                    )
                }
                Divider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}
