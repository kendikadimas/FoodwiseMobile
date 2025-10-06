package com.unsoed.foodwise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.unsoed.foodwise.data.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Data class for nutrition summary
data class NutritionSummary(
    val totalCalories: Int = 0,
    val caloriesPerServing: Int = 0
)

// Changed class name to avoid conflict with RecipeWithDetails.kt
// This class holds processed data for UI display.
data class RecipeDisplayModel(
    val recipe: Recipe,
    val totalCalories: Int,
    val caloriesPerServing: Int,
    val ingredientCount: Int
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val giziDao: GiziDao = AppDatabase.getDatabase(application).giziDao()

    // Search results for ingredients
    private val _searchResults = MutableLiveData<List<FoodItem>>()
    val searchResults: LiveData<List<FoodItem>> = _searchResults

    // Ingredients added to the recipe (Key: FoodItem, Value: weight in grams)
    private val _addedIngredients = MutableLiveData<MutableMap<FoodItem, Int>>(mutableMapOf())
    val addedIngredients: LiveData<MutableMap<FoodItem, Int>> = _addedIngredients

    // Number of servings
    private val _servingCount = MutableLiveData(1)

    // Nutrition summary
    private val _nutritionSummary = MutableLiveData<NutritionSummary>()
    val nutritionSummary: LiveData<NutritionSummary> = _nutritionSummary

    // List of saved recipes
    val savedRecipes: LiveData<List<Recipe>> = giziDao.getAllRecipes()

    // LiveData now uses RecipeDisplayModel
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
            currentMap[foodItem] = 100 // Default 100 grams
            _addedIngredients.postValue(currentMap)
            calculateNutrition()
        }
    }



    fun removeIngredient(foodItem: FoodItem) {
        val currentMap = _addedIngredients.value ?: return
        currentMap.remove(foodItem)
        _addedIngredients.postValue(currentMap)
        calculateNutrition()
    }


    fun updateIngredientQuantity(foodItem: FoodItem, grams: Int) {
        val currentMap = _addedIngredients.value ?: return
        if (currentMap.containsKey(foodItem) && currentMap[foodItem] != grams) {
            currentMap[foodItem] = grams
            _addedIngredients.postValue(currentMap) // Use postValue for thread safety
            calculateNutrition()
        }
    }

    fun updateServingCount(servings: Int) {
        val currentServings = _servingCount.value ?: 1
        if (currentServings != servings) {
            _servingCount.postValue(if (servings > 0) servings else 1)
            calculateNutrition()
        }
    }

    private fun calculateNutrition() {
        val ingredients = _addedIngredients.value ?: emptyMap()
        val servings = _servingCount.value ?: 1

        val baseTotalCalories = ingredients.entries.sumOf { (foodItem, grams) ->
            (foodItem.calories / 100.0) * grams
        }.roundToInt()

        val finalTotalCalories = baseTotalCalories * servings

        _nutritionSummary.postValue(NutritionSummary(
            totalCalories = finalTotalCalories,
            caloriesPerServing = baseTotalCalories
        ))
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
            val detailedList = mutableListOf<RecipeDisplayModel>()

            for (recipe in recipes) {
                val ingredients = giziDao.getIngredientsForRecipe(recipe.recipeId)
                val totalCalories = ingredients.sumOf { ingredient ->
                    val foodItem = allFoodItems.find { it.foodId == ingredient.foodId }
                    val calories = foodItem?.calories ?: 0
                    (calories / 100.0 * ingredient.quantityInGrams)
                }.roundToInt()

                val caloriesPerServing = if (recipe.servingCount > 0) totalCalories / recipe.servingCount else 0

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
        _searchResults.postValue(emptyList())
        _addedIngredients.postValue(mutableMapOf())
        _servingCount.postValue(1)
        calculateNutrition()
    }
}