package com.example.gestorgastos.features.grupos.di

import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.grupos.data.repositories.GruposRepositoryImpl
import com.example.gestorgastos.features.grupos.domain.repositories.GruposRepository
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GruposModule {

    @Provides
    @Singleton
    fun provideGruposRepository(
        api: GastosApi,
        grupoDao: GrupoDao,
        tokenManager: TokenManager
    ): GruposRepository {
        return GruposRepositoryImpl(api, grupoDao, tokenManager)
    }
}
