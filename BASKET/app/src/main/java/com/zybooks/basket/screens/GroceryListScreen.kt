package com.zybooks.basket.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zybooks.basket.data.GroceryItem
import com.zybooks.basket.viewmodel.BasketViewModel
import com.zybooks.basket.screens.components.BottomNavBar
import com.zybooks.basket.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(
    viewModel: BasketViewModel,
    navController: NavController
) {
    val groceryItems by viewModel.allGroceryItems.collectAsState(initial = emptyList())

    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("") }

    // Separate checked and unchecked items
    val uncheckedItems = groceryItems.filter { !it.isChecked }
    val checkedItems = groceryItems.filter { it.isChecked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Grocery List",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Clear All") },
                            onClick = {
                                viewModel.clearAllGroceryItems()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Checked Items") },
                            onClick = {
                                viewModel.clearCheckedItems()
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = Screen.GroceryList.route
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Add, "Add Item", tint = Color.White)
            }
        }
    ) { padding ->
        if (groceryItems.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "📝",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No items yet!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Add meals to your basket to see ingredients here",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF8E7)) // Cream background
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Unchecked items (active shopping list)
                    items(uncheckedItems) { item ->
                        GroceryItemRow(
                            item = item,
                            onCheckedChange = {
                                viewModel.updateGroceryItem(item.copy(isChecked = true))
                            },
                            onDelete = {
                                viewModel.deleteGroceryItem(item)
                            }
                        )
                    }

                    // Divider between active and completed
                    if (checkedItems.isNotEmpty() && uncheckedItems.isNotEmpty()) {
                        item {
                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFCCCCCC),
                                thickness = 1.dp
                            )
                        }
                    }

                    // Checked items (completed)
                    items(checkedItems) { item ->
                        GroceryItemRow(
                            item = item,
                            onCheckedChange = {
                                viewModel.updateGroceryItem(item.copy(isChecked = false))
                            },
                            onDelete = {
                                viewModel.deleteGroceryItem(item)
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddItemDialog = false
                newItemName = ""
                newItemQuantity = ""
            },
            title = { Text("Add Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newItemQuantity,
                        onValueChange = { newItemQuantity = it },
                        label = { Text("Quantity (e.g., 2 cups, 1 lb)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Item name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newItemName.isNotBlank() && newItemQuantity.isNotBlank()) {
                            val item = GroceryItem(
                                name = newItemName,
                                quantity = newItemQuantity,
                                category = "Other",
                                isChecked = false,
                                fromMealIds = ""
                            )
                            viewModel.insertGroceryItem(item)
                            showAddItemDialog = false
                            newItemName = ""
                            newItemQuantity = ""
                        }
                    },
                    enabled = newItemName.isNotBlank() && newItemQuantity.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddItemDialog = false
                        newItemName = ""
                        newItemQuantity = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun GroceryItemRow(
    item: GroceryItem,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Text(
            text = if (item.isChecked) "☑" else "☐",
            fontSize = 24.sp,
            color = Color(0xFF2C2C2C),
            modifier = Modifier.clickable { onCheckedChange() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Item text
        Text(
            text = "${item.quantity} ${item.name}",
            fontSize = 18.sp,
            color = if (item.isChecked) Color(0xFF888888) else Color(0xFF2C2C2C),
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
            modifier = Modifier
                .weight(1f)
                .clickable { onCheckedChange() }
        )

        // Delete button
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                "Delete",
                tint = Color.Red.copy(alpha = 0.6f)
            )
        }
    }
}