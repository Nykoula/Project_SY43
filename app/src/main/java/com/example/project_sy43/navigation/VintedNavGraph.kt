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
import com.example.project_sy43.ui.theme.screens.Dressing
import com.example.project_sy43.ui.theme.screens.MatieresScreen
import com.example.project_sy43.ui.theme.screens.Messages
import com.example.project_sy43.ui.theme.screens.Profile
import com.example.project_sy43.ui.theme.screens.Search
import com.example.project_sy43.ui.theme.screens.Setting
import com.example.project_sy43.ui.theme.screens.SizeScreen
import com.example.project_sy43.viewmodel.PersonViewModel
import com.example.project_sy43.viewmodel.SellViewModel
import com.example.project_sy43.ui.theme.screens.UpdatePassword
import com.example.project_sy43.ui.theme.screens.UpdateProfile
import com.example.project_sy43.ui.theme.screens.NotificationSetting

@Composable
fun VintedNavGraph(navController: NavHostController, viewModelProduct: ProductViewModel, viewModelSell: SellViewModel, viewModelPerson: PersonViewModel) {
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
        composable(route = VintedScreen.Search.name) {
            Search(
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = VintedScreen.Messages.name) {
            Messages(
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = VintedScreen.Profile.name) {
            Profile(
                personViewModel = viewModelPerson,
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = VintedScreen.Setting.name) {
            Setting(
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = VintedScreen.Dressing.name) {
            Dressing(
                personViewModel = viewModelPerson,
                navController = navController,
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(VintedScreen.UpdatePassword.name) {
            UpdatePassword(navController = navController)
        }
        composable(VintedScreen.UpdateProfile.name) {
            UpdateProfile(navController = navController,onCancel = {})
        }
        composable(VintedScreen.NotificationSettings.name) {
            NotificationSetting(navController = navController,onCancel = {navController.popBackStack()})
        }

    }
}