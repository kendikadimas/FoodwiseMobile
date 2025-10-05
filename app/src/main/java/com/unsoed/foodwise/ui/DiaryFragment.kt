package com.unsoed.foodwise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unsoed.foodwise.R
import com.unsoed.foodwise.data.FoodItem
import com.unsoed.foodwise.data.UserProfile
import com.unsoed.foodwise.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var breakfastAdapter: LoggedFoodAdapter
    private lateinit var lunchAdapter: LoggedFoodAdapter
    private lateinit var dinnerAdapter: LoggedFoodAdapter

    private var currentMealType: String = ""

    private val foodSelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedFoodId = result.data?.getLongExtra("SELECTED_FOOD_ID", -1L) ?: -1L
            if (selectedFoodId != -1L && currentMealType.isNotBlank()) {
                viewModel.addDailyLog(selectedFoodId, currentMealType)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        breakfastAdapter = LoggedFoodAdapter(emptyList())
        binding.rvBreakfast.apply {
            adapter = breakfastAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        lunchAdapter = LoggedFoodAdapter(emptyList())
        binding.rvLunch.apply {
            adapter = lunchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        dinnerAdapter = LoggedFoodAdapter(emptyList())
        binding.rvDinner.apply {
            adapter = dinnerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.btnAddBreakfast.setOnClickListener {
            currentMealType = "Sarapan"
            foodSelectionLauncher.launch(Intent(requireContext(), FoodListActivity::class.java))
        }
        binding.btnAddLunch.setOnClickListener {
            currentMealType = "Makan Siang"
            foodSelectionLauncher.launch(Intent(requireContext(), FoodListActivity::class.java))
        }
        binding.btnAddDinner.setOnClickListener {
            currentMealType = "Makan Malam"
            foodSelectionLauncher.launch(Intent(requireContext(), FoodListActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            viewModel.logsForToday.observe(viewLifecycleOwner) { logs ->
                val allFoods = viewModel.allFoodItems.value ?: emptyList()

                if (userProfile != null) {
                    val consumedFoods = logs.mapNotNull { log -> allFoods.find { it.foodId == log.foodId } }
                    updateSummaryCard(consumedFoods, userProfile)
                    updateMealLists(logs, allFoods)
                }
            }
        }
    }

    private fun updateMealLists(logs: List<com.unsoed.foodwise.data.DailyLog>, allFoods: List<FoodItem>) {
        val breakfastFoods = logs.filter { it.mealType == "Sarapan" }
            .mapNotNull { log -> allFoods.find { it.foodId == log.foodId } }
        breakfastAdapter.updateData(breakfastFoods)

        val lunchFoods = logs.filter { it.mealType == "Makan Siang" }
            .mapNotNull { log -> allFoods.find { it.foodId == log.foodId } }
        lunchAdapter.updateData(lunchFoods)

        val dinnerFoods = logs.filter { it.mealType == "Makan Malam" }
            .mapNotNull { log -> allFoods.find { it.foodId == log.foodId } }
        dinnerAdapter.updateData(dinnerFoods)
    }

    private fun updateSummaryCard(consumedFoods: List<FoodItem>, user: UserProfile) {
        val consumedCalories = consumedFoods.sumOf { it.calories }
        val consumedCarbs = consumedFoods.sumOf { it.carbs }
        val consumedProtein = consumedFoods.sumOf { it.protein }
        val consumedFat = consumedFoods.sumOf { it.fat }

        val targetCalories = calculateTDEE(user)
        // Hitung target makro secara dinamis
        val targetCarbs = (targetCalories * 0.50 / 4).toInt() // 50% karbo
        val targetProtein = (targetCalories * 0.20 / 4).toInt() // 20% protein
        val targetFat = (targetCalories * 0.30 / 9).toInt() // 30% lemak

        // Update UI
        binding.tvTotalCalories.text = consumedCalories.toString()
        binding.tvTargetCalories.text = "/$targetCalories kkal"
        binding.progressBarCalories.progress = if(targetCalories > 0) (consumedCalories * 100) / targetCalories else 0

//        binding.tvCarbsValue.text = "${consumedCarbs.roundToInt()}/${targetCarbs}g"
//        binding.progressBarCarbs.progress = if(targetCarbs > 0) (consumedCarbs.toInt() * 100 / targetCarbs).coerceIn(0, 100) else 0
//
//        binding.tvProteinValue.text = "${consumedProtein.roundToInt()}/${targetProtein}g"
//        binding.progressBarProtein.progress = if(targetProtein > 0) (consumedProtein.toInt() * 100 / targetProtein).coerceIn(0, 100) else 0
//
//        binding.tvFatValue.text = "${consumedFat.roundToInt()}/${targetFat}g"
//        binding.progressBarFat.progress = if(targetFat > 0) (consumedFat.toInt() * 100 / targetFat).coerceIn(0, 100) else 0
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