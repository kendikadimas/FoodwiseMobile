package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.unsoed.foodwise.R
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemFoodBinding

// Adapter ini akan menerima daftar FoodItem, bukan DailyLog
// Karena kita butuh nama dan kalori untuk ditampilkan
class DailyLogAdapter(private var loggedFoods: List<FoodItem>) : RecyclerView.Adapter<DailyLogAdapter.LogViewHolder>() {

    class LogViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun getItemCount() = loggedFoods.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val food = loggedFoods[position]
        holder.binding.tvFoodName.text = food.name
        holder.binding.tvFoodCalories.text = "${food.calories} kkal"

        // Perintah untuk Coil memuat gambar
        holder.binding.ivFoodImage.load(food.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
    }

    fun updateData(newLoggedFoods: List<FoodItem>) {
        loggedFoods = newLoggedFoods
        notifyDataSetChanged()
    }
}