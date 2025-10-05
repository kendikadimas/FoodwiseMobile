package com.unsoed.foodwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weight_history")
data class WeightHistory(
    @PrimaryKey(autoGenerate = true) val historyId: Long = 0,
    val date: Date,
    val weight: Double
)