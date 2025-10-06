package com.unsoed.foodwise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.unsoed.foodwise.data.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Data class untuk ringkasan nutrisi
data class NutritionSummary(
    val totalCalories: Int = 0,
    val caloriesPerServing: Int = 0
)

// PERUBAHAN: Nama kelas diubah untuk menghindari konflik dengan file RecipeWithDetails.kt
// Kelas ini digunakan untuk menampung data yang sudah diolah untuk ditampilkan di UI.
data class RecipeDisplayModel(
    val recipe: Recipe,
    val totalCalories: Int,
    val caloriesPerServing: Int,
    val ingredientCount: Int
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val giziDao: GiziDao = AppDatabase.getDatabase(application).giziDao()

    // Hasil pencarian bahan
    private val _searchResults = MutableLiveData<List<FoodItem>>()
    val searchResults: LiveData<List<FoodItem>> = _searchResults

    // Bahan yang ditambahkan ke resep (Key: FoodItem, Value: berat dalam gram)
    private val _addedIngredients = MutableLiveData<MutableMap<FoodItem, Int>>(mutableMapOf())
    val addedIngredients: LiveData<MutableMap<FoodItem, Int>> = _addedIngredients

    // Jumlah porsi
    private val _servingCount = MutableLiveData(1)

    // Ringkasan nutrisi
    private val _nutritionSummary = MutableLiveData<NutritionSummary>()
    val nutritionSummary: LiveData<NutritionSummary> = _nutritionSummary

    // Daftar resep yang disimpan
    val savedRecipes: LiveData<List<Recipe>> = giziDao.getAllRecipes()

    // PERUBAHAN: LiveData sekarang menggunakan RecipeDisplayModel
    private val _savedRecipesWithDetails = MutableLiveData<List<RecipeDisplayModel>>()
    val savedRecipesWithDetails: LiveData<List<RecipeDisplayModel>> = _savedRecipesWithDetails


    fun searchIngredients(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                val results = giziDao.searchFoodItems("%$query%")
                _searchResults.postValue(results)
            }
        }
    }

    fun addIngredient(foodItem: FoodItem) {
        val currentMap = _addedIngredients.value ?: mutableMapOf()
        if (!currentMap.containsKey(foodItem)) {
            currentMap[foodItem] = 100 // Default 100 gram
            _addedIngredients.value = currentMap
            calculateNutrition()
        }
    }

    fun removeIngredient(foodItem: FoodItem) {
        val currentMap = _addedIngredients.value ?: return
        currentMap.remove(foodItem)
        _addedIngredients.value = currentMap
        calculateNutrition()
    }


    fun updateIngredientQuantity(foodItem: FoodItem, grams: Int) {
        val currentMap = _addedIngredients.value ?: return
        if (currentMap.containsKey(foodItem)) {
            currentMap[foodItem] = grams
            _addedIngredients.value = currentMap // Trigger update
            calculateNutrition()
        }
    }

    fun updateServingCount(servings: Int) {
        _servingCount.value = if (servings > 0) servings else 1
        calculateNutrition()
    }

    private fun calculateNutrition() {
        val ingredients = _addedIngredients.value ?: emptyMap()
        val servings = _servingCount.value ?: 1

        // Menghitung total kalori dasar dari semua bahan yang ditambahkan
        val baseTotalCalories = ingredients.entries.sumOf { (foodItem, grams) ->
            (foodItem.calories / 100.0) * grams
        }.roundToInt()

        // ================== PERUBAHAN LOGIKA BUG ==================
        // Kalikan kalori dasar dengan jumlah porsi untuk mendapatkan total akhir.
        val finalTotalCalories = baseTotalCalories * servings
        // ==========================================================

        // "Total Resep" akan menampilkan hasil perkalian.
        // "Per Porsi" akan menampilkan kalori dasar untuk 1 porsi.
        _nutritionSummary.value = NutritionSummary(
            totalCalories = finalTotalCalories,
            caloriesPerServing = baseTotalCalories
        )
    }

    fun saveRecipe(recipeName: String) {
        viewModelScope.launch {
            val ingredientsMap = _addedIngredients.value ?: return@launch
            if (ingredientsMap.isEmpty()) return@launch

            val newRecipe = Recipe(name = recipeName, servingCount = _servingCount.value ?: 1)
            val recipeId = giziDao.insertRecipe(newRecipe)

            val recipeIngredients = ingredientsMap.map { (foodItem, grams) ->
                RecipeIngredient(recipeId = recipeId, foodId = foodItem.foodId, quantityInGrams = grams)
            }

            giziDao.insertRecipeIngredients(recipeIngredients)
        }
    }

    fun calculateSavedRecipesDetails(allFoodItems: List<FoodItem>) {
        viewModelScope.launch {
            val recipes = savedRecipes.value ?: return@launch
            // PERUBAHAN: List sekarang menggunakan RecipeDisplayModel
            val detailedList = mutableListOf<RecipeDisplayModel>()

            for (recipe in recipes) {
                val ingredients = giziDao.getIngredientsForRecipe(recipe.recipeId)
                val totalCalories = ingredients.sumOf { ingredient ->
                    val foodItem = allFoodItems.find { it.foodId == ingredient.foodId }
                    val calories = foodItem?.calories ?: 0
                    (calories / 100.0 * ingredient.quantityInGrams)
                }.roundToInt()

                val caloriesPerServing = if (recipe.servingCount > 0) totalCalories / recipe.servingCount else 0

                // PERUBAHAN: Membuat instance dari RecipeDisplayModel
                detailedList.add(
                    RecipeDisplayModel(
                        recipe = recipe,
                        totalCalories = totalCalories,
                        caloriesPerServing = caloriesPerServing,
                        ingredientCount = ingredients.size
                    )
                )
            }
            _savedRecipesWithDetails.postValue(detailedList)
        }
    }

    fun clearRecipeData() {
        _searchResults.value = emptyList()
        _addedIngredients.value = mutableMapOf()
        _servingCount.value = 1
        calculateNutrition()
    }
}

