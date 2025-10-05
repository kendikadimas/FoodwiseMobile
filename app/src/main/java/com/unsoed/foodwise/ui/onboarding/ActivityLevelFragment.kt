package com.unsoed.foodwise.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.unsoed.foodwise.databinding.FragmentOnboardingChoiceBinding

class ActivityLevelFragment : Fragment() {
    private var _binding: FragmentOnboardingChoiceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestionChoice.text = "Seberapa aktif kamu sehari-hari?"
        binding.tvDescriptionChoice.text = "Pilih level aktivitas yang paling sesuai dengan rutinitasmu."

        val options = listOf("Jarang Olahraga", "Olahraga Ringan (1-3x seminggu)", "Olahraga Sedang (3-5x seminggu)", "Olahraga Aktif (6-7x seminggu)")
        options.forEach { optionText ->
            val radioButton = RadioButton(requireContext()).apply {
                text = optionText
                textSize = 18f
                setPadding(20, 30, 20, 30)
                id = View.generateViewId()
            }
            binding.rgChoices.addView(radioButton)

            if (viewModel.activityLevel.value == optionText) {
                binding.rgChoices.check(radioButton.id)
            }
        }

        binding.rgChoices.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            viewModel.activityLevel.value = selectedRadioButton.text.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}