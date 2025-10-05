package com.unsoed.foodwise.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemAddedIngredientBinding

class AddedIngredientAdapter(private var items: MutableMap<FoodItem, Int>) : RecyclerView.Adapter<AddedIngredientAdapter.ViewHolder>() {

    private var onQuantityChangedListener: ((FoodItem, Int) -> Unit)? = null
    fun setOnQuantityChangedListener(listener: (FoodItem, Int) -> Unit) { onQuantityChangedListener = listener }

    private var onRemoveClickListener: ((FoodItem) -> Unit)? = null
    fun setOnRemoveClickListener(listener: (FoodItem) -> Unit) { onRemoveClickListener = listener }

    class ViewHolder(val binding: ItemAddedIngredientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddedIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = items.keys.toList()[position]
        val quantity = items[foodItem] ?: 0

        holder.binding.tvAddedIngredientName.text = foodItem.name
        holder.binding.etQuantity.setText(quantity.toString())

        val caloriesPerGram = foodItem.calories / 100.0
        val calculatedCalories = (caloriesPerGram * quantity).toInt()
        holder.binding.tvCalculatedCalories.text = "$calculatedCalories kkal"

        holder.binding.btnRemoveIngredient.setOnClickListener { onRemoveClickListener?.invoke(foodItem) }

        // Listener untuk EditText (cukup rumit, perhatikan)
        holder.binding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newQuantity = s.toString().toIntOrNull() ?: 0
                onQuantityChangedListener?.invoke(foodItem, newQuantity)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: MutableMap<FoodItem, Int>) {
        items = newItems
        notifyDataSetChanged()
    }
}