package com.unsoed.foodwise.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.unsoed.foodwise.MainActivity
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.data.UserProfile
import com.unsoed.foodwise.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    private var waterCount = 0
    private val waterTarget = 8

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        setupWaterTracker()
        setupClickListeners()
        observeViewModel()
        updateHeader()
        updateGreetingCard()
    }

    private fun updateHeader() {
        val user = firebaseAuth.currentUser
        val userName = user?.email?.split("@")?.get(0)?.replaceFirstChar { it.titlecase() } ?: "Pengguna"
        binding.tvGreeting.text = "Halo, $userName!"

        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val currentDate = sdf.format(Date())
        binding.tvDate.text = currentDate
    }

    private fun updateGreetingCard() {
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 5..10 -> {
                binding.tvGreetingTitle.text = "Sudah sarapan hari ini?"
                binding.tvGreetingSubtitle.text = "Awali harimu dengan nutrisi!"
            }
            in 11..15 -> {
                binding.tvGreetingTitle.text = "Waktunya Makan Siang!"
                binding.tvGreetingSubtitle.text = "Apa yang kamu makan?"
            }
            in 16..21 -> {
                binding.tvGreetingTitle.text = "Jangan lupa makan malam"
                binding.tvGreetingSubtitle.text = "Catat asupanmu hari ini."
            }
            else -> {
                binding.tvGreetingTitle.text = "Jaga Pola Makanmu"
                binding.tvGreetingSubtitle.text = "Sudah catat camilanmu?"
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            viewModel.logsForToday.observe(viewLifecycleOwner) { dailyLogs ->
                val allFoods = viewModel.allFoodItems.value ?: emptyList()
                if (userProfile != null) {
                    updateCalorieCard(userProfile, dailyLogs, allFoods)
                }
            }
        }
    }

    private fun updateCalorieCard(user: UserProfile, logs: List<com.unsoed.foodwise.data.DailyLog>, allFoods: List<FoodItem>) {
        val targetCalories = calculateTDEE(user)
        val consumedFoods = logs.mapNotNull { log -> allFoods.find { it.foodId == log.foodId } }
        val consumedCalories = consumedFoods.sumOf { it.calories }
        val remainingCalories = targetCalories - consumedCalories
        val progress = if (targetCalories > 0) (consumedCalories * 100) / targetCalories else 0

        binding.tvCaloriesRemaining.text = remainingCalories.toString()
        binding.tvCaloriesTarget.text = "dari $targetCalories kkal"
        binding.progressBarCalories.progress = progress
    }

    private fun setupWaterTracker() {
        val sharedPref = requireActivity().getSharedPreferences("WaterTracker", Context.MODE_PRIVATE)
        waterCount = sharedPref.getInt("waterCount_${getCurrentDateString()}", 0)
        updateWaterUI()

        binding.btnAddWater.setOnClickListener {
            if (waterCount < waterTarget) {
                waterCount++
                saveAndupdateWater()
            }
        }
        binding.btnRemoveWater.setOnClickListener {
            if (waterCount > 0) {
                waterCount--
                saveAndupdateWater()
            }
        }
    }

    private fun saveAndupdateWater() {
        val sharedPref = requireActivity().getSharedPreferences("WaterTracker", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("waterCount_${getCurrentDateString()}", waterCount)
            apply()
        }
        updateWaterUI()
    }

    private fun updateWaterUI() {
        binding.tvWaterCount.text = "$waterCount / $waterTarget"
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun setupClickListeners() {
        val mainActivity = activity as? MainActivity
        binding.cardDiary.setOnClickListener { mainActivity?.loadFragment(DiaryFragment()) }
        binding.cardProgress.setOnClickListener { mainActivity?.loadFragment(ProgressFragment()) }
        binding.cardRecipe.setOnClickListener { mainActivity?.loadFragment(RecipeFragment()) }
        binding.cardKuliner.setOnClickListener { mainActivity?.loadFragment(DiscoveryFragment()) }
        binding.cardNextMeal.setOnClickListener { mainActivity?.loadFragment(DiaryFragment()) }
    }

    private fun calculateTDEE(user: UserProfile): Int {
        val age = user.age
        val bmr = if (user.gender == "Pria") {
            88.362 + (13.397 * user.weight) + (4.799 * user.height) - (5.677 * age)
        } else {
            447.593 + (9.247 * user.weight) + (3.098 * user.height) - (4.330 * age)
        }
        val activityFactor = when {
            user.activityLevel.contains("Jarang") -> 1.2
            user.activityLevel.contains("Ringan") -> 1.375
            user.activityLevel.contains("Sedang") -> 1.55
            else -> 1.725
        }
        var tdee = bmr * activityFactor
        when {
            user.goal.contains("Menurunkan") -> tdee -= 500
            user.goal.contains("Menaikkan") -> tdee += 500
        }
        return tdee.roundToInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}