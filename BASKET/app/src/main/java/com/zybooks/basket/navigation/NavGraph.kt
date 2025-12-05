package com.zybooks.basket.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.zybooks.basket.screens.*
import com.zybooks.basket.viewmodel.AuthViewModel
import com.zybooks.basket.viewmodel.BasketViewModel
import com.zybooks.basket.viewmodel.CommunityViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    basketViewModel: BasketViewModel,
    authViewModel: AuthViewModel,
    communityViewModel: CommunityViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }

        // Main app screens
        composable(Screen.MyMeals.route) {
            MyMealsScreen(
                viewModel = basketViewModel,
                navController = navController
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                viewModel = communityViewModel,
                navController = navController
            )
        }

        composable(Screen.AddEditMeal.route) {
            AddEditMealScreen(
                viewModel = basketViewModel,
                authViewModel = authViewModel,
                communityViewModel = communityViewModel,
                navController = navController
            )
        }

        composable(Screen.GroceryList.route) {
            GroceryListScreen(
                viewModel = basketViewModel,
                navController = navController
            )
        }

        composable(
            route = Screen.CookingMode.route,
            arguments = listOf(navArgument("mealId") { type = NavType.IntType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getInt("mealId") ?: 0
            CookingModeScreen(
                viewModel = basketViewModel,
                navController = navController,
                mealId = mealId
            )
        }
    }
}