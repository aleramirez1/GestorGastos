package com.example.gestorgastos.features.gastos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestorgastos.features.gastos.domain.repositories.GastosRepository

class GastosViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GastosViewModel(repository) as T
    }
}
