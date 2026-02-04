package com.example.gestorgastos

import android.app.Application
import com.example.gestorgastos.core.di.AppContainer

class GestorGastosApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
