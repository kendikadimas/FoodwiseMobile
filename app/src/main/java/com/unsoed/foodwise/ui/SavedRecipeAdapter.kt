package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.databinding.ItemSavedRecipeBinding

class SavedRecipeAdapter(
    private var recipes: List<RecipeDisplayModel>
) : RecyclerView.Adapter<SavedRecipeAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSavedRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detailedRecipe = recipes[position]
        holder.binding.tvRecipeName.text = detailedRecipe.recipe.name

//        val caloriesPerServing = if (recipes.recipe.servingCount > 0)
//            recipes.totalCalories / recipes.recipe.servingCount
//        else 0

        holder.binding.tvRecipeDetails.text = "${detailedRecipe.caloriesPerServing} kkal per porsi"
    }



    fun updateData(newRecipes: List<RecipeDisplayModel>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
