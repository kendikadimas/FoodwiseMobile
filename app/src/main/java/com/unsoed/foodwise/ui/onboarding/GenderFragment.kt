package com.unsoed.foodwise.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.unsoed.foodwise.databinding.FragmentOnboardingChoiceBinding

class GenderFragment : Fragment() {
    private var _binding: FragmentOnboardingChoiceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestionChoice.text = "Apa jenis kelaminmu?"
        binding.tvDescriptionChoice.text = "Ini akan membantu kami menghitung kebutuhan kalorimu lebih akurat."

        val options = listOf("Pria", "Wanita")
        options.forEach { optionText ->
            val radioButton = RadioButton(requireContext()).apply {
                text = optionText
                textSize = 18f
                setPadding(20, 30, 20, 30) // Memberi jarak lebih
                id = View.generateViewId() // Penting untuk state listener
            }
            binding.rgChoices.addView(radioButton)

            // Jika ada data sebelumnya di ViewModel, set radio button yang sesuai
            if (viewModel.gender.value == optionText) {
                binding.rgChoices.check(radioButton.id)
            }
        }

        binding.rgChoices.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            viewModel.gender.value = selectedRadioButton.text.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}