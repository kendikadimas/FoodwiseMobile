package com.unsoed.foodwise.data

import android.content.Context
import android.util.Log // <-- Import Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [UserProfile::class, FoodItem::class, DailyLog::class, WeightHistory::class, Recipe::class, RecipeIngredient::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun giziDao(): GiziDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gizi_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(CoroutineScope(Dispatchers.IO)))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.giziDao())
                }
            }
        }

        suspend fun populateDatabase(giziDao: GiziDao) {
            // TAMBAHKAN LOG INI UNTUK DEBUGGING
            Log.d("AppDatabase", "POPULATING DATABASE WITH INITIAL DATA...")

            giziDao.deleteAllFoodItems()

            giziDao.insertFoodItem(FoodItem(name = "Bakso Urat", calories = 400, protein = 25.0, carbs = 30.0, fat = 18.0, imageUrl = null))
            giziDao.insertFoodItem(FoodItem(name = "Ayam Bakar", calories = 240, protein = 30.0, carbs = 5.0, fat = 12.0, imageUrl = null))
            giziDao.insertFoodItem(FoodItem(name = "Rendang Sapi", calories = 460, protein = 28.0, carbs = 8.0, fat = 35.0, imageUrl = null))
            giziDao.insertFoodItem(FoodItem(name = "Nasi Goreng", calories = 350, protein = 15.0, carbs = 50.0, fat = 10.0, imageUrl = null))
            giziDao.insertFoodItem(FoodItem(name = "Susu Putih", calories = 150, protein = 8.0, carbs = 12.0, fat = 8.0, imageUrl = null))
            giziDao.insertFoodItem(FoodItem(name = "Jus Jeruk", calories = 112, protein = 1.7, carbs = 26.0, fat = 0.5, imageUrl = null))

            Log.d("AppDatabase", "DATABASE POPULATED.")
        }
    }
}