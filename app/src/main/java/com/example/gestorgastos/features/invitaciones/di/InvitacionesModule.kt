package com.example.gestorgastos.features.invitaciones.di

import com.example.gestorgastos.features.invitaciones.data.repositories.InvitacionesRepositoryImpl
import com.example.gestorgastos.features.invitaciones.domain.repositories.InvitacionesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InvitacionesModule {

    @Binds
    @Singleton
    abstract fun bindInvitacionesRepository(
        impl: InvitacionesRepositoryImpl
    ): InvitacionesRepository
}
