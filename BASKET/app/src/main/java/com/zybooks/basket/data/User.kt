package com.zybooks.basket.data

/**
 * User profile stored in Firestore
 */
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val recipesCreated: Int = 0,
    val recipesSaved: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val totalLikes: Int = 0
)

/**
 * Follow relationship stored in Firestore
 */
data class Follow(
    val followerId: String = "",
    val followedId: String = "",
    val followedAt: Long = System.currentTimeMillis()
)