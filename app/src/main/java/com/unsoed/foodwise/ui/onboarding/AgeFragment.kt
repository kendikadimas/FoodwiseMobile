package com.unsoed.foodwise.ui.onboarding

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.unsoed.foodwise.databinding.FragmentOnboardingStepBinding

class AgeFragment : Fragment() {
    private var _binding: FragmentOnboardingStepBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ubah semua teks dan hint
        binding.tvQuestion.text = "Berapa usiamu?"
        binding.tvDescription.text = "Usia akan digunakan untuk personalisasi perhitungan kalori."
        binding.tvInputLabel.text = "Usia (tahun)"
        binding.etInput.hint = "Contoh: 25"
        binding.etInput.inputType = InputType.TYPE_CLASS_NUMBER // Pastikan keyboard angka

        if (!viewModel.age.value.isNullOrEmpty()) {
            binding.etInput.setText(viewModel.age.value)
        }

        binding.etInput.addTextChangedListener {
            viewModel.age.value = it.toString() // Simpan ke viewModel.age
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}