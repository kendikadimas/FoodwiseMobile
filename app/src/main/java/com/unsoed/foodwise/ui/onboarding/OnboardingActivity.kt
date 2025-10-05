package com.unsoed.foodwise.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.unsoed.foodwise.MainActivity
import com.unsoed.foodwise.data.UserProfile
import com.unsoed.foodwise.databinding.ActivityOnboardingBinding
import com.unsoed.foodwise.ui.MainViewModel
import com.unsoed.foodwise.ui.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var mainViewModel: MainViewModel // <-- TAMBAHKAN INI
    private lateinit var onboardingAdapter: OnboardingAdapter
    private val totalSteps = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java) // <-- TAMBAHKAN INI

        onboardingAdapter = OnboardingAdapter(this)
        binding.viewPagerOnboarding.adapter = onboardingAdapter
        binding.viewPagerOnboarding.isUserInputEnabled = false

        binding.progressBar.max = 100 * totalSteps
        binding.progressBar.progress = 100

        binding.btnNext.setOnClickListener {
            val currentStep = binding.viewPagerOnboarding.currentItem
            if (currentStep < totalSteps - 1) {
                binding.viewPagerOnboarding.currentItem = currentStep + 1
                binding.progressBar.progress = 100 * (currentStep + 2)
            } else {
                saveAllData()
            }

            if (binding.viewPagerOnboarding.currentItem == totalSteps - 1) {
                binding.btnNext.text = "Selesai"
            }
        }
    }

    private fun saveAllData() {
        val gender = onboardingViewModel.gender.value
        val age = onboardingViewModel.age.value?.toIntOrNull()
        val weight = onboardingViewModel.weight.value?.toDoubleOrNull()
        val height = onboardingViewModel.height.value?.toIntOrNull()
        val activityLevel = onboardingViewModel.activityLevel.value
        val goal = onboardingViewModel.goal.value

        if (gender.isNullOrBlank() || age == null || weight == null || height == null || activityLevel.isNullOrBlank() || goal.isNullOrBlank()) {
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val userProfile = UserProfile(
            id = 1,
            gender = gender,
            age = age,
            weight = weight,
            height = height,
            activityLevel = activityLevel,
            goal = goal
        )
        profileViewModel.saveUserProfile(userProfile)

        // ===== PERBAIKAN UTAMA ADA DI SINI =====
        // Simpan juga berat badan awal sebagai catatan pertama di riwayat
        mainViewModel.addWeightHistory(weight)
        // ======================================

        Toast.makeText(this, "Profil berhasil dibuat!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}