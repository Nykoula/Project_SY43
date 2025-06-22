package com.example.project_sy43

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.project_sy43.navigation.VintedNavGraph
import com.example.project_sy43.ui.theme.theme.Project_SY43Theme
import com.example.project_sy43.viewmodel.PersonViewModel
import com.example.project_sy43.viewmodel.ProductViewModel
import com.example.project_sy43.viewmodel.SellViewModel
import com.example.project_sy43.viewmodel.SharedViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            Project_SY43Theme {
                val navController = rememberNavController()
                val productViewModel: ProductViewModel = viewModel()
                val sellViewModel: SellViewModel = viewModel()
                val sharedViewModel: SharedViewModel = viewModel()
                val personViewModel: PersonViewModel = viewModel()
                VintedNavGraph(navController, productViewModel, sellViewModel, personViewModel)
            }
        }
    }
}