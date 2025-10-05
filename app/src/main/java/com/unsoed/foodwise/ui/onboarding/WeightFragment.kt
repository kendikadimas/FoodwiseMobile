package com.unsoed.foodwise.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.unsoed.foodwise.databinding.FragmentOnboardingStepBinding

class WeightFragment : Fragment() {
    private var _binding: FragmentOnboardingStepBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestion.text = "Berapa berat badanmu?"
        binding.tvDescription.text = "Gunakan satuan kilogram (kg)."
        binding.tvInputLabel.text = "Berat Badan"
        binding.etInput.hint = "Contoh: 65.5"

        if (!viewModel.weight.value.isNullOrEmpty()) {
            binding.etInput.setText(viewModel.weight.value)
        }

        binding.etInput.addTextChangedListener {
            viewModel.weight.value = it.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}