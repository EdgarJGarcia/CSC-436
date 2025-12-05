package com.zybooks.basket.data

import kotlinx.coroutines.flow.Flow

class BasketRepository(private val basketDao: BasketDao) {

    // Meals
    fun getAllMeals(): Flow<List<Meal>> = basketDao.getAllMeals()

    suspend fun getMealById(mealId: Int): Meal? = basketDao.getMealById(mealId)

    suspend fun insertMeal(meal: Meal): Long = basketDao.insertMeal(meal)

    suspend fun updateMeal(meal: Meal) = basketDao.updateMeal(meal)

    suspend fun deleteMeal(meal: Meal) = basketDao.deleteMeal(meal)

    fun getUserPublicMeals(userId: String): Flow<List<Meal>> =
        basketDao.getUserPublicMeals(userId)

    // Ingredients
    fun getIngredientsForMeal(mealId: Int): Flow<List<Ingredient>> =
        basketDao.getIngredientsForMeal(mealId)

    suspend fun insertIngredient(ingredient: Ingredient) =
        basketDao.insertIngredient(ingredient)

    suspend fun insertIngredients(ingredients: List<Ingredient>) =
        basketDao.insertIngredients(ingredients)

    suspend fun deleteIngredientsForMeal(mealId: Int) =
        basketDao.deleteIngredientsForMeal(mealId)

    // Grocery Items
    fun getAllGroceryItems(): Flow<List<GroceryItem>> =
        basketDao.getAllGroceryItems()

    suspend fun insertGroceryItem(item: GroceryItem) =
        basketDao.insertGroceryItem(item)

    suspend fun insertGroceryItems(items: List<GroceryItem>) =
        basketDao.insertGroceryItems(items)

    suspend fun updateGroceryItem(item: GroceryItem) =
        basketDao.updateGroceryItem(item)

    suspend fun deleteGroceryItem(item: GroceryItem) =
        basketDao.deleteGroceryItem(item)

    suspend fun clearAllGroceryItems() = basketDao.clearAllGroceryItems()

    suspend fun clearCheckedItems() = basketDao.clearCheckedItems()
}