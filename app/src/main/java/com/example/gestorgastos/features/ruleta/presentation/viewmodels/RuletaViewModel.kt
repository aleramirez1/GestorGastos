package com.example.gestorgastos.features.ruleta.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class RuletaUiState(
    val participantes: List<String> = emptyList(),
    val ganador: String? = null,
    val isSpinning: Boolean = false,
    val currentRotation: Float = 0f
)

class RuletaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RuletaUiState())
    val uiState = _uiState.asStateFlow()

    fun setParticipantes(participantes: List<String>) {
        _uiState.update { it.copy(participantes = participantes) }
    }

    fun girarRuleta() {
        if (_uiState.value.isSpinning || _uiState.value.participantes.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSpinning = true, ganador = null) }

            val totalRotations = Random.nextInt(5, 10) * 360f
            val steps = 50
            val rotationPerStep = totalRotations / steps

            repeat(steps) { step ->
                delay(30L + step * 2L)
                _uiState.update { it.copy(currentRotation = it.currentRotation + rotationPerStep) }
            }

            val ganador = _uiState.value.participantes.random()
            _uiState.update { it.copy(ganador = ganador, isSpinning = false) }
        }
    }

    fun resetRuleta() {
        _uiState.update { RuletaUiState(participantes = it.participantes) }
    }
}
