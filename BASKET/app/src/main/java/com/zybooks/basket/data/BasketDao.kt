package com.zybooks.basket.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BasketDao {
    // Get all meals
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<Meal>>

    // Get a single meal by ID
    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Int): Meal?

    // Insert a meal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    // Update a meal
    @Update
    suspend fun updateMeal(meal: Meal)

    // Delete a meal
    @Delete
    suspend fun deleteMeal(meal: Meal)

    // Get user's public meals (for their profile)
    @Query("SELECT * FROM meals WHERE creatorId = :userId AND isPublic = 1")
    fun getUserPublicMeals(userId: String): Flow<List<Meal>>

    // Ingredients
    @Query("SELECT * FROM ingredients WHERE mealId = :mealId")
    fun getIngredientsForMeal(mealId: Int): Flow<List<Ingredient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<Ingredient>)

    @Query("DELETE FROM ingredients WHERE mealId = :mealId")
    suspend fun deleteIngredientsForMeal(mealId: Int)

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    // Grocery Items
    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, category ASC")
    fun getAllGroceryItems(): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItems(items: List<GroceryItem>)

    @Update
    suspend fun updateGroceryItem(item: GroceryItem)

    @Delete
    suspend fun deleteGroceryItem(item: GroceryItem)

    @Query("DELETE FROM grocery_items")
    suspend fun clearAllGroceryItems()

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun clearCheckedItems()
}