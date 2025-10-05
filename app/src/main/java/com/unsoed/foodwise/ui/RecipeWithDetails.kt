package com.unsoed.foodwise.ui

import androidx.room.Embedded
import androidx.room.Relation
import com.unsoed.foodwise.data.Recipe
import com.unsoed.foodwise.data.RecipeIngredient

data class RecipeWithDetails(
    @Embedded
    val recipe: Recipe,

    @Relation(
        parentColumn = "id", // pastikan ini sesuai dengan primary key di entity Recipe
        entityColumn = "recipeId"
    )
    val ingredients: List<RecipeIngredient>,

    // Tambahan field untuk menyimpan total kalori resep
    val totalCalories: Int = 0
)
