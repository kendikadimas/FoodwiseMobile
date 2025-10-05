package com.unsoed.foodwise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.databinding.ActivityFoodListBinding

class FoodListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodListBinding
    private lateinit var foodAdapter: FoodListAdapter

    // Gunakan MainViewModel untuk mendapatkan daftar semua makanan
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupRecyclerView()
        setupSearchView()

        // Amati daftar semua makanan dari database
        viewModel.allFoodItems.observe(this) { foodList ->
            foodAdapter.updateData(foodList)
        }
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodListAdapter(emptyList()) // Mulai dengan daftar kosong
        binding.rvFoodList.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@FoodListActivity)
        }

        // Saat item makanan di dalam daftar diklik...
        foodAdapter.setOnItemClickListener { foodItem ->
            // Siapkan intent untuk mengirim data kembali
            val resultIntent = Intent()
            // Masukkan ID dari makanan yang dipilih
            resultIntent.putExtra("SELECTED_FOOD_ID", foodItem.foodId)
            // Kirim hasilnya kembali ke halaman sebelumnya (DiaryFragment)
            setResult(Activity.RESULT_OK, resultIntent)
            // Tutup halaman ini
            finish()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter daftar makanan berdasarkan input pencarian
                val fullList = viewModel.allFoodItems.value ?: emptyList()
                if (newText.isNullOrBlank()) {
                    foodAdapter.updateData(fullList)
                } else {
                    val filteredList = fullList.filter {
                        it.name.contains(newText, ignoreCase = true)
                    }
                    foodAdapter.updateData(filteredList)
                }
                return true
            }
        })
    }
}