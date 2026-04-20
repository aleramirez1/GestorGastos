package com.example.gestorgastos.features.invitaciones.domain.entities

data class InvitacionGrupo(
    val id: String = "",
    val grupoId: String = "",
    val grupoNombre: String = "",
    val invitadoPor: String = "",
    val invitadoTelefono: String = "",
    val invitadoNombre: String = "",
    val estado: EstadoInvitacion = EstadoInvitacion.PENDIENTE,
    val fechaInvitacion: Long = System.currentTimeMillis(),
    val fechaRespuesta: Long? = null,
    val mensaje: String = ""
)

enum class EstadoInvitacion {
    PENDIENTE,
    ACEPTADA,
    RECHAZADA,
    EXPIRADA
}
