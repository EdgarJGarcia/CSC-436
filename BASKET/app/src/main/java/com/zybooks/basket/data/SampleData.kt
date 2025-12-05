package com.zybooks.basket.data

object SampleData {
    fun getSampleMeals(): List<Meal> {
        return listOf(
            Meal(
                name = "Spaghetti Carbonara",
                cookTime = 30,
                servings = 4,
                instructions = "1. Boil pasta\n2. Cook bacon\n3. Mix eggs and cheese\n4. Combine all",
                isPublic = false
            ),
            Meal(
                name = "Chicken Stir Fry",
                cookTime = 25,
                servings = 4,
                instructions = "1. Cut chicken\n2. Prepare vegetables\n3. Stir fry in wok\n4. Add sauce",
                isPublic = false
            ),
            Meal(
                name = "Beef Tacos",
                cookTime = 20,
                servings = 4,
                instructions = "1. Brown beef\n2. Add taco seasoning\n3. Warm tortillas\n4. Assemble tacos",
                isPublic = false
            ),
            Meal(
                name = "Caesar Salad",
                cookTime = 15,
                servings = 2,
                instructions = "1. Chop lettuce\n2. Make dressing\n3. Add croutons\n4. Toss and serve",
                isPublic = false
            )
        )
    }
}