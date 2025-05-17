package com.example.project_sy43.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_sy43.ui.theme.screens.Accueil
import com.example.project_sy43.ui.theme.screens.LoginScreen
import com.example.project_sy43.viewmodel.ProductViewModel
import androidx.navigation.compose.rememberNavController
import com.example.project_sy43.ui.theme.screens.MonCompte
import com.example.project_sy43.ui.theme.screens.SellScreen
import com.example.project_sy43.ui.theme.screens.SignUpScreen
import com.example.project_sy43.ui.theme.screens.ColorScreen
import com.example.project_sy43.ui.theme.screens.MatieresScreen
import com.example.project_sy43.ui.theme.screens.SizeScreen
import com.example.project_sy43.viewmodel.SellViewModel

@Composable
fun VintedNavGraph(navController: NavHostController, viewModelProduct: ProductViewModel, viewModelSell: SellViewModel) {
    NavHost(navController = navController, startDestination = VintedScreen.Accueil.name) {
        composable(route = VintedScreen.Accueil.name) {
            Accueil(
                viewModel = viewModelProduct,
                navController = navController
            )
        }
        composable(route = VintedScreen.Login.name) {
            LoginScreen(
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = VintedScreen.SignUp.name) {
            SignUpScreen(
                navController = navController
            )
        }
        composable(route = VintedScreen.MonCompte.name) {
            MonCompte(
                viewModel = viewModelProduct,
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = VintedScreen.Sell.name) {
            SellScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.ColorScreen.name) {
            ColorScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Matieres.name) {
            MatieresScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Size.name) {
            SizeScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
    }
}