package com.unsoed.foodwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val recipeId: Long = 0,
    val name: String,
    val servingCount: Int
)