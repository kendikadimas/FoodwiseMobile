package com.unsoed.foodwise.ui

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.unsoed.foodwise.data.UserProfile
import com.unsoed.foodwise.data.WeightHistory
import com.unsoed.foodwise.databinding.FragmentProgressBinding
import java.util.*
import kotlin.math.abs

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    // CUKUP GUNAKAN SATU VIEWMODEL INI
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var historyAdapter: WeightHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModels()
    }

    private fun observeViewModels() {
        // Observer ini akan berjalan setiap kali riwayat berat ATAU profil pengguna berubah
        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            mainViewModel.weightHistoryList.observe(viewLifecycleOwner) { historyList ->
                if (userProfile != null) {
                    if (historyList.isNullOrEmpty()) {
                        binding.contentLayout.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.VISIBLE
                        // Update kartu target meskipun riwayat kosong
                        binding.tvTargetWeight.text = String.format("%.1f Kg", userProfile.targetWeight ?: userProfile.weight)
                    } else {
                        binding.contentLayout.visibility = View.VISIBLE
                        binding.emptyStateLayout.visibility = View.GONE
                        updateUI(historyList, userProfile)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.cardCurrentWeight.setOnClickListener { showUpdateWeightDialog() }
        binding.cardSetTarget.setOnClickListener { showUpdateTargetDialog() }
        binding.btnAddFirstWeight.setOnClickListener { showUpdateWeightDialog() }
    }

    private fun updateUI(history: List<WeightHistory>, user: UserProfile) {
        val latestWeight = history.first().weight
        val startWeight = history.last().weight
        val achievement = latestWeight - startWeight
        val targetWeight = user.targetWeight ?: startWeight

        // Update kartu statistik
        binding.tvAchievement.text = String.format("%.1f Kg", achievement)
        binding.tvStartWeight.text = String.format("%.1f Kg", startWeight)
        binding.tvCurrentWeight.text = String.format("%.1f Kg", latestWeight)
        binding.tvTargetWeight.text = String.format("%.1f Kg", targetWeight)

        // --- LOGIKA PROGRESS BAR BARU YANG UNIVERSAL ---
        val totalJourney = abs(targetWeight - startWeight)
        val distanceTraveled = abs(latestWeight - startWeight)

        if (totalJourney > 0) {
            val progressPercentage = (distanceTraveled / totalJourney * 100).toInt()
            binding.progressBarGoal.progress = progressPercentage.coerceIn(0, 100)
        } else {
            binding.progressBarGoal.progress = 0
        }

        val distanceRemaining = abs(targetWeight - latestWeight)

        if (distanceTraveled >= totalJourney) {
            binding.tvProgressLabel.text = "Selamat, target tercapai!"
            binding.progressBarGoal.progress = 100
        } else {
            binding.tvProgressLabel.text = String.format("%.1f kg lagi menuju target!", distanceRemaining)
        }

        historyAdapter.updateData(history)
        setupChart(history)
    }

    private fun setupRecyclerView() {
        historyAdapter = WeightHistoryAdapter(emptyList())
        binding.rvWeightHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun setupChart(history: List<WeightHistory>) {
        val sortedHistory = history.reversed()
        val entries = sortedHistory.mapIndexed { index, item -> Entry(index.toFloat(), item.weight.toFloat()) }
        val timestamps = sortedHistory.map { it.date.time }

        val dataSet = LineDataSet(entries, "Berat Badan").apply {
            color = Color.parseColor("#9ACD32")
            lineWidth = 3f
            setCircleColor(Color.parseColor("#9ACD32"))
            circleRadius = 5f
            setDrawCircleHole(false)
            valueTextSize = 0f
            setDrawFilled(true)
            fillColor = Color.parseColor("#9ACD32")
            fillAlpha = 30
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.lineChart.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = DateAxisValueFormatter(timestamps)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = (sortedHistory.minOfOrNull { it.weight }?.toFloat() ?: 40f) - 5f
            xAxis.labelCount = 4
            xAxis.granularity = 1f
            setTouchEnabled(true)
            invalidate()
        }
    }

    private fun showUpdateWeightDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val editText = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Masukkan berat badan (kg)"
        }
        builder.setTitle("Catat Berat Badan Baru")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val weightStr = editText.text.toString()
                if (weightStr.isNotBlank()) {
                    mainViewModel.addWeightHistory(weightStr.toDouble())
                }
            }
            .setNegativeButton("Batal", null)
        builder.create().show()
    }

    private fun showUpdateTargetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val editText = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Masukkan berat target (kg)"
        }
        builder.setTitle("Ubah Target Berat Badan")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val targetStr = editText.text.toString()
                if (targetStr.isNotBlank()) {
                    // SEKARANG MEMANGGIL FUNGSI DARI MAINVIEWMODEL
                    mainViewModel.updateTargetWeight(targetStr.toDouble())
                }
            }
            .setNegativeButton("Batal", null)
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}