package com.zybooks.basket.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zybooks.basket.navigation.Screen

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                label = "Meals",
                emoji = "🍽️",
                isSelected = currentRoute == Screen.MyMeals.route,
                onClick = {
                    navController.navigate(Screen.MyMeals.route) {
                        popUpTo(Screen.MyMeals.route) { inclusive = true }
                    }
                }
            )

            BottomNavItem(
                label = "List",
                emoji = "📝",
                isSelected = currentRoute == Screen.GroceryList.route,
                onClick = {
                    navController.navigate(Screen.GroceryList.route) {
                        popUpTo(Screen.MyMeals.route)
                    }
                }
            )

            BottomNavItem(
                label = "Community",
                emoji = "👥",
                isSelected = currentRoute == Screen.Community.route,
                onClick = {
                    navController.navigate(Screen.Community.route) {
                        popUpTo(Screen.MyMeals.route)
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}