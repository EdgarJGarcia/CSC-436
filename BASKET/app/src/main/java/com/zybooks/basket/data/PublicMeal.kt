package com.zybooks.basket.data

/**
 * Public meal shared in the community
 */
data class PublicMeal(
    val id: String = "",
    val name: String = "",
    val cookTime: Int = 0,
    val servings: Int = 4,
    val instructions: String = "",
    val creatorId: String = "",
    val creatorUsername: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val saveCount: Int = 0,
    val likeCount: Int = 0,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val ingredients: List<PublicIngredient> = emptyList()
)

data class PublicIngredient(
    val name: String = "",
    val quantity: String = ""
)

/**
 * Recipe rating
 */
data class RecipeRating(
    val userId: String = "",
    val username: String = "",
    val mealId: String = "",
    val rating: Int = 0,
    val review: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Recipe like
 */
data class RecipeLike(
    val userId: String = "",
    val mealId: String = "",
    val likedAt: Long = System.currentTimeMillis()
)