package com.example.project_sy43.ui.theme.screens

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
fun SizeScreen(navController: NavController, sellViewModel: SellViewModel) {

    var selectedSize by sellViewModel.selectedSize

    Scaffold(
        topBar = {
            VintedTopBar(title = "Size", navController, true)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(
                listOf(
                    "XXXS/30/2",
                    "XXS/32/4",
                    "XS/34/6",
                    "S/36/8",
                    "M/38/10",
                    "L/40/12",
                    "XL/42/14",
                    "XXL/44/16",
                    "XXXL/46/18",
                    "4XL/48/20",
                    "5XL/50/22",
                    "6XL/52/24",
                    "7XL/54/26",
                    "8XL/56/28",
                    "9XL/58/30",
                    "Unique size",
                    "other"
                )
            ) { size ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            selectedSize = size
                            sellViewModel.setSelectedSize(size)
                            navController.popBackStack()
                        }
                ) {
                    Text(text = size, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = selectedSize == size,
                        onClick = null // on désactive onClick
                    )
                }
                Divider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}
