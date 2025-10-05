package com.unsoed.foodwise.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Total 6 langkah
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GenderFragment()
            1 -> AgeFragment()
            2 -> WeightFragment()
            3 -> HeightFragment()
            4 -> ActivityLevelFragment()
            5 -> GoalFragment()
            else -> Fragment()
        }
    }
}