package com.example.gestorgastos.features.gastos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorgastos.features.gastos.domain.entities.Gasto
import com.example.gestorgastos.features.gastos.domain.entities.ResumenGastos
import com.example.gestorgastos.features.gastos.domain.usecases.CrearGastoUseCase
import com.example.gestorgastos.features.gastos.domain.usecases.ObtenerResumenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GastosUiState(
    val isLoading: Boolean = false,
    val resumen: ResumenGastos? = null,
    val error: String? = null,
    val gastoCreado: Boolean = false
)

class GastosViewModel(
    private val crearGastoUseCase: CrearGastoUseCase,
    private val obtenerResumenUseCase: ObtenerResumenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GastosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarResumen()
    }

    fun cargarResumen() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = obtenerResumenUseCase()
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { resumen ->
                        currentState.copy(
                            isLoading = false,
                            resumen = resumen
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                )
            }
        }
    }

    fun crearGasto(monto: Double, descripcion: String, quienPago: String, tipo: String) {
        _uiState.update { it.copy(isLoading = true, error = null, gastoCreado = false) }

        viewModelScope.launch {
            val result = crearGastoUseCase(monto, descripcion, quienPago, tipo)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        currentState.copy(
                            isLoading = false,
                            gastoCreado = true
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                )
            }
            if (result.isSuccess) {
                cargarResumen()
            }
        }
    }

    fun limpiarGastoCreado() {
        _uiState.update { it.copy(gastoCreado = false) }
    }
}
