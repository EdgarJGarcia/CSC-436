package com.zybooks.basket.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: String,
    val category: String = "Other",
    val isChecked: Boolean = false,
    val fromMealIds: String = ""
)