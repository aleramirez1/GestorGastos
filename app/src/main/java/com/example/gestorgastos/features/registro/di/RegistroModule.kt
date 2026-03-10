package com.example.gestorgastos.features.registro.di

import com.example.gestorgastos.features.registro.data.repositories.RegistroRepositoryImpl
import com.example.gestorgastos.features.registro.domain.repositories.RegistroRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegistroModule {

    @Binds
    @Singleton
    abstract fun bindRegistroRepository(
        registroRepositoryImpl: RegistroRepositoryImpl
    ): RegistroRepository
}