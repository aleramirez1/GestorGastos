package com.example.gestorgastos.features.login.di

import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.login.data.datasources.local.TokenManager
import com.example.gestorgastos.features.login.data.repositories.LoginRepositoryImpl
import com.example.gestorgastos.features.login.domain.repositories.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginRepository(
        api: GastosApi,
        tokenManager: TokenManager
    ): LoginRepository {
        return LoginRepositoryImpl(api, tokenManager)
    }
}
