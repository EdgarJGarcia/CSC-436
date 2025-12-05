package com.zybooks.basket.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.zybooks.basket.data.*
import kotlinx.coroutines.tasks.await

class CommunityRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val mealsCollection = firestore.collection("public_meals")
    private val usersCollection = firestore.collection("users")


    suspend fun publishMeal(meal: PublicMeal): Result<String> {
        return try {
            val docRef = mealsCollection.document()
            val mealWithId = meal.copy(id = docRef.id)

            docRef.set(mealWithId).await()

            usersCollection.document(meal.creatorId)
                .update("recipesCreated", FieldValue.increment(1))
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularMeals(limit: Int = 20): Result<List<PublicMeal>> {
        return try {
            val snapshot = mealsCollection
                .orderBy("likeCount", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull {
                it.toObject(PublicMeal::class.java)
            }

            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentMeals(limit: Int = 20): Result<List<PublicMeal>> {
        return try {
            val snapshot = mealsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull {
                it.toObject(PublicMeal::class.java)
            }

            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingMeals(limit: Int = 20): Result<List<PublicMeal>> {
        return try {
            val snapshot = mealsCollection
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull {
                it.toObject(PublicMeal::class.java)
            }

            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFollowingFeed(userId: String, limit: Int = 20): Result<List<PublicMeal>> {
        return try {
            val followingSnapshot = usersCollection.document(userId)
                .collection("following")
                .get()
                .await()

            val followingIds = followingSnapshot.documents.map { it.id }

            if (followingIds.isEmpty()) {
                return Result.success(emptyList())
            }

            val mealsSnapshot = mealsCollection
                .whereIn("creatorId", followingIds.take(10))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val meals = mealsSnapshot.documents.mapNotNull {
                it.toObject(PublicMeal::class.java)
            }

            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMealsByCreator(creatorId: String): Result<List<PublicMeal>> {
        return try {
            val snapshot = mealsCollection
                .whereEqualTo("creatorId", creatorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull {
                it.toObject(PublicMeal::class.java)
            }

            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementSaveCount(mealId: String): Result<Unit> {
        return try {
            mealsCollection.document(mealId)
                .update("saveCount", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun followUser(followerId: String, followedId: String): Result<Unit> {
        return try {
            firestore.runBatch { batch ->
                val followingRef = usersCollection.document(followerId)
                    .collection("following")
                    .document(followedId)
                batch.set(followingRef, Follow(followerId, followedId))

                val followerRef = usersCollection.document(followedId)
                    .collection("followers")
                    .document(followerId)
                batch.set(followerRef, Follow(followerId, followedId))

                batch.update(
                    usersCollection.document(followerId),
                    "followingCount",
                    FieldValue.increment(1)
                )

                batch.update(
                    usersCollection.document(followedId),
                    "followersCount",
                    FieldValue.increment(1)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unfollowUser(followerId: String, followedId: String): Result<Unit> {
        return try {
            firestore.runBatch { batch ->
                val followingRef = usersCollection.document(followerId)
                    .collection("following")
                    .document(followedId)
                batch.delete(followingRef)

                val followerRef = usersCollection.document(followedId)
                    .collection("followers")
                    .document(followerId)
                batch.delete(followerRef)

                batch.update(
                    usersCollection.document(followerId),
                    "followingCount",
                    FieldValue.increment(-1)
                )

                batch.update(
                    usersCollection.document(followedId),
                    "followersCount",
                    FieldValue.increment(-1)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFollowing(followerId: String, followedId: String): Result<Boolean> {
        return try {
            val doc = usersCollection.document(followerId)
                .collection("following")
                .document(followedId)
                .get()
                .await()

            Result.success(doc.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun likeMeal(userId: String, mealId: String): Result<Unit> {
        return try {
            firestore.runBatch { batch ->
                val likeRef = mealsCollection.document(mealId)
                    .collection("likes")
                    .document(userId)
                batch.set(likeRef, RecipeLike(userId, mealId))

                batch.update(
                    mealsCollection.document(mealId),
                    "likeCount",
                    FieldValue.increment(1)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlikeMeal(userId: String, mealId: String): Result<Unit> {
        return try {
            firestore.runBatch { batch ->
                val likeRef = mealsCollection.document(mealId)
                    .collection("likes")
                    .document(userId)
                batch.delete(likeRef)

                batch.update(
                    mealsCollection.document(mealId),
                    "likeCount",
                    FieldValue.increment(-1)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun hasLiked(userId: String, mealId: String): Result<Boolean> {
        return try {
            val doc = mealsCollection.document(mealId)
                .collection("likes")
                .document(userId)
                .get()
                .await()

            Result.success(doc.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun rateMeal(
        userId: String,
        username: String,
        mealId: String,
        rating: Int,
        review: String = ""
    ): Result<Unit> {
        return try {
            val ratingData = RecipeRating(userId, username, mealId, rating, review)

            mealsCollection.document(mealId)
                .collection("ratings")
                .document(userId)
                .set(ratingData)
                .await()

            updateAverageRating(mealId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateAverageRating(mealId: String) {
        val ratingsSnapshot = mealsCollection.document(mealId)
            .collection("ratings")
            .get()
            .await()

        val ratings = ratingsSnapshot.documents.mapNotNull {
            it.toObject(RecipeRating::class.java)?.rating
        }

        if (ratings.isNotEmpty()) {
            val average = ratings.average().toFloat()
            mealsCollection.document(mealId)
                .update(
                    mapOf(
                        "rating" to average,
                        "ratingCount" to ratings.size
                    )
                )
                .await()
        }
    }
}