package com.example.gestorgastos.features.grupos.domain.entities

data class GastoGrupo(
    val id: Int,
    val persona: String,
    val monto: Double,
    val descripcion: String,
    val tipo: String, // "te_deben", "tu_debes", "abono"
    val fecha: String,
    val comprobanteUri: String? = null
)

data class Grupo(
    val id: Int,
    val nombre: String,
    val usuarioId: Int,
    val fechaCreacion: String,
    val personas: List<String>,
    val gastos: List<GastoGrupo>,
    val fotoTicketUri: String? = null,
    val ganadorRuleta: String? = null,
    val isAhorro: Boolean = false,
    val metaAhorro: Double = 0.0,
    val fechaLimite: String? = null,
    val personasQueYaRecibieron: List<String> = emptyList()
) {
    val montoAcumulado: Double
        get() = gastos.filter { it.tipo == "abono" }.sumOf { it.monto }
    
    val progreso: Float
        get() = if (metaAhorro > 0) (montoAcumulado / metaAhorro).toFloat() else 0f
}
