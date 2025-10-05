package com.unsoed.foodwise.ui

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateAxisValueFormatter(private val timestamps: List<Long>) : ValueFormatter() {
    private val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        // value adalah index, kita ambil timestamp dari index tersebut
        val index = value.toInt()
        return if (index >= 0 && index < timestamps.size) {
            sdf.format(Date(timestamps[index]))
        } else {
            ""
        }
    }
}