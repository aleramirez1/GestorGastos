package com.example.gestorgastos.features.ruleta.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class RuletaUiState(
    val participantes: List<String> = emptyList(),
    val personasQueYaRecibieron: List<String> = emptyList(),
    val ganador: String? = null,
    val isSpinning: Boolean = false,
    val currentRotation: Float = 0f
) {
    val participantesActivos: List<String>
        get() = participantes.filter { it !in personasQueYaRecibieron }
}

@HiltViewModel
class RuletaViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RuletaUiState())
    val uiState = _uiState.asStateFlow()

    fun setParticipantes(participantes: List<String>, personasQueYaRecibieron: List<String> = emptyList()) {
        _uiState.update { it.copy(
            participantes = participantes,
            personasQueYaRecibieron = personasQueYaRecibieron
        ) }
    }

    fun girarRuleta() {
        val activos = _uiState.value.participantesActivos
        if (_uiState.value.isSpinning || activos.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSpinning = true, ganador = null) }

            val totalRotations = Random.nextInt(5, 10) * 360f
            val steps = 50
            val rotationPerStep = totalRotations / steps

            repeat(steps) { step ->
                delay(30L + step * 2L)
                _uiState.update { it.copy(currentRotation = it.currentRotation + rotationPerStep) }
            }

            val ganador = activos.random()
            _uiState.update { it.copy(ganador = ganador, isSpinning = false) }
        }
    }

    fun resetRuleta() {
        _uiState.update { RuletaUiState(
            participantes = it.participantes,
            personasQueYaRecibieron = it.personasQueYaRecibieron
        ) }
    }
}
