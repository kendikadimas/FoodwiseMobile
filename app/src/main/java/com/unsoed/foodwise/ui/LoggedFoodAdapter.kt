package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.unsoed.foodwise.R
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemLoggedFoodBinding

class LoggedFoodAdapter(private var loggedFoods: List<FoodItem>) : RecyclerView.Adapter<LoggedFoodAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLoggedFoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoggedFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = loggedFoods.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = loggedFoods[position]
        holder.binding.tvFoodName.text = food.name
        holder.binding.tvFoodCalories.text = "${food.calories} kkal"
        holder.binding.tvFoodServing.text = "1 porsi" // TODO: Ganti dengan data porsi asli

        holder.binding.ivFoodImage.load(food.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background) // Ganti dengan placeholder yang sesuai
            error(R.drawable.ic_launcher_background)
        }
    }

    fun updateData(newLoggedFoods: List<FoodItem>) {
        loggedFoods = newLoggedFoods
        notifyDataSetChanged()
    }
}