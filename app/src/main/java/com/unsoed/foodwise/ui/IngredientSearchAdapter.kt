package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemIngredientSearchBinding

class IngredientSearchAdapter(private var items: List<FoodItem>) : RecyclerView.Adapter<IngredientSearchAdapter.ViewHolder>() {

    private var onAddClickListener: ((FoodItem) -> Unit)? = null
    fun setOnAddClickListener(listener: (FoodItem) -> Unit) { onAddClickListener = listener }

    class ViewHolder(val binding: ItemIngredientSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIngredientSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvIngredientName.text = item.name
        holder.binding.tvIngredientCalories.text = "${item.calories} kkal / 100g"
        holder.binding.btnAddIngredient.setOnClickListener { onAddClickListener?.invoke(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<FoodItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}