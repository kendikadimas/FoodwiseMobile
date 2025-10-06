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
import com.unsoed.foodwise.data.DailyLog
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
        // Setup untuk Sarapan
        breakfastAdapter = LoggedFoodAdapter(emptyList())
        breakfastAdapter.setOnDeleteClickListener { dailyLog ->
            viewModel.deleteDailyLog(dailyLog)
        }
        binding.rvBreakfast.apply {
            adapter = breakfastAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        // Setup untuk Makan Siang
        lunchAdapter = LoggedFoodAdapter(emptyList())
        lunchAdapter.setOnDeleteClickListener { dailyLog ->
            viewModel.deleteDailyLog(dailyLog)
        }
        binding.rvLunch.apply {
            adapter = lunchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        // Setup untuk Makan Malam
        dinnerAdapter = LoggedFoodAdapter(emptyList())
        dinnerAdapter.setOnDeleteClickListener { dailyLog ->
            viewModel.deleteDailyLog(dailyLog)
        }
        binding.rvDinner.apply {
            adapter = dinnerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.btnAddBreakfast.setOnClickListener {
            currentMealType = "Sarapan Pagi"
            // Ganti dengan activity Anda untuk memilih makanan
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

    // Fungsi ini diubah untuk memproses data gabungan
    private fun updateMealLists(logs: List<DailyLog>, allFoods: List<FoodItem>) {
        val breakfastItems = logs.filter { it.mealType == "Sarapan" }
            .mapNotNull { log ->
                allFoods.find { it.foodId == log.foodId }?.let { food ->
                    LoggedItem(log, food)
                }
            }
        breakfastAdapter.updateData(breakfastItems)

        val lunchItems = logs.filter { it.mealType == "Makan Siang" }
            .mapNotNull { log ->
                allFoods.find { it.foodId == log.foodId }?.let { food ->
                    LoggedItem(log, food)
                }
            }
        lunchAdapter.updateData(lunchItems)

        val dinnerItems = logs.filter { it.mealType == "Makan Malam" }
            .mapNotNull { log ->
                allFoods.find { it.foodId == log.foodId }?.let { food ->
                    LoggedItem(log, food)
                }
            }
        dinnerAdapter.updateData(dinnerItems)
    }


    private fun updateSummaryCard(consumedFoods: List<FoodItem>, user: UserProfile) {
        val consumedCalories = consumedFoods.sumOf { it.calories }
        val targetCalories = calculateTDEE(user)

        binding.tvTotalCalories.text = consumedCalories.toString()
        binding.tvTargetCalories.text = "/$targetCalories kkal"
        binding.progressBarCalories.progress = if(targetCalories > 0) (consumedCalories * 100) / targetCalories else 0
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
