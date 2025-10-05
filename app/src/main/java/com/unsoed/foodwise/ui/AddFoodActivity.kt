package com.unsoed.foodwise.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ActivityAddFoodBinding

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private lateinit var viewModel: FoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FoodViewModel::class.java)

        binding.btnSaveFood.setOnClickListener {
            saveFoodItem()
        }
    }

    private fun saveFoodItem() {
        val name = binding.etFoodName.text.toString()
        val calories = binding.etCalories.text.toString().toIntOrNull()
        val protein = binding.etProtein.text.toString().toDoubleOrNull()
        val carbs = binding.etCarbs.text.toString().toDoubleOrNull()
        val fat = binding.etFat.text.toString().toDoubleOrNull()

        if (name.isBlank() || calories == null || protein == null || carbs == null || fat == null) {
            Toast.makeText(this, "Harap isi semua kolom dengan benar", Toast.LENGTH_SHORT).show()
            return
        }

        val foodItem = FoodItem(name = name, calories = calories, protein = protein, carbs = carbs, fat = fat, imageUrl = null)
        viewModel.insertFoodItem(foodItem){}

        Toast.makeText(this, "$name berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        finish() // Tutup activity dan kembali ke daftar makanan
    }
}