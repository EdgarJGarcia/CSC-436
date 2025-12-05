package com.zybooks.basket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zybooks.basket.data.AppDatabase
import com.zybooks.basket.data.BasketRepository
import com.zybooks.basket.data.GroceryItem
import com.zybooks.basket.data.Ingredient
import com.zybooks.basket.data.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import android.content.Context
import com.zybooks.basket.screens.IngredientInput


class BasketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BasketRepository

    // Flows for observing data
    val allMeals: Flow<List<Meal>>
    val allGroceryItems: Flow<List<GroceryItem>>

    init {
        val basketDao = AppDatabase.Companion.getDatabase(application).basketDao()
        repository = BasketRepository(basketDao)
        allMeals = repository.getAllMeals()
        allGroceryItems = repository.getAllGroceryItems()

        val sharedPrefs = application.getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)
        val hasInsertedSampleData = sharedPrefs.getBoolean("has_sample_data", false)

        if (!hasInsertedSampleData) {
            viewModelScope.launch {
                val sampleMeals = listOf(
                    Meal(
                        name = "Spaghetti Carbonara",
                        cookTime = 30,
                        servings = 4,
                        instructions = "Cook pasta, fry bacon, mix with eggs"
                    ),
                    Meal(
                        name = "Chicken Stir Fry",
                        cookTime = 25,
                        servings = 4,
                        instructions = "Stir fry chicken and vegetables"
                    ),
                    Meal(
                        name = "Beef Tacos",
                        cookTime = 20,
                        servings = 4,
                        instructions = "Brown beef, warm tortillas, assemble"
                    ),
                    Meal(
                        name = "Caesar Salad",
                        cookTime = 15,
                        servings = 2,
                        instructions = "Chop lettuce, make dressing, toss"
                    )
                )
                sampleMeals.forEach { repository.insertMeal(it) }

                // Mark as done
                sharedPrefs.edit().putBoolean("has_sample_data", true).apply()

            }
        }
    }

    fun addMealToGroceryList(mealId: Int) {
        viewModelScope.launch {
            // Get ingredients for this meal
            val ingredients = repository.getIngredientsForMeal(mealId)

            ingredients.collect { ingredientList ->
                // Convert each ingredient to a grocery item
                val groceryItems = ingredientList.map { ingredient ->
                    GroceryItem(
                        name = ingredient.name,
                        quantity = ingredient.quantity,
                        category = ingredient.category,
                        isChecked = false,
                        fromMealIds = mealId.toString()
                    )
                }

                // Insert all grocery items
                repository.insertGroceryItems(groceryItems)
            }
        }
    }

    // Meal operations
    fun insertMeal(meal: Meal) = viewModelScope.launch {
        repository.insertMeal(meal)
    }

    fun updateMeal(meal: Meal) = viewModelScope.launch {
        repository.updateMeal(meal)
    }

    fun deleteMeal(meal: Meal) = viewModelScope.launch {
        repository.deleteMeal(meal)
    }

    fun getIngredientsForMeal(mealId: Int): Flow<List<Ingredient>> {
        return repository.getIngredientsForMeal(mealId)
    }

    // Ingredient operations
    fun insertIngredients(ingredients: List<Ingredient>) = viewModelScope.launch {
        repository.insertIngredients(ingredients)
    }

    // Grocery operations
    fun insertGroceryItem(item: GroceryItem) = viewModelScope.launch {
        repository.insertGroceryItem(item)
    }

    fun updateGroceryItem(item: GroceryItem) = viewModelScope.launch {
        repository.updateGroceryItem(item)
    }

    fun deleteGroceryItem(item: GroceryItem) = viewModelScope.launch {
        repository.deleteGroceryItem(item)
    }

    fun clearAllGroceryItems() = viewModelScope.launch {
        repository.clearAllGroceryItems()
    }

    fun insertMealWithIngredients(meal: Meal, ingredientInputs: List<IngredientInput>) {
        viewModelScope.launch {
            val mealId = repository.insertMeal(meal)

            val ingredients = ingredientInputs.map { input ->
                Ingredient(
                    mealId = mealId.toInt(),
                    name = input.name,
                    quantity = input.quantity,
                    category = "Other"
                )
            }

            repository.insertIngredients(ingredients)
        }
    }

    fun clearCheckedItems() = viewModelScope.launch {
        repository.clearCheckedItems()
    }

    suspend fun getMealById(mealId: Int): Meal? {
        return repository.getMealById(mealId)
    }

}