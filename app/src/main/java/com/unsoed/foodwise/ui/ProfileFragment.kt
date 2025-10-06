package com.unsoed.foodwise.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.unsoed.foodwise.data.UserProfile
import com.unsoed.foodwise.data.WeightHistory
import com.unsoed.foodwise.databinding.FragmentProfileBinding
import com.unsoed.foodwise.ui.onboarding.OnboardingActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        setupClickListeners()
        observeViewModel()
        updateStreak()
        updateUsername()
    }

    private fun updateUsername() {
        val user = firebaseAuth.currentUser
        val userName = user?.email?.split("@")?.get(0)?.replaceFirstChar { it.titlecase() } ?: "Pengguna"
        binding.tvUsername.text = userName

        // Member since dari Firebase user creation time
        user?.metadata?.creationTimestamp?.let { timestamp ->
            val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            val date = Date(timestamp)
            binding.tvMemberSince.text = "Member sejak ${sdf.format(date)}"
        }
    }

    private fun observeViewModel() {
        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            mainViewModel.weightHistoryList.observe(viewLifecycleOwner) { historyList ->
                if (userProfile != null) {
                    updateProfileUI(userProfile, historyList)
                }
            }
        }
    }

    private fun updateProfileUI(user: UserProfile, history: List<WeightHistory>?) {
        // Update statistics
        val currentWeight = history?.firstOrNull()?.weight ?: user.weight
        val heightInMeters = user.height / 100.0
        val bmi = currentWeight / (heightInMeters.pow(2))

        binding.tvCurrentWeightStat.text = String.format("%.1f kg", currentWeight)
        binding.tvHeightStat.text = "${user.height} cm"
        binding.tvBmiStat.text = String.format("%.1f", bmi)

        // Update progress bar
        if (!history.isNullOrEmpty()) {
            val startWeight = history.lastOrNull()?.weight ?: user.weight
            val targetWeight = user.targetWeight ?: startWeight
            val totalJourney = abs(targetWeight - startWeight)
            val distanceTraveled = abs(currentWeight - startWeight)

            binding.tvStartWeightProgress.text = "Awal: ${String.format("%.1f", startWeight)} kg"
            binding.tvTargetWeightProgress.text = "Target: ${String.format("%.1f", targetWeight)} kg"

            if (totalJourney > 0) {
                val progressPercentage = ((distanceTraveled / totalJourney) * 100).toInt()
                binding.progressBarWeight.progress = progressPercentage.coerceIn(0, 100)
            } else {
                binding.progressBarWeight.progress = 0
            }
        }
    }

    private fun updateStreak() {
        val sharedPref = requireActivity().getSharedPreferences("StreakTracker", Context.MODE_PRIVATE)
        val lastLoginDate = sharedPref.getString("lastLoginDate", null)
        val currentStreak = sharedPref.getInt("currentStreak", 0)
        val today = getCurrentDateString()

        val newStreak = when {
            lastLoginDate == null -> 1 // First time
            lastLoginDate == today -> currentStreak // Same day
            isYesterday(lastLoginDate) -> currentStreak + 1 // Consecutive day
            else -> 1 // Streak broken
        }

        // Save new streak
        with(sharedPref.edit()) {
            putString("lastLoginDate", today)
            putInt("currentStreak", newStreak)
            apply()
        }

        binding.tvStreakCount.text = "$newStreak Hari Berturut-turut"
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun isYesterday(dateString: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return false
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.time
        return sdf.format(date) == sdf.format(yesterday)
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.cardLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showEditProfileDialog() {
        val user = mainViewModel.userProfile.value ?: return

        val builder = AlertDialog.Builder(requireContext())
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val ageInput = EditText(requireContext()).apply {
            hint = "Umur"
            setText(user.age.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val heightInput = EditText(requireContext()).apply {
            hint = "Tinggi Badan (cm)"
            setText(user.height.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(ageInput)
        layout.addView(heightInput)

        builder.setTitle("Edit Profil")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val newAge = ageInput.text.toString().toIntOrNull()
                val newHeight = heightInput.text.toString().toIntOrNull()

                if (newAge != null && newHeight != null) {
                    val updatedProfile = user.copy(age = newAge, height = newHeight)
                    mainViewModel.updateUserProfile(updatedProfile)
                    Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Input tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .setNeutralButton("Hapus Profil") { _, _ ->
                showDeleteProfileConfirmation()
            }
            .create()
            .show()
    }

    private fun showDeleteProfileConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Profil")
            .setMessage("Apakah Anda yakin ingin menghapus semua data profil? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                mainViewModel.deleteAllData()
                Toast.makeText(context, "Profil berhasil dihapus", Toast.LENGTH_SHORT).show()

                // Redirect to onboarding
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Keluar") { _, _ ->
                firebaseAuth.signOut()
                Toast.makeText(context, "Berhasil keluar", Toast.LENGTH_SHORT).show()

                // Redirect to login
                val intent = Intent(requireContext(), com.unsoed.foodwise.ui.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}