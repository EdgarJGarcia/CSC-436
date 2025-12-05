package com.zybooks.basket.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object MyMeals : Screen("my_meals")
    object Community : Screen("community")
    object GroceryList : Screen("grocery_list")
    object AddEditMeal : Screen("add_edit_meal")
    object CookingMode : Screen("cooking_mode/{mealId}") {
        fun createRoute(mealId: Int) = "cooking_mode/$mealId"
    }
    object MealDetail : Screen("meal_detail/{mealId}") {
        fun createRoute(mealId: Int) = "meal_detail/$mealId"
    }
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
}