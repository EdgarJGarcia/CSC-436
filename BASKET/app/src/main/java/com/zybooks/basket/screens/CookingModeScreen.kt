package com.zybooks.basket.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zybooks.basket.viewmodel.BasketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingModeScreen(
    viewModel: BasketViewModel,
    navController: NavController,
    mealId: Int
) {
    var meal by remember { mutableStateOf<com.zybooks.basket.data.Meal?>(null) }

    LaunchedEffect(mealId) {
        meal = viewModel.getMealById(mealId)
    }

    val ingredients by viewModel.getIngredientsForMeal(mealId)
        .collectAsState(initial = emptyList())

    // Parse instructions into steps
    val steps = remember(meal) {
        meal?.instructions?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
    }

    var currentStep by remember { mutableStateOf(0) }
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        meal?.name ?: "Cooking",
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
        if (meal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (steps.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Info message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF9C4)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ℹ️",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            "No cooking instructions available for this meal. Check the ingredients below!",
                            fontSize = 14.sp,
                            color = Color(0xFF795548)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Ingredients reference
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "📋 Ingredients",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (ingredients.isEmpty()) {
                            Text(
                                "No ingredients listed",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        } else {
                            ingredients.forEach { ingredient ->
                                Text(
                                    "• ${ingredient.quantity} ${ingredient.name}",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done button
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        "Done",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Progress indicator
                Text(
                    "Step ${currentStep + 1} of ${steps.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { (currentStep + 1).toFloat() / steps.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Current step card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFAFAFA)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Step number badge
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF4CAF50), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${currentStep + 1}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Step instruction
                        Text(
                            steps.getOrNull(currentStep) ?: "",
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Completion checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = currentStep in completedSteps,
                        onCheckedChange = { checked ->
                            completedSteps = if (checked) {
                                completedSteps + currentStep
                            } else {
                                completedSteps - currentStep
                            }
                        }
                    )
                    Text("Mark step as complete")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Previous button
                    OutlinedButton(
                        onClick = {
                            if (currentStep > 0) currentStep--
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = currentStep > 0,
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("← Previous")
                    }

                    // Next button
                    Button(
                        onClick = {
                            if (currentStep < steps.size - 1) {
                                currentStep++
                            } else {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            if (currentStep < steps.size - 1) "Next →" else "Done ✓",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Ingredients reference
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "📋 Ingredients",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ingredients.forEach { ingredient ->
                            Text(
                                "• ${ingredient.quantity} ${ingredient.name}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}