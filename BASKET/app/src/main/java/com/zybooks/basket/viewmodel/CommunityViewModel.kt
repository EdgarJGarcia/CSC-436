package com.zybooks.basket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zybooks.basket.data.Ingredient
import com.zybooks.basket.data.Meal
import com.zybooks.basket.data.PublicMeal
import com.zybooks.basket.data.PublicIngredient
import com.zybooks.basket.data.BasketRepository
import com.zybooks.basket.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val communityRepository: CommunityRepository = CommunityRepository(),
    private val basketRepository: BasketRepository
) : ViewModel() {

    private val _currentMeals = MutableStateFlow<List<PublicMeal>>(emptyList())
    val currentMeals: StateFlow<List<PublicMeal>> = _currentMeals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFollowing = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isFollowing: StateFlow<Map<String, Boolean>> = _isFollowing.asStateFlow()

    private val _hasLiked = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val hasLiked: StateFlow<Map<String, Boolean>> = _hasLiked.asStateFlow()

    fun loadPopularMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = communityRepository.getPopularMeals(limit = 20)
            result.onSuccess { meals ->
                _currentMeals.value = meals
            }.onFailure {
                _currentMeals.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun loadRecentMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = communityRepository.getRecentMeals(limit = 20)
            result.onSuccess { meals ->
                _currentMeals.value = meals
            }.onFailure {
                _currentMeals.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun loadTrendingMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = communityRepository.getTrendingMeals(limit = 20)
            result.onSuccess { meals ->
                _currentMeals.value = meals
            }.onFailure {
                _currentMeals.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun loadFollowingFeed(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = communityRepository.getFollowingFeed(userId, limit = 20)
            result.onSuccess { meals ->
                _currentMeals.value = meals
            }.onFailure {
                _currentMeals.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    suspend fun saveToMyMeals(publicMeal: PublicMeal) {
        val meal = Meal(
            name = publicMeal.name,
            cookTime = publicMeal.cookTime,
            servings = publicMeal.servings,
            instructions = publicMeal.instructions,
            isPublic = false
        )

        val mealId = basketRepository.insertMeal(meal)

        val ingredients = publicMeal.ingredients.map { publicIngredient ->
            Ingredient(
                name = publicIngredient.name,
                quantity = publicIngredient.quantity,
                mealId = mealId.toInt(),
                category = "Other"
            )
        }

        basketRepository.insertIngredients(ingredients)
        communityRepository.incrementSaveCount(publicMeal.id)
    }

    suspend fun publishMeal(
        meal: Meal,
        ingredients: List<Ingredient>,
        creatorId: String,
        creatorUsername: String
    ): Result<String> {
        val publicMeal = PublicMeal(
            name = meal.name,
            cookTime = meal.cookTime,
            servings = meal.servings,
            instructions = meal.instructions,
            creatorId = creatorId,
            creatorUsername = creatorUsername,
            ingredients = ingredients.map { ingredient ->
                PublicIngredient(
                    name = ingredient.name,
                    quantity = ingredient.quantity
                )
            }
        )

        return communityRepository.publishMeal(publicMeal)
    }

    // Follow/Unfollow
    fun followUser(followerId: String, followedId: String) {
        viewModelScope.launch {
            communityRepository.followUser(followerId, followedId).onSuccess {
                _isFollowing.value = _isFollowing.value + (followedId to true)
            }
        }
    }

    fun unfollowUser(followerId: String, followedId: String) {
        viewModelScope.launch {
            communityRepository.unfollowUser(followerId, followedId).onSuccess {
                _isFollowing.value = _isFollowing.value + (followedId to false)
            }
        }
    }

    fun checkIfFollowing(followerId: String, followedId: String) {
        viewModelScope.launch {
            val result = communityRepository.isFollowing(followerId, followedId)
            result.onSuccess { isFollowing ->
                _isFollowing.value = _isFollowing.value + (followedId to isFollowing)
            }
        }
    }

    // Like/Unlike
    fun likeMeal(userId: String, mealId: String) {
        viewModelScope.launch {
            communityRepository.likeMeal(userId, mealId).onSuccess {
                _hasLiked.value = _hasLiked.value + (mealId to true)
            }
        }
    }

    fun unlikeMeal(userId: String, mealId: String) {
        viewModelScope.launch {
            communityRepository.unlikeMeal(userId, mealId).onSuccess {
                _hasLiked.value = _hasLiked.value + (mealId to false)
            }
        }
    }

    fun checkIfLiked(userId: String, mealId: String) {
        viewModelScope.launch {
            val result = communityRepository.hasLiked(userId, mealId)
            result.onSuccess { hasLiked ->
                _hasLiked.value = _hasLiked.value + (mealId to hasLiked)
            }
        }
    }

    // Ratings
    fun rateMeal(userId: String, username: String, mealId: String, rating: Int, review: String = "") {
        viewModelScope.launch {
            communityRepository.rateMeal(userId, username, mealId, rating, review)
        }
    }
}