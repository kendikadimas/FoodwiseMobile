package com.unsoed.foodwise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.unsoed.foodwise.data.AppDatabase
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.data.GiziDao // Import DAO
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    // Langsung berinteraksi dengan DAO, bukan Repository
    private val giziDao: GiziDao
    val allFoodItems: LiveData<List<FoodItem>>

    init {
        giziDao = AppDatabase.getDatabase(application).giziDao()
        allFoodItems = giziDao.getAllFoodItems()
    }

    /**
     * Fungsi untuk memasukkan FoodItem baru ke database.
     * Digunakan oleh fitur 'Tambah Makanan Manual' atau setelah memilih dari API (jika nanti diaktifkan lagi).
     */
    fun insertFoodItem(foodItem: FoodItem, onInserted: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = giziDao.insertFoodItem(foodItem)
            onInserted(newId)
        }
    }

    // SEMUA FUNGSI TERKAIT API (searchFood, searchResults, dll.) SUDAH DIHAPUS.
}