package com.unsoed.foodwise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.unsoed.foodwise.data.AppDatabase
import com.unsoed.foodwise.data.GiziDao // Import DAO
import com.unsoed.foodwise.data.UserProfile
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // Langsung berinteraksi dengan DAO
    private val giziDao: GiziDao
    val userProfile: LiveData<UserProfile?>

    init {
        // Inisialisasi DAO
        giziDao = AppDatabase.getDatabase(application).giziDao()
        // Ambil data langsung dari DAO
        userProfile = giziDao.getUserProfile()
    }

    /**
     * Fungsi untuk menyimpan data profil pengguna baru atau memperbaruinya.
     */
    fun saveUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            giziDao.insertOrUpdateUserProfile(userProfile)
        }
    }

    /**
     * Fungsi untuk memperbarui target berat badan.
     * Catatan: Fungsi ini juga ada di MainViewModel. Memusatkannya di satu tempat
     * (misalnya hanya di MainViewModel) akan lebih baik untuk konsistensi.
     */
    fun updateTargetWeight(newTarget: Double) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(targetWeight = newTarget)
                giziDao.insertOrUpdateUserProfile(updatedProfile)
            }
        }
    }
}