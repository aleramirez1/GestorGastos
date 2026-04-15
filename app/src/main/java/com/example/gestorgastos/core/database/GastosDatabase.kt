package com.example.gestorgastos.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestorgastos.core.database.dao.GastoDao
import com.example.gestorgastos.core.database.dao.GrupoDao
import com.example.gestorgastos.core.database.dao.SesionDao
import com.example.gestorgastos.core.database.dao.UsuarioDao
import com.example.gestorgastos.core.database.entities.GastoEntity
import com.example.gestorgastos.core.database.entities.GrupoEntity
import com.example.gestorgastos.core.database.entities.SesionEntity
import com.example.gestorgastos.core.database.entities.UsuarioEntity
import com.example.gestorgastos.core.database.converters.Converters

@Database(
    entities = [
        GrupoEntity::class,
        UsuarioEntity::class,
        SesionEntity::class,
        GastoEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GastosDatabase : RoomDatabase() {
    abstract fun grupoDao(): GrupoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun sesionDao(): SesionDao
    abstract fun gastoDao(): GastoDao
}
