package com.zybooks.basket.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mealId: Int,
    val name: String,
    val quantity: String,
    val category: String = "Other"
)