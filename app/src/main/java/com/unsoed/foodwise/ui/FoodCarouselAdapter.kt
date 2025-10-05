package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.unsoed.foodwise.R
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemFoodCardBinding // Pastikan nama file layout item Anda adalah item_food_card.xml

class FoodCarouselAdapter(private var foodItems: List<FoodItem>) : RecyclerView.Adapter<FoodCarouselAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFoodCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = foodItems[position]
        holder.binding.foodName.text = item.name
        holder.binding.foodCalories.text = "${item.calories} kkal"

        // Muat gambar dari URL menggunakan Coil
        holder.binding.cardImage.load(item.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background) // Ganti dengan gambar placeholder Anda
            error(R.drawable.ic_launcher_background)
        }
    }

    override fun getItemCount(): Int = foodItems.size

    /**
     * INI FUNGSI YANG HILANG.
     * Fungsi ini digunakan untuk memperbarui daftar makanan di dalam adapter
     * dan memberitahu RecyclerView untuk menggambar ulang tampilannya.
     */
    fun updateData(newFoodItems: List<FoodItem>) {
        foodItems = newFoodItems
        notifyDataSetChanged() // Perintah untuk refresh tampilan
    }
}