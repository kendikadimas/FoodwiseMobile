package com.unsoed.foodwise.ui // <-- Sesuaikan package Anda

import android.app.Application
import androidx.lifecycle.*
import com.unsoed.foodwise.data.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Langsung berinteraksi dengan DAO, bukan Repository
    private val giziDao: GiziDao

    val userProfile: LiveData<UserProfile?>
    val allFoodItems: LiveData<List<FoodItem>>
    val logsForToday: LiveData<List<DailyLog>>
    val weightHistoryList: LiveData<List<WeightHistory>>

    init {
        // Inisialisasi DAO
        giziDao = AppDatabase.getDatabase(application).giziDao()

        // Ambil data langsung dari DAO
        userProfile = giziDao.getUserProfile()
        allFoodItems = giziDao.getAllFoodItems()
        weightHistoryList = giziDao.getWeightHistory()

        // Logika untuk rentang tanggal hari ini
        logsForToday = giziDao.getLogsForToday()

    }

    fun addWeightHistory(weight: Double) {
        viewModelScope.launch {
            val newEntry = WeightHistory(date = Date(), weight = weight)
            giziDao.insertWeightHistory(newEntry) // Panggil DAO langsung
        }
    }

    fun updateTargetWeight(newTarget: Double) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(targetWeight = newTarget)
                giziDao.insertOrUpdateUserProfile(updatedProfile) // Panggil DAO langsung
            }
        }
    }

    fun addDailyLog(foodId: Long, mealType: String) {
        viewModelScope.launch {
            val newLog = DailyLog(date = Date(), mealType = mealType, foodId = foodId)
            giziDao.insertDailyLog(newLog) // Panggil DAO langsung
        }
    }
}