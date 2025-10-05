package com.unsoed.foodwise.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {
    val weight = MutableLiveData<String>()
    val height = MutableLiveData<String>()
    val age = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val activityLevel = MutableLiveData<String>()
    val goal = MutableLiveData<String>()
}