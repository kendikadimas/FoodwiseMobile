package com.unsoed.foodwise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.unsoed.foodwise.data.*
import kotlinx.coroutines.launch

data class NutritionSummary(
    val totalCalories: Int = 0,
    val caloriesPerServing: Int = 0
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val giziDao: GiziDao

    private val _searchResults = MutableLiveData<List<FoodItem>>()
    val searchResults: LiveData<List<FoodItem>> = _searchResults

    private val _addedIngredients = MutableLiveData<MutableMap<FoodItem, Int>>(mutableMapOf())
    val addedIngredients: LiveData<MutableMap<FoodItem, Int>> = _addedIngredients

    private val _nutritionSummary = MutableLiveData<NutritionSummary>()
    val nutritionSummary: LiveData<NutritionSummary> = _nutritionSummary

    val savedRecipes: LiveData<List<Recipe>>
    private val _savedRecipesWithDetails = MutableLiveData<List<RecipeWithDetails>>()
    val savedRecipesWithDetails: LiveData<List<RecipeWithDetails>> = _savedRecipesWithDetails

    var servingCount = 1

    init {
        giziDao = AppDatabase.getDatabase(application).giziDao()
        savedRecipes = giziDao.getAllRecipes()
    }

    fun searchIngredients(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.postValue(emptyList())
            } else {
                _searchResults.postValue(giziDao.searchFoodItems("%$query%"))
            }
        }
    }

    fun addIngredient(foodItem: FoodItem) {
        val currentMap = _addedIngredients.value ?: mutableMapOf()
        currentMap[foodItem] = 100 // default 100 gram
        _addedIngredients.postValue(currentMap)
        recalculateTotals()
    }

    fun removeIngredient(foodItem: FoodItem) {
        val currentMap = _addedIngredients.value ?: return
        currentMap.remove(foodItem)
        _addedIngredients.postValue(currentMap)
        recalculateTotals()
    }

    fun updateIngredientQuantity(foodItem: FoodItem, grams: Int) {
        val currentMap = _addedIngredients.value ?: return
        if (currentMap.containsKey(foodItem)) {
            currentMap[foodItem] = grams
            recalculateTotals()
        }
    }

    fun updateServingCount(count: Int) {
        servingCount = if (count > 0) count else 1
        recalculateTotals()
    }

    private fun recalculateTotals() {
        val currentMap = _addedIngredients.value ?: mutableMapOf()
        var totalCalories = 0.0

        for ((foodItem, grams) in currentMap) {
            val caloriesPerGram = foodItem.calories / 100.0
            totalCalories += caloriesPerGram * grams
        }

        val caloriesPerServing = if (servingCount > 0) totalCalories / servingCount else 0.0

        _nutritionSummary.postValue(
            NutritionSummary(
                totalCalories = totalCalories.toInt(),
                caloriesPerServing = caloriesPerServing.toInt()
            )
        )
    }

    fun saveRecipe(name: String) {
        val ingredientsMap = _addedIngredients.value ?: return
        if (name.isBlank() || ingredientsMap.isEmpty()) return

        viewModelScope.launch {
            val newRecipe = Recipe(name = name, servingCount = servingCount)
            val recipeId = giziDao.insertRecipe(newRecipe)

            val recipeIngredients = ingredientsMap.map { (foodItem, grams) ->
                RecipeIngredient(recipeId = recipeId, foodId = foodItem.foodId, quantityInGrams = grams)
            }

            giziDao.insertRecipeIngredients(recipeIngredients)
        }
    }

    // ================== PERBAIKAN DI SINI ==================
    fun calculateSavedRecipesDetails(allFoodItems: List<FoodItem>) {
        val recipes = savedRecipes.value ?: return

        viewModelScope.launch {
            val detailedList = mutableListOf<RecipeWithDetails>()
            for (recipe in recipes) {
                // Ambil daftar bahan untuk resep ini
                val ingredients = giziDao.getIngredientsForRecipe(recipe.recipeId.toLong())

                // Hitung total kalori
                var totalCalories = 0.0
                for (ingredient in ingredients) {
                    val foodItem = allFoodItems.find { it.foodId == ingredient.foodId }
                    if (foodItem != null) {
                        val caloriesPerGram = foodItem.calories / 100.0
                        totalCalories += caloriesPerGram * ingredient.quantityInGrams
                    }
                }

                // Simpan hasil dengan struktur data yang sudah diperbarui
                detailedList.add(
                    RecipeWithDetails(
                        recipe = recipe,
                        ingredients = ingredients,
                        totalCalories = totalCalories.toInt()
                    )
                )
            }

            _savedRecipesWithDetails.postValue(detailedList)
        }
    }
}
