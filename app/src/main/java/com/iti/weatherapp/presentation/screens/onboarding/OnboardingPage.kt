package com.iti.weatherapp.presentation.screens.onboarding

import androidx.annotation.RawRes
import com.iti.weatherapp.R

data class OnboardingPage(
    val title: String,
    val description: String,
    @RawRes val animationRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Real-Time Forecasts",
        description = "Get precise, up-to-the-minute weather updates for your exact location, complete with hourly and daily predictions.",
        animationRes = R.raw.on_boarding_1
    ),
    OnboardingPage(
        title = "Explore & Save Anywhere",
        description = "Browse the interactive map to check the weather across the globe. Pin your favorite cities to access them instantly.",
        animationRes = R.raw.on_boarding_2
    ),
    OnboardingPage(
        title = "Smart Weather Alarms",
        description = "Never get caught in the rain. Set precise background alarms and pinned notifications to warn you before a storm hits.",
        animationRes = R.raw.on_boarding_3
    )
)