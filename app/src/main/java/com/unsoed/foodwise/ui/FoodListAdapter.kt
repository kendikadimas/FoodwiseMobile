package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.unsoed.foodwise.R
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemFoodBinding

// 1. Ubah parameter constructor menjadi List<FoodItem>
class FoodListAdapter(private var foodList: List<FoodItem>) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount() = foodList.size

    // 2. Ubah onBindViewHolder untuk bekerja dengan FoodItem
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentFood = foodList[position]
        holder.binding.tvFoodName.text = currentFood.name
        holder.binding.tvFoodCalories.text = "${currentFood.calories} kkal"

        // Muat gambar dari imageUrl (jika ada)
        holder.binding.ivFoodImage.load(currentFood.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }

        // Set listener di sini
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(currentFood)
        }
    }

    // 3. Ubah tipe data listener menjadi FoodItem
    private var onItemClickListener: ((FoodItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (FoodItem) -> Unit) {
        onItemClickListener = listener
    }

    // 4. Ubah fungsi updateData untuk menerima List<FoodItem>
    fun updateData(newFoodList: List<FoodItem>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}