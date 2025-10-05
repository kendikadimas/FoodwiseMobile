package com.unsoed.foodwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_ingredients")
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long, // Merujuk ke Recipe
    val foodId: Long,   // Merujuk ke FoodItem
    val quantityInGrams: Int
)