package com.unsoed.foodwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val date: Date,
    val mealType: String, // "Sarapan", "Makan Siang", "Makan Malam"
    val foodId: Long,
    val servingSize: Double = 1.0 // Default 1 porsi
)