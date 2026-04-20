package com.example.gestorgastos.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Registro

@Serializable
object Grupos

@Serializable
data class Ruleta(val participantes: List<String>, val grupoId: Int)

@Serializable
object VerGanadoresRuleta

@Serializable
object Personas

@Serializable
data class Invitaciones(val telefono: String)
