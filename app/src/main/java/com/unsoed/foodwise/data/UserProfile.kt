package com.unsoed.foodwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Hanya ada 1 profil, jadi ID-nya tetap 1
    val gender: String,
//    val birthDate: Long, // Simpan sebagai timestamp agar mudah dihitung
    val age: Int,
    val height: Int,
    val weight: Double,
    val activityLevel: String,
    val goal: String,
    val targetWeight: Double? = null
)