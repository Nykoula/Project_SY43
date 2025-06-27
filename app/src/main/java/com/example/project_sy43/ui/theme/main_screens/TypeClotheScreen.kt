package com.example.project_sy43.ui.theme.main_screens

import android.util.Log
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


object TypeClothes {
    val womanType = listOf(
        "Manteaux et vestes" to VintedScreen.ManteauxEtVestes.name ,
        "Sweats et sweats à capuche" to VintedScreen.SweatCapuche.name ,
        "Blazer et tailleurs" to VintedScreen.Blazer.name ,
        "Robes" to VintedScreen.Robe.name ,
        "Jupes" to VintedScreen.Jupe.name ,
        "Hauts et t-shirts" to VintedScreen.Haut.name ,
        "Pantalons, jeans et leggings" to VintedScreen.Pantalon.name ,
        "Shorts" to VintedScreen.Short.name ,
        "Combinaisons et combishorts" to VintedScreen.Combinaison.name ,
        "Maillots de bain" to VintedScreen.MaillotDeBain.name ,
        "Lingerie et pyjamas" to VintedScreen.LingeriePyjama.name ,
        "Maternité" to VintedScreen.Maternite.name ,
        "Vêtements de sport" to VintedScreen.Sport.name
    )
    val manType = listOf(
        "Pantalons et jeans" to VintedScreen.Pantalon.name ,
        "Vestes et manteaux" to VintedScreen.ManteauxEtVestes.name ,
        "Hauts et t-shirts" to VintedScreen.Haut.name ,
        "Costumes et blazers" to VintedScreen.Blazer.name ,
        "Sweats et pulls" to VintedScreen.SweatCapuche.name ,
        "Shorts" to VintedScreen.Short.name ,
        "Sous-vêtements, chaussettes et pyjamas" to VintedScreen.LingeriePyjama.name ,
        "Vêtements de sport" to VintedScreen.Sport.name ,
        //"Maillots de bain" to VintedScreen.MaillotDeBain.name,//skip

    )
    val childrenType = listOf(
        "Vêtements pour filles" to VintedScreen.BabyGirl.name ,
        "Vêtements pour garçons" to VintedScreen.Baby.name//A CHANGER
    )
}

object TypeClothesChoice {
    val womanTypeChoice = listOf(
        "Costumes et tenues particulières" , "Autres vêtements femmes"
    )
    val manTypeChoice = listOf(
        "Maillots de bain" , "Vêtements spécialisés et costumes" , "Autres vêtements hommes"

    )
    val childrenTypeChoice = listOf(
        "Autres articles pour bébé et enfant"
    )
}


@Composable
fun TypeClotheScreen(navController: NavController , sellViewModel: SellViewModel) {
    var selectedType by sellViewModel.selectedType

    val typeList = when (sellViewModel.selectedCategory.value) {
        "Woman" -> TypeClothes.womanType
        "Man" -> TypeClothes.manType
        "Children" -> TypeClothes.childrenType
        else -> emptyList()
    }

    val typeListChoice = when (sellViewModel.selectedCategory.value) {
        "Woman" -> TypeClothesChoice.womanTypeChoice
        "Man" -> TypeClothesChoice.manTypeChoice
        "Children" -> TypeClothesChoice.childrenTypeChoice
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            VintedTopBar(title = "Vêtements" , navController , true)
        }
    ) { innerPadding ->
        if (typeList.isEmpty() && typeListChoice.isEmpty()) {
            Text(
                text = "Please choose a category first" ,
                color = Color.Black ,
                fontWeight = FontWeight.Bold ,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(
                    typeList
                ) { (type , screen) ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp , vertical = 8.dp)
                            .clickable {
                                navController.navigate(screen)
                            }
                    ) {
                        Text(text = type , fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight ,
                            contentDescription = "Arrow"
                        )
                    }
                    Divider(thickness = 1.dp , color = Color.Gray)
                }
                items(
                    typeListChoice
                ) { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp , vertical = 8.dp)
                            .clickable {
                                selectedType = type
                                sellViewModel.setProductType(type)
                                navController.popBackStack()
                            }
                    ) {
                        Text(text = type , fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        RadioButton(
                            selected = selectedType == type ,
                            onClick = null // on désactive onClick
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
}

@Composable
fun CategorySelectionScreen(navController: NavController) {

    Scaffold(
        topBar = {
            VintedTopBar(
                title = "Select Category" ,
                navController = navController ,
                canGoBack = true
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                val categoryNavigation = listOf(
                    "Woman" to "WomanClothesScreen" ,
                    "Man" to "ManClothesScreen" ,
                    "Children" to "ChildrenClothesScreen"
                )

                categoryNavigation.forEach { (category , route) ->
                    CategoryItem(
                        category = category ,
                        onClick = {
                            Log.d(
                                "CategorySelection" ,
                                "Clicked category: $category, navigating to: $route"
                            )
                            try {
                                navController.navigate(route)
                            } catch (e: Exception) {
                                Log.e("CategorySelection" , "Navigation error: ${e.message}" , e)
                            }
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun CategoryItem(category: String , onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically ,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp , vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = category , fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight ,
            contentDescription = "Arrow"
        )
    }
    Divider(thickness = 1.dp , color = Color.Gray)
}
