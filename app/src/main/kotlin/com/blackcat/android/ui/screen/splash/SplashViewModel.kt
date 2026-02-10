package com.blackcat.android.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.blackcat.android.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {
    val hasCompletedOnboarding: Flow<Boolean> = settingsRepository.hasCompletedOnboarding
}
