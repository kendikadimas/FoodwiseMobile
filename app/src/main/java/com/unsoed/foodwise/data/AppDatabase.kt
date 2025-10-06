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

@Database(entities = [UserProfile::class, FoodItem::class, DailyLog::class, WeightHistory::class, Recipe::class, RecipeIngredient::class], version = 8, exportSchema = false)
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

            // Mengisi imageUrl dengan nama drawable (tanpa awalan @drawable/). Pastikan file-file drawable tersedia.
            giziDao.insertFoodItem(FoodItem(name = "Bakso Urat", calories = 400, protein = 25.0, carbs = 30.0, fat = 18.0, imageUrl = "ic_bakso_urat"))
            giziDao.insertFoodItem(FoodItem(name = "Ayam Bakar", calories = 240, protein = 30.0, carbs = 5.0, fat = 12.0, imageUrl = "ic_ayam_bakar"))
            giziDao.insertFoodItem(FoodItem(name = "Rendang Sapi", calories = 460, protein = 28.0, carbs = 8.0, fat = 35.0, imageUrl = "ic_rendang_sapi"))
            giziDao.insertFoodItem(FoodItem(name = "Nasi Goreng", calories = 350, protein = 15.0, carbs = 50.0, fat = 10.0, imageUrl = "ic_nasi_goreng"))
            giziDao.insertFoodItem(FoodItem(name = "Susu Putih", calories = 150, protein = 8.0, carbs = 12.0, fat = 8.0, imageUrl = "ic_susu_putih"))
            giziDao.insertFoodItem(FoodItem(name = "Jus Jeruk", calories = 112, protein = 1.7, carbs = 26.0, fat = 0.5, imageUrl = "ic_jus_jeruk"))
            giziDao.insertFoodItem(FoodItem(name = "Bubur Ayam", calories = 372, protein = 10.0, carbs = 30.0, fat = 3.0, imageUrl = "ic_bubur_ayam"))
            giziDao.insertFoodItem(FoodItem(name = "Ayam Geprek", calories = 400, protein = 35.0, carbs = 40.0, fat = 20.0, imageUrl = "ic_ayam_geprek"))
            giziDao.insertFoodItem(FoodItem(name = "Nasi Padang", calories = 600, protein = 20.0, carbs = 80.0, fat = 25.0, imageUrl = "ic_nasi_padang"))
            giziDao.insertFoodItem(FoodItem(name = "Tumis Kangkung", calories = 150, protein = 5.0, carbs = 10.0, fat = 7.0, imageUrl = "ic_tumis_kangkung"))
            giziDao.insertFoodItem(FoodItem(name = "Salad Sayur", calories = 210, protein = 10.0, carbs = 20.0, fat = 10.0, imageUrl = "ic_salad_sayur"))
            giziDao.insertFoodItem(FoodItem(name = "Sate Ayam", calories = 350, protein = 30.0, carbs = 5.0, fat = 12.0, imageUrl = "ic_sate_ayam"))
            giziDao.insertFoodItem(FoodItem(name = "Gado-Gado", calories = 400, protein = 15.0, carbs = 30.0, fat = 20.0, imageUrl = "ic_gado_gado"))
            giziDao.insertFoodItem(FoodItem(name = "Mie Goreng", calories = 300, protein = 10.0, carbs = 45.0, fat = 8.0, imageUrl = "ic_mie_goreng"))
            giziDao.insertFoodItem(FoodItem(name = "Es Teh Manis", calories = 120, protein = 0.0, carbs = 30.0, fat = 0.0, imageUrl = "ic_es_teh"))
            giziDao.insertFoodItem(FoodItem(name = "Es Jeruk", calories = 130, protein = 1.0, carbs = 32.0, fat = 0.0, imageUrl = "ic_es_jeruk"))
            giziDao.insertFoodItem(FoodItem(name = "Pecel Lele", calories = 450, protein = 35.0, carbs = 40.0, fat = 15.0, imageUrl = "ic_pecel_lele"))
            giziDao.insertFoodItem(FoodItem(name = "Soto Ayam", calories = 300, protein = 25.0, carbs = 20.0, fat = 10.0, imageUrl = "ic_soto_ayam"))
            giziDao.insertFoodItem(FoodItem(name = "Ayam Goreng", calories = 500, protein = 40.0, carbs = 45.0, fat = 22.0, imageUrl = "ic_ayam_goreng"))
            giziDao.insertFoodItem(FoodItem(name = "Jus Alpukat", calories = 250, protein = 3.0, carbs = 20.0, fat = 15.0, imageUrl = "ic_jus_alpukat"))
            giziDao.insertFoodItem(FoodItem(name = "Jus Mangga", calories = 180, protein = 2.0, carbs = 40.0, fat = 1.0, imageUrl = "ic_jus_mangga"))
            giziDao.insertFoodItem(FoodItem(name = "Susu Coklat", calories = 200, protein = 8.0, carbs = 25.0, fat = 5.0, imageUrl = "ic_susu_coklat"))

            Log.d("AppDatabase", "DATABASE POPULATED.")
        }
    }
}