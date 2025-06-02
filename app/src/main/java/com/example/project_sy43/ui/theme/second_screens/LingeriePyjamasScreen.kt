package com.example.project_sy43.ui.theme.second_screens

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

object Pyjama {
    val womanType = listOf(
        "Soutiens-gorge",
        "Culottes",
        "Ensembles",
        "Gaines",
        "Pyjamas",
        "Peignoirs",
        "Collants",
        "Chaussettes",
        "Accessoires de lingerie",
        "Autres lingerie",
        "Autres pyjamas"
    )
    val manType = listOf(
        "Sous-vêtements",
        "Chaussettes",
        "Peignoirs",
        "Pyjamas une pièce",
        "Bas de pyjamas",
        "Ensembles de pyjamas",
        "Hauts de pyjamas",
        "Autres pyjamas",
        "Autres sous-vêtements"

    )
    val childrenType = listOf(
        "Autres shorts"
    )
}

@Composable
fun LingeriePyjamaScreen(navController: NavController, sellViewModel: SellViewModel) {

    var selectedType by sellViewModel.selectedType

    val typeList = when (sellViewModel.selectedCategory.value) {
        "Woman" -> Pyjama.womanType
        "Man" -> Pyjama.manType
        "Children" -> Pyjama.childrenType
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            VintedTopBar(title = "Lingerie et pyjamas", navController, true)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
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
        }
    }
}
