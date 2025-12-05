package com.zybooks.basket.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zybooks.basket.data.PublicMeal
import com.zybooks.basket.navigation.Screen
import com.zybooks.basket.screens.components.BottomNavBar
import com.zybooks.basket.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch

enum class CommunityTab {
    POPULAR, RECENT, TRENDING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(CommunityTab.POPULAR) }
    val meals by viewModel.currentMeals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var showSaveDialog by remember { mutableStateOf(false) }
    var selectedMeal by remember { mutableStateOf<PublicMeal?>(null) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            CommunityTab.POPULAR -> viewModel.loadPopularMeals()
            CommunityTab.RECENT -> viewModel.loadRecentMeals()
            CommunityTab.TRENDING -> viewModel.loadTrendingMeals()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Discover Recipes",
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
                currentRoute = Screen.Community.route
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.White,
                contentColor = Color(0xFF4CAF50)
            ) {
                Tab(
                    selected = selectedTab == CommunityTab.POPULAR,
                    onClick = { selectedTab = CommunityTab.POPULAR },
                    text = { Text("Popular") }
                )
                Tab(
                    selected = selectedTab == CommunityTab.RECENT,
                    onClick = { selectedTab = CommunityTab.RECENT },
                    text = { Text("Recent") }
                )
                Tab(
                    selected = selectedTab == CommunityTab.TRENDING,
                    onClick = { selectedTab = CommunityTab.TRENDING },
                    text = { Text("Trending") }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else if (meals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No recipes yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Be the first to share a recipe!",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(meals) { meal ->
                        SimpleMealCard(
                            meal = meal,
                            onSaveClick = {
                                selectedMeal = meal
                                showSaveDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSaveDialog && selectedMeal != null) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Recipe") },
            text = {
                Text("Save \"${selectedMeal?.name}\" to your meals?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            selectedMeal?.let { meal ->
                                viewModel.saveToMyMeals(meal)
                            }
                            showSaveDialog = false
                            selectedMeal = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                        selectedMeal = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SimpleMealCard(
    meal: PublicMeal,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = meal.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "👤 ${meal.creatorUsername}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "⏱ ${meal.cookTime} min",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "💾 ${meal.saveCount}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save to My Meals")
            }
        }
    }
}