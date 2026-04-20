package com.example.gestorgastos.features.personas.di

import com.example.gestorgastos.features.personas.data.repositories.PersonasRepositoryImpl
import com.example.gestorgastos.features.personas.domain.repositories.PersonasRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PersonasModule {

    @Binds
    @Singleton
    abstract fun bindPersonasRepository(
        personasRepositoryImpl: PersonasRepositoryImpl
    ): PersonasRepository
}
