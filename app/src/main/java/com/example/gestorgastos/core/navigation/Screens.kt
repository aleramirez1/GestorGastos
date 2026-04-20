package com.example.gestorgastos.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Registro

@Serializable
object Home

@Serializable
object Grupos

@Serializable
data class Ruleta(
    val participantes: List<String>, 
    val grupoId: Int,
    val yaRecibieron: List<String> = emptyList()
)

@Serializable
object VerGanadoresRuleta

@Serializable
object Personas

@Serializable
object Perfil

@Serializable
data class Invitaciones(val telefono: String)
