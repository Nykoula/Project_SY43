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
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sy43.viewmodel.SharedViewModel

object WomanClothes {
    val womanType = listOf(
        "Manteaux et vestes",
        "Sweats et sweats à capuche",
        "Blazer et tailleurs",
        "Robes",
        "Jupes",
        "Hauts et t-shirts",
        "Pantalons, jeans et leggings",
        "Shorts",
        "Combinaisons et combishorts",
        "Maillots de bain",
        "Lingerie et pyjamas",
        "Maternité",
        "Vêtements de sport"
    )
}

@Composable
fun WomanClothesScreen(
    navController: NavController,
    sellViewModel: SellViewModel,
    sharedViewModel: SharedViewModel = viewModel()
) {
    val selectedType by sharedViewModel.selectedType
    val typeList = WomanClothes.womanType

    Scaffold(
        topBar = {
            VintedTopBar(title = "Vêtements pour femmes", navController, true)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(typeList) { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            sharedViewModel.setType(type)
                            Log.d("FilterSearch", "Clicked category: $type")
                            navController.popBackStack("Search", inclusive = false)
                        }
                ) {
                    Text(text = type, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = selectedType == type,
                        onClick = null
                    )
                }
                Divider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}


