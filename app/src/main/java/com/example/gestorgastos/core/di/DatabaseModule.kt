package com.example.gestorgastos.core.di

import android.content.Context
import androidx.room.Room
import com.example.gestorgastos.core.database.GastosDatabase
import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.database.dao.SesionDao
import com.example.gestorgastos.core.database.dao.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GastosDatabase {
        return Room.databaseBuilder(
            context,
            GastosDatabase::class.java,
            "gestor_gastos_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideGrupoDao(database: GastosDatabase): GrupoDao {
        return database.grupoDao()
    }
    
    @Provides
    @Singleton
    fun provideUsuarioDao(database: GastosDatabase): UsuarioDao {
        return database.usuarioDao()
    }
    
    @Provides
    @Singleton
    fun provideSesionDao(database: GastosDatabase): SesionDao {
        return database.sesionDao()
    }
}
