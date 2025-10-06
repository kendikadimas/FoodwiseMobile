package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.unsoed.foodwise.R
import com.unsoed.foodwise.data.DailyLog
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ItemLoggedFoodBinding


data class LoggedItem(val log: DailyLog, val food: FoodItem)
class LoggedFoodAdapter(private var loggedItems: List<LoggedItem>) : RecyclerView.Adapter<LoggedFoodAdapter.ViewHolder>() {

    private var onDeleteClickListener: ((DailyLog) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (DailyLog) -> Unit) {
        onDeleteClickListener = listener
    }

    class ViewHolder(val binding: ItemLoggedFoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoggedFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = loggedItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loggedItem = loggedItems[position]
        val food = loggedItem.food
        val log = loggedItem.log

        holder.binding.tvFoodName.text = food.name
        holder.binding.tvFoodCalories.text = "${food.calories} kkal"
        holder.binding.tvFoodServing.text = "${log.servingSize} porsi" // TODO: Ganti dengan data porsi asli

        holder.binding.ivFoodImage.load(food.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground) // Ganti dengan placeholder yang sesuai
            error(R.drawable.ic_launcher_foreground)
        }
        holder.binding.btnDeleteLog.setOnClickListener {
            onDeleteClickListener?.invoke(log)
        }
    }

    fun updateData(newLoggedItems: List<LoggedItem>) {
        loggedItems = newLoggedItems
        notifyDataSetChanged()
    }
}