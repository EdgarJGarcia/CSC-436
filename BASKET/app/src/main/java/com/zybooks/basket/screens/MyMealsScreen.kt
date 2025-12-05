package com.zybooks.basket.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zybooks.basket.data.Meal
import com.zybooks.basket.navigation.Screen
import com.zybooks.basket.viewmodel.BasketViewModel
import com.zybooks.basket.screens.components.BottomNavBar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi




@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyMealsScreen(
    viewModel: BasketViewModel,
    navController: NavController
) {
    val meals by viewModel.allMeals.collectAsState(initial = emptyList())
    val groceryItems by viewModel.allGroceryItems.collectAsState(initial = emptyList())

    var showDuplicateDialog by remember { mutableStateOf(false) }
    var selectedMealForBasket by remember { mutableStateOf<Meal?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Meals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        },

        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = Screen.MyMeals.route
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditMeal.route) },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal", tint = Color.White)
            }
        }
    ) { padding ->
        if (meals.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No meals yet!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tap + to add your first meal",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            // Grid of meal cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(meals) { meal ->
                    MealCard(
                        meal = meal,
                        onCookNowClick = {
                            navController.navigate(Screen.CookingMode.createRoute(meal.id))
                        },
                        onAddToBasketClick = {
                            val mealAlreadyAdded = groceryItems.any {
                                it.fromMealIds.contains(meal.id.toString())
                            }

                            if (mealAlreadyAdded) {
                                selectedMealForBasket = meal
                                showDuplicateDialog = true
                            } else {
                                viewModel.addMealToGroceryList(meal.id)
                            }
                        },
                        onDeleteClick = {
                            viewModel.deleteMeal(meal)
                        }

                    )
                }
            }
        }
    }

    if (showDuplicateDialog && selectedMealForBasket != null) {
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            title = { Text("Already Added") },
            text = { Text("This meal's ingredients are already in your grocery list. Add them again?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedMealForBasket?.let { viewModel.addMealToGroceryList(it.id) }
                        showDuplicateDialog = false
                        selectedMealForBasket = null
                    }
                ) {
                    Text("Yes, Add Again")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDuplicateDialog = false
                        selectedMealForBasket = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealCard(
    meal: Meal,
    onCookNowClick: () -> Unit,
    onAddToBasketClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { showDeleteDialog = true }
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(getMealColor(meal.id)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getMealEmoji(meal.name),
                    fontSize = 56.sp
                )
            }

            // Meal info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = meal.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "⏱ ${meal.cookTime} min",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cook Now button
                Button(
                    onClick = onCookNowClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Cook Now!", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onAddToBasketClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Add to Basket")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Meal?") },
            text = { Text("Are you sure you want to delete ${meal.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}

// Helper function to get color based on meal ID
fun getMealColor(id: Int): Color {
    val colors = listOf(
        Color(0xFFFFB74D), // Orange
        Color(0xFF81C784), // Green
        Color(0xFFFF8A65), // Red
        Color(0xFFFFD54F), // Yellow
        Color(0xFF64B5F6), // Blue
        Color(0xFFBA68C8)  // Purple
    )
    return colors[id % colors.size]
}

// Helper function to get emoji based on meal name
fun getMealEmoji(name: String): String {
    return when {
        name.contains("pasta", ignoreCase = true) -> "🍝"
        name.contains("chicken", ignoreCase = true) -> "🍗"
        name.contains("taco", ignoreCase = true) -> "🌮"
        name.contains("salad", ignoreCase = true) -> "🥗"
        name.contains("burger", ignoreCase = true) -> "🍔"
        name.contains("pizza", ignoreCase = true) -> "🍕"
        name.contains("fish", ignoreCase = true) -> "🐟"
        name.contains("rice", ignoreCase = true) -> "🍚"
        name.contains("soup", ignoreCase = true) -> "🍲"
        else -> "🍽️"
    }
}