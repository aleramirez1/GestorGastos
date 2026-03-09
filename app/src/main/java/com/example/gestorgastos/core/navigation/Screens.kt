package com.example.gestorgastos.core.navigation

import kotlinx.serialization.Serializable

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
