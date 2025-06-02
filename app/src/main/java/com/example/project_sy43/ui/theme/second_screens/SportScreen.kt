package com.example.project_sy43.ui.theme.second_screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel

object Sport {
    val womanType = listOf(
        "Vêtements d'extérieur",
        "Survêtements",
        "Joggings & leggings",
        "Shorts",
        "Robes de sports",
        "Jupes de sports",
        "Hauts & t-shirt de sports",
        "Maillots de sports",
        "Sweats & sweats à capuche",
        "Brassières",
        "Autres vêtements de sports"
    )
    val manType = listOf(
        "Vêtements d'extérieur",
        "Survêtements",
        "Pantalons",
        "Joggings",
        "Shorts",
        "Hauts & t-shirt de sports",
        "Maillots de sports",
        "Pulls & sweats",
        "Autres vêtements de sports"

    )
    val childrenType = listOf(
        "Autres shorts"
    )
}

@Composable
fun SportScreen(navController: NavController, sellViewModel: SellViewModel) {
    var selectedType by sellViewModel.selectedType

    val typeList = when (sellViewModel.selectedCategory.value) {
        "Woman" -> Sport.womanType
        "Man" -> Sport.manType
        "Children" -> Sport.childrenType
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            VintedTopBar(title = "Vêtements de sports", navController, true)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            navController.navigate(VintedScreen.AccessoireSport.name)
                        }
                ) {
                    Text(text = "Accessoires de sports", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = "Arrow"
                    )
                }
                Divider(thickness = 1.dp, color = Color.Gray)
            }
            items(
                typeList
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
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

