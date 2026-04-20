package com.example.gestorgastos.features.ruleta.di

import com.example.gestorgastos.features.ruleta.data.repositories.RuletaRepositoryImpl
import com.example.gestorgastos.features.ruleta.domain.repositories.RuletaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RuletaModule {

    @Binds
    @Singleton
    abstract fun bindRuletaRepository(
        ruletaRepositoryImpl: RuletaRepositoryImpl
    ): RuletaRepository
}
