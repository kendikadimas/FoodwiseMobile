package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.R

class CarouselAdapter : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    // Tambahkan gambar-gambar untuk carousel di sini
    private val images = listOf(
        R.drawable.ic_onboarding, // Ganti dengan nama file gambar kamu
        R.drawable.ic_onboarding_2,
        R.drawable.ic_onboarding_3,
        R.drawable.ic_onboarding_4
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_carousel)

        fun bind(imageRes: Int) {
            imageView.setImageResource(imageRes)
        }
    }
}