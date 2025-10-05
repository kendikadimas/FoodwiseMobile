package com.unsoed.foodwise.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.unsoed.foodwise.ui.RecipeWithDetails
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface GiziDao {

    // --- Perintah untuk UserProfile ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): LiveData<UserProfile?>

    // --- Perintah untuk FoodItem ---
    @Insert
    suspend fun insertFoodItem(foodItem: FoodItem) : Long

    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoodItems(): LiveData<List<FoodItem>>

    // --- Perintah untuk DailyLog ---
    @Insert
    suspend fun insertDailyLog(dailyLog: DailyLog)

    @Query("SELECT * FROM daily_logs WHERE date(date/1000, 'unixepoch', 'localtime') = date('now','localtime')")
    fun getLogsForToday(): LiveData<List<DailyLog>>


    // --- Perintah untuk WeightHistory ---
    @Insert
    suspend fun insertWeightHistory(weightHistory: WeightHistory)

    @Query("SELECT * FROM weight_history ORDER BY date DESC")
    fun getWeightHistory(): LiveData<List<WeightHistory>>

    // Di dalam interface GiziDao
    @Query("DELETE FROM food_items")
    suspend fun deleteAllFoodItems()

    // Di dalam interface GiziDao
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredients(ingredients: List<RecipeIngredient>)

    // Tambahkan fungsi ini di dalam interface GiziDao

    @Query("SELECT * FROM food_items WHERE name LIKE :query")
    suspend fun searchFoodItems(query: String): List<FoodItem>

    // Di dalam interface GiziDao

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun getIngredientsForRecipe(recipeId: Long): List<RecipeIngredient>

}
