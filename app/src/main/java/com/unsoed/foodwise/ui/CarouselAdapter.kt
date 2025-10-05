package com.unsoed.foodwise.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.databinding.ItemCarouselPlaceholderBinding

class CarouselAdapter : RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

    // Daftar warna placeholder untuk 4 slide
    private val colors = listOf(
        Color.parseColor("#E0E0E0"), // Abu-abu
        Color.parseColor("#C8E6C9"), // Hijau muda
        Color.parseColor("#BBDEFB"), // Biru muda
        Color.parseColor("#FFCCBC")  // Oranye muda
    )

    class ViewHolder(val binding: ItemCarouselPlaceholderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCarouselPlaceholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set warna background sesuai posisi slide
        holder.binding.placeholderBackground.setBackgroundColor(colors[position])
    }

    // Kita punya 4 slide
    override fun getItemCount(): Int = 4
}