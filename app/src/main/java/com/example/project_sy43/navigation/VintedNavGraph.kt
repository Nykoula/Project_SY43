package com.example.project_sy43.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_sy43.ui.theme.main_screens.Accueil
import com.example.project_sy43.ui.theme.main_screens.LoginScreen
import com.example.project_sy43.viewmodel.ProductViewModel
import androidx.navigation.navArgument
import com.example.project_sy43.ui.theme.second_screens.AccessoireSportScreen
import com.example.project_sy43.ui.theme.second_screens.BlazerScreen
import com.example.project_sy43.ui.theme.main_screens.ClothingDetailView
import com.example.project_sy43.ui.theme.main_screens.MonCompte
import com.example.project_sy43.ui.theme.main_screens.SellScreen
import com.example.project_sy43.ui.theme.main_screens.SignUpScreen
import com.example.project_sy43.ui.theme.second_screens.ColorScreen
import com.example.project_sy43.ui.theme.second_screens.CombinaisonScreen
import com.example.project_sy43.ui.theme.main_screens.Dressing
import com.example.project_sy43.ui.theme.second_screens.HautScreen
import com.example.project_sy43.ui.theme.second_screens.JupeScreen
import com.example.project_sy43.ui.theme.second_screens.LingerieMaterniteScreen
import com.example.project_sy43.ui.theme.second_screens.LingeriePyjamaScreen
import com.example.project_sy43.ui.theme.second_screens.MaillotDeBainScreen
import com.example.project_sy43.ui.theme.second_screens.ManteauxEtVestesScreen
import com.example.project_sy43.ui.theme.second_screens.ManteauxScreen
import com.example.project_sy43.ui.theme.second_screens.MaterniteScreen
import com.example.project_sy43.ui.theme.second_screens.MatieresScreen
import com.example.project_sy43.ui.theme.main_screens.Messages
import com.example.project_sy43.ui.theme.main_screens.Profile
import com.example.project_sy43.ui.theme.main_screens.Search
import com.example.project_sy43.ui.theme.main_screens.Setting
import com.example.project_sy43.ui.theme.second_screens.SizeScreen
import com.example.project_sy43.viewmodel.PersonViewModel
import com.example.project_sy43.viewmodel.SellViewModel
import com.example.project_sy43.ui.theme.main_screens.UpdatePassword
import com.example.project_sy43.ui.theme.main_screens.UpdateProfile
import com.example.project_sy43.ui.theme.main_screens.NotificationSetting
import com.example.project_sy43.ui.theme.second_screens.PantalonScreen
import com.example.project_sy43.ui.theme.second_screens.RobeOccasionScreen
import com.example.project_sy43.ui.theme.second_screens.RobeScreen
import com.example.project_sy43.ui.theme.second_screens.ShortScreen
import com.example.project_sy43.ui.theme.second_screens.SportScreen
import com.example.project_sy43.ui.theme.second_screens.SweatCapucheScreen
import com.example.project_sy43.ui.theme.second_screens.SweatScreen
import com.example.project_sy43.ui.theme.main_screens.TypeClotheScreen
import com.example.project_sy43.ui.theme.second_screens.VestesScreen
import com. example. project_sy43.ui. theme. main_screens. ConversationScreen
import android. util. Log
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.project_sy43.ui.theme.children_screen.BabyGirlScreen
import com.example.project_sy43.ui.theme.children_screen.BabyScreen
import com.example.project_sy43.ui.theme.children_screen.BabyShoesScreen
import com.example.project_sy43.viewmodel.ConversationViewModel
import com.example.project_sy43.ui.theme.main_screens.CategorySelectionScreen
import com.example.project_sy43.ui.theme.second_screens.ChildrenClothesScreen
import com.example.project_sy43.ui.theme.second_screens.ManClothesScreen
import com.example.project_sy43.ui.theme.second_screens.WomanClothesScreen
import com.example.project_sy43.viewmodel.SharedViewModel

@Composable
fun VintedNavGraph(
    navController: NavHostController,
    viewModelProduct: ProductViewModel,
    viewModelSell: SellViewModel,
    viewModelPerson: PersonViewModel,
    viewModelConversation: ConversationViewModel
) {
    NavHost(navController = navController, startDestination = VintedScreen.MonCompte.name) {
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
        /*composable(route = VintedScreen.Sell.name) {
            SellScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }*/
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
        composable(route = VintedScreen.TypeClothe.name) {
            TypeClotheScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.CategorySelectionScreen.name) {
            CategorySelectionScreen(
                navController = navController
            )
        }
        composable(route = VintedScreen.WomanClothesScreen.name) {
            WomanClothesScreen(
                navController = navController,
                sellViewModel = viewModelSell,
                sharedViewModel = SharedViewModel()
            )
        }
        composable(route = VintedScreen.ManClothesScreen.name) {
            ManClothesScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.ChildrenClothesScreen.name) {
            ChildrenClothesScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.ManteauxEtVestes.name) {
            ManteauxEtVestesScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Vestes.name) {
            VestesScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.SweatCapuche.name) {
            SweatCapucheScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Manteaux.name) {
            ManteauxScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Sweat.name) {
            SweatScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Blazer.name) {
            BlazerScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Robe.name) {
            RobeScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.RobeOccasion.name) {
            RobeOccasionScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Jupe.name) {
            JupeScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Haut.name) {
            HautScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Pantalon.name) {
            PantalonScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Short.name) {
            ShortScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Combinaison.name) {
            CombinaisonScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.MaillotDeBain.name) {
            MaillotDeBainScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.LingeriePyjama.name) {
            LingeriePyjamaScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Maternite.name) {
            MaterniteScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.LingerieMaternite.name) {
            LingerieMaterniteScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Sport.name) {
            SportScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.AccessoireSport.name) {
            AccessoireSportScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.BabyGirl.name) {
            BabyGirlScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.Baby.name) {
            BabyScreen(
                navController = navController,
                sellViewModel = viewModelSell
            )
        }
        composable(route = VintedScreen.BabyShoes.name) {
            BabyShoesScreen(
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
                },
                sharedViewModel = SharedViewModel()
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

//        composable(
//            // Original route: "${VintedScreen.Conversation.name}/{conversationId}"
//            // New route:
//            route = "${VintedScreen.Conversation.name}/{conversationId}/{otherUserName}", // Add otherUserName
//            arguments = listOf(
//                navArgument("conversationId") { type = NavType.StringType },
//                navArgument("otherUserName") {
//                    type = NavType.StringType
//                    nullable =
//                        true // Make it nullable if it can sometimes be missing, otherwise remove this line
//                    defaultValue =
//                        null // Or a sensible default string if not nullable, e.g., "User"
//                }
//            )
//        ) { backStackEntry ->
//            val conversationId = backStackEntry.arguments?.getString("conversationId")
//            val otherUserName = backStackEntry.arguments?.getString("otherUserName") // Retrieve it
//
//            if (conversationId != null) {
//                ConversationScreen(
//                    navController = navController,
//                    conversationId = conversationId,
//                    otherUserName = otherUserName // Pass it to the Composable
//                )
//            } else {
//                navController.popBackStack() // Or handle error
//            }
//        }

        composable(
            route = "${VintedScreen.Conversation.name}/{conversationId}",
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) {
            val conversationIdArg = it.arguments?.getString("conversationId")
            // Get the ViewModel using the default factory
            val conversationViewModel: ConversationViewModel =
                viewModel()

            // IMPORTANT: Initialize the ViewModel with the conversationId
            // This should ideally happen only once or when conversationId changes.
            // LaunchedEffect is good for this.
            LaunchedEffect(conversationIdArg) {
                if (conversationIdArg != null) {
                    conversationViewModel.initialize(conversationIdArg)
                } else {
                    // Handle missing conversationId, e.g., log error, navigate back
                    Log.e("NavGraph", "ConversationId is null, cannot initialize ViewModel.")
                    // navController.popBackStack() // Example
                }
            }

            if (conversationIdArg != null) {
                ConversationScreen(
                    viewModel = viewModelConversation,
                    navController = navController,
                    conversationId = conversationIdArg,
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            } else {
                Log.e("VintedNavGraph", "ConversationId is null, cannot navigate to ConversationScreen.")
                navController.popBackStack()
            }
        }

        composable(route = VintedScreen.Messages.name) {
            Messages(
                navController = navController ,
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
        composable(
            route = "${VintedScreen.ArticleDetail.name}/{itemId}?menuDeroulant={menuDeroulant}",
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("menuDeroulant") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            val menuDeroulant = backStackEntry.arguments?.getBoolean("menuDeroulant") ?: false

            ClothingDetailView(
                personViewModel = viewModelPerson,
                navController = navController,
                itemId = itemId,
                onCancel = {
                    navController.popBackStack()
                },
                menuDeroulant = menuDeroulant
            )
        }
        /*composable(route = VintedScreen.FlappyBirdGames.name) {
            FlappyBirdGame()
        }*/
        composable(VintedScreen.UpdateProfile.name) {
            UpdateProfile(navController = navController, onCancel = {})
        }
        composable(VintedScreen.UpdatePassword.name) {
            UpdatePassword(navController)
        }
        composable(VintedScreen.NotificationSettings.name) {
            NotificationSetting(
                navController = navController,
                onCancel = { navController.popBackStack() })
        }
        /*composable(
            route = "${VintedScreen.Sell.name}/{itemId}",
            //la route accepte un argument de type string
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ){
            val itemId = it.arguments?.getString("itemId")
            SellScreen(
                navController = navController,
                sellViewModel = viewModelSell,
                itemId = itemId
            )
        }*/
        composable(
            route = "${VintedScreen.Sell.name}?itemId={itemId}",
            arguments = listOf(navArgument("itemId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            val itemId = it.arguments?.getString("itemId")
            SellScreen(
                navController = navController,
                sellViewModel = viewModelSell,
                itemId = itemId
            )
        }

    }
}