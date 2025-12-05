package com.zybooks.basket.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zybooks.basket.data.Ingredient
import com.zybooks.basket.data.Meal
import com.zybooks.basket.viewmodel.BasketViewModel
import com.zybooks.basket.viewmodel.AuthViewModel
import com.zybooks.basket.viewmodel.CommunityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope


data class IngredientInput(
    val name: String = "",
    val quantity: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMealScreen(
    viewModel: BasketViewModel,
    authViewModel: AuthViewModel,
    communityViewModel: CommunityViewModel,
    navController: NavController,
    mealId: Int = -1
) {
    val scope = rememberCoroutineScope()

    var mealName by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("4") }
    var ingredients by remember { mutableStateOf(listOf(IngredientInput())) }
    var cookingSteps by remember { mutableStateOf(listOf("")) }
    var isPublic by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (mealId == -1) "Add Meal" else "Edit Meal",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Meal Name
            OutlinedTextField(
                value = mealName,
                onValueChange = { mealName = it },
                label = { Text("Meal Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cook Time
            OutlinedTextField(
                value = cookTime,
                onValueChange = { cookTime = it },
                label = { Text("Cook Time (minutes)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Servings
            OutlinedTextField(
                value = servings,
                onValueChange = { servings = it },
                label = { Text("Servings") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ingredients Section
            Text(
                "Ingredients",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ingredients.forEachIndexed { index, ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingredient.quantity,
                        onValueChange = { newQuantity ->
                            ingredients = ingredients.toMutableList().also {
                                it[index] = ingredient.copy(quantity = newQuantity)
                            }
                        },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(0.3f)
                    )

                    OutlinedTextField(
                        value = ingredient.name,
                        onValueChange = { newName ->
                            ingredients = ingredients.toMutableList().also {
                                it[index] = ingredient.copy(name = newName)
                            }
                        },
                        label = { Text("Ingredient") },
                        modifier = Modifier.weight(0.6f)
                    )

                    if (ingredients.size > 1) {
                        IconButton(
                            onClick = {
                                ingredients = ingredients.toMutableList().also {
                                    it.removeAt(index)
                                }
                            },
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Ingredient Button
            OutlinedButton(
                onClick = {
                    ingredients = ingredients + IngredientInput()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Ingredient")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Public/Private Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPublic) Color(0xFFE8F5E9) else Color(0xFFFAFAFA)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Share with Community",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (isPublic) "Others can discover and save this recipe"
                            else "Only you can see this recipe",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF4CAF50),
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cooking Instructions Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cooking Instructions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Optional",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                "Add step-by-step instructions for cooking this meal",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            cookingSteps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Step number badge
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 8.dp),
                        shape = MaterialTheme.shapes.small,
                        color = Color(0xFF4CAF50)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${index + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    OutlinedTextField(
                        value = step,
                        onValueChange = { newStep ->
                            cookingSteps = cookingSteps.toMutableList().also {
                                it[index] = newStep
                            }
                        },
                        label = { Text("Step ${index + 1}") },
                        modifier = Modifier.weight(0.85f),
                        minLines = 2,
                        maxLines = 4
                    )

                    if (cookingSteps.size > 1) {
                        IconButton(
                            onClick = {
                                cookingSteps = cookingSteps.toMutableList().also {
                                    it.removeAt(index)
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Step Button
            OutlinedButton(
                onClick = {
                    cookingSteps = cookingSteps + ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Next Step")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    // Combine all steps into a single string
                    val instructionsText = cookingSteps
                        .filter { it.isNotBlank() }
                        .joinToString("\n")

                    val meal = Meal(
                        name = mealName,
                        cookTime = cookTime.toIntOrNull() ?: 30,
                        servings = servings.toIntOrNull() ?: 4,
                        instructions = instructionsText,
                        isPublic = isPublic
                    )

                    val ingredientsList = ingredients.filter {
                        it.name.isNotBlank() && it.quantity.isNotBlank()
                    }

                    // Save meal locally
                    viewModel.insertMealWithIngredients(
                        meal = meal,
                        ingredientInputs = ingredientsList
                    )

                    // If public, also publish to Firebase
                    if (isPublic) {
                        scope.launch {
                            val currentUser = authViewModel.currentUser.value
                            val userProfile = authViewModel.userProfile.value

                            if (currentUser != null && userProfile != null) {
                                // Get the meal ID after saving locally
                                delay(100) // Small delay to ensure it's saved

                                val ingredientsForPublish = ingredientsList.map { input ->
                                    Ingredient(
                                        name = input.name,
                                        quantity = input.quantity,
                                        mealId = 0, // Will be ignored for publishing
                                        category = "Other"
                                    )
                                }

                                communityViewModel.publishMeal(
                                    meal = meal,
                                    ingredients = ingredientsForPublish,
                                    creatorId = currentUser.uid,
                                    creatorUsername = userProfile.username
                                )
                            }
                        }
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                enabled = mealName.isNotBlank() &&
                        cookTime.isNotBlank() &&
                        ingredients.any { it.name.isNotBlank() }
            ) {
                Text("Save Meal", fontSize = 16.sp)
            }
        }
    }
}