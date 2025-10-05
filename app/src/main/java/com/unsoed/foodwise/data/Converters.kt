package com.unsoed.foodwise.data // Pastikan nama package Anda benar

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    /**
     * Mengubah Long (timestamp) dari database menjadi objek Date.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Mengubah objek Date dari aplikasi menjadi Long (timestamp) untuk disimpan ke database.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}