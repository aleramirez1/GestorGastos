package com.example.gestorgastos.features.gastos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.features.gastos.domain.usecases.CrearGastoUseCase
import com.example.gestorgastos.features.gastos.domain.usecases.ObtenerResumenUseCase

class GastosViewModelFactory(
    private val crearGastoUseCase: CrearGastoUseCase,
    private val obtenerResumenUseCase: ObtenerResumenUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GastosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GastosViewModel(crearGastoUseCase, obtenerResumenUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
