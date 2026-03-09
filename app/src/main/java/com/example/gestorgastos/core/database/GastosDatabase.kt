package com.example.gestorgastos.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.database.entities.GrupoEntity
import com.example.gestorgastos.core.database.converters.Converters

@Database(
    entities = [GrupoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GastosDatabase : RoomDatabase() {
    abstract fun grupoDao(): GrupoDao
}
