package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.R
import com.unsoed.foodwise.databinding.ItemFeaturedBannerBinding

// Data class sederhana untuk menampung data banner
data class BannerItem(val title: String, val calories: String, val imageRes: Int)

class FeaturedBannerAdapter(private val bannerItems: List<BannerItem>) : RecyclerView.Adapter<FeaturedBannerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFeaturedBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeaturedBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bannerItems[position]
        holder.binding.ivBannerImage.setImageResource(item.imageRes)
        holder.binding.tvBannerTitle.text = item.title
        holder.binding.tvBannerCalories.text = item.calories
    }

    override fun getItemCount(): Int = bannerItems.size
}