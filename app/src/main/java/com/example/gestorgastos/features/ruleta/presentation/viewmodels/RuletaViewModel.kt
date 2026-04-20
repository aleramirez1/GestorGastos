package com.example.gestorgastos.features.ruleta.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.ruleta.domain.usecases.GirarRuletaUseCase
import com.example.gestorgastos.features.ruleta.domain.usecases.GuardarResultadoRuletaUseCase
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
    val ganador: String? = null,
    val isSpinning: Boolean = false,
    val currentRotation: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RuletaViewModel @Inject constructor(
    private val girarRuletaUseCase: GirarRuletaUseCase,
    private val guardarResultadoRuletaUseCase: GuardarResultadoRuletaUseCase,
    private val notificationManager: com.example.gestorgastos.core.notifications.domain.NotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuletaUiState())
    val uiState = _uiState.asStateFlow()

    fun setParticipantes(participantes: List<String>) {
        _uiState.update { it.copy(participantes = participantes) }
    }

    fun girarRuleta() {
        if (_uiState.value.isSpinning || _uiState.value.participantes.isEmpty()) return

        viewModelScope.launch {
            try {
                val participantes = _uiState.value.participantes
                val anglePerSegment = 360f / participantes.size

                val ganador = girarRuletaUseCase(participantes)
                val ganadorIndex = participantes.indexOf(ganador)

                val centroSegmento = ganadorIndex * anglePerSegment + anglePerSegment / 2f
                val anguloFinal = (360f * Random.nextInt(5, 10)) + (360f - centroSegmento)

                _uiState.update { it.copy(isSpinning = true, ganador = null, error = null) }

                val steps = 60
                val startRotation = _uiState.value.currentRotation
                val totalDelta = anguloFinal

                for (step in 1..steps) {
                    val progress = step.toFloat() / steps
                    val eased = 1f - (1f - progress) * (1f - progress)
                    val newRotation = startRotation + totalDelta * eased
                    _uiState.update { it.copy(currentRotation = newRotation) }
                    delay(16L + (step * 1.5f).toLong())
                }

                _uiState.update { it.copy(ganador = ganador, isSpinning = false) }
                
                notificationManager.showLocalNotification(
                    title = "Ganador de la Ruleta",
                    message = "$ganador debe pagar $50 extra",
                    channelId = "ruleta"
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSpinning = false, 
                        error = "Error al girar la ruleta: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun guardarResultado(grupoId: Int, ganador: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                guardarResultadoRuletaUseCase(grupoId, ganador)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Error al guardar resultado: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun resetRuleta() {
        _uiState.update { RuletaUiState(participantes = it.participantes) }
    }
}
