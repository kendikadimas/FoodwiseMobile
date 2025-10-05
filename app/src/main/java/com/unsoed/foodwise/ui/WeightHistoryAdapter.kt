package com.unsoed.foodwise.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.foodwise.data.WeightHistory
import com.unsoed.foodwise.databinding.ItemProgressHistoryBinding // <-- UBAH INI
import java.text.SimpleDateFormat
import java.util.*

class WeightHistoryAdapter(private var historyList: List<WeightHistory>) : RecyclerView.Adapter<WeightHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProgressHistoryBinding) : RecyclerView.ViewHolder(binding.root) // <-- UBAH INI

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProgressHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false) // <-- UBAH INI
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        holder.binding.tvDate.text = sdf.format(item.date)
        holder.binding.tvWeight.text = "${item.weight} kg"
    }

    override fun getItemCount() = historyList.size

    fun updateData(newHistory: List<WeightHistory>) {
        historyList = newHistory
        notifyDataSetChanged()
    }
}