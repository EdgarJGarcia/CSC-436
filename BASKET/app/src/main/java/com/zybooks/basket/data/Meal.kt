package com.zybooks.basket.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val cookTime: Int,
    val servings: Int,
    val instructions: String,
    val imageUrl: String = "",
    val isPublic: Boolean = false,
    val creatorId: String = "",
    val creatorName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)