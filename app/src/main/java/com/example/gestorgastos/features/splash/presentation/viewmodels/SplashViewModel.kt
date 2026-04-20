package com.example.gestorgastos.features.splash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.splash.domain.usecases.CheckAuthStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val navigationReady: Boolean = false
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkAuthAndNavigate()
    }

    private fun checkAuthAndNavigate() {
        viewModelScope.launch {
            delay(2000)
            try {
                val isAuthenticated = checkAuthStatusUseCase()
                _uiState.update {
                    it.copy(isLoading = false, isAuthenticated = isAuthenticated, navigationReady = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isAuthenticated = false, navigationReady = true)
                }
            }
        }
    }
}
