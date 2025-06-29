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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
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
import com.example.project_sy43.ui.theme.components.ButtonBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel

@Composable
fun MatieresScreen(navController: NavController , sellViewModel: SellViewModel) {

    var selectedMatieres by sellViewModel.selectedMaterial

    Scaffold(
        topBar = {
            VintedTopBar(
                title = "Matière (recommandé)" ,
                navController ,
                true ,
                description = "Sélectionne jusqu'à 3 options"
            )
        } ,
        bottomBar = {
            ButtonBottomBar(
                navController ,
                VintedScreen.Matieres ,
                onSaveClicked = {
                    sellViewModel.setSelectedMaterial(selectedMatieres)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(
                listOf(
                    "Acier" , "Acrylique" , "Alpaga" , "Argent" , "Bambou" , "Bois" ,
                    "Cachemire" , "Caoutchouc" , "Carton" , "Coton" , "Cuir" , "Cuir synthétique" ,
                    "Cuir verni" , "Céramique" , "Daim" , "Denim" , "Dentelle" , "Duvet" ,
                    "Élasthane" , "Fausse fourrure" , "Feutre" , "Flanelle" , "Jute" ,
                    "Laine" , "Latex" , "Lin" , "Maille" , "Mohair" , "Mousse" , "Mousseline" ,
                    "Mérinos" , "Métal" , "Nylon" , "Néoprène" , "Or" , "Paille" , "Papier" ,
                    "Peluche" , "Pierre" , "Plastique" , "Polaire" , "Polyester" , "Porcelaine" ,
                    "Rotin" , "Satin" , "Sequin" , "Silicone" , "Soie" , "Toile" , "Tulle" ,
                    "Tweed" , "Velours" , "Velours côtelé" , "Verre" , "Viscose"
                )
            ) { matiere ->
                val isChecked = selectedMatieres.contains(matiere)

                Row(
                    verticalAlignment = Alignment.CenterVertically ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isChecked) {
                                selectedMatieres = selectedMatieres - matiere
                            } else {
                                if (selectedMatieres.size < 3) {
                                    selectedMatieres = selectedMatieres + matiere
                                }
                            }
                        }
                ) {
                    Text(text = matiere , fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = isChecked ,
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
                Divider(thickness = 1.dp , color = Color.Gray)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
