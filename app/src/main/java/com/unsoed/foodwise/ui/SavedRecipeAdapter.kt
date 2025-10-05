package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.databinding.ItemSavedRecipeBinding

class SavedRecipeAdapter(
    private var items: List<RecipeWithDetails>
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvRecipeName.text = item.recipe.name

        val caloriesPerServing = if (item.recipe.servingCount > 0)
            item.totalCalories / item.recipe.servingCount
        else 0

        holder.binding.tvRecipeDetails.text = "$caloriesPerServing kkal per porsi"
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<RecipeWithDetails>) {
        items = newItems
        notifyDataSetChanged()
    }
}
