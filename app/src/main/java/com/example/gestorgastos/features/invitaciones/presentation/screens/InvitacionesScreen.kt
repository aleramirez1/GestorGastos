package com.example.gestorgastos.features.invitaciones.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestorgastos.features.invitaciones.domain.entities.InvitacionGrupo
import com.example.gestorgastos.features.invitaciones.presentation.viewmodels.InvitacionesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitacionesScreen(
    telefono: String,
    onNavigateBack: () -> Unit,
    viewModel: InvitacionesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDialogoRegistro by remember { mutableStateOf(false) }
    var invitacionPendienteId by remember { mutableStateOf<String?>(null) }
    var nombreUsuario by remember { mutableStateOf("") }
    var codigoInput by remember { mutableStateOf("") }
    var mostrarInputCodigo by remember { mutableStateOf(false) }

    LaunchedEffect(telefono) {
        viewModel.cargarInvitaciones(telefono)
    }

    uiState.mensaje?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMensaje()
        }
    }

    if (mostrarDialogoRegistro && invitacionPendienteId != null) {
        Dialog(onDismissRequest = { mostrarDialogoRegistro = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Antes de unirte", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ingresa tu nombre para que los demás miembros del grupo te identifiquen.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = { nombreUsuario = it },
                        label = { Text("Tu nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (nombreUsuario.isNotBlank()) {
                                viewModel.aceptarInvitacionConNombre(invitacionPendienteId!!, nombreUsuario)
                                mostrarDialogoRegistro = false
                                invitacionPendienteId = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) { Text("Unirme al grupo") }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { mostrarDialogoRegistro = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cancelar") }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invitaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("¿Tienes un código de invitación?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (!mostrarInputCodigo) {
                                        Button(
                                            onClick = { mostrarInputCodigo = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(25.dp)
                                        ) { Text("Ingresar código") }
                                    } else {
                                        OutlinedTextField(
                                            value = codigoInput,
                                            onValueChange = { codigoInput = it.uppercase() },
                                            label = { Text("Código (ej: GG-GRUP-1234)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                if (codigoInput.isNotBlank()) {
                                                    viewModel.buscarInvitacionPorCodigo(codigoInput)
                                                    mostrarInputCodigo = false
                                                    codigoInput = ""
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(25.dp)
                                        ) { Text("Buscar invitación") }
                                    }
                                }
                            }
                        }

                        if (uiState.invitaciones.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                                    Text("No tienes invitaciones pendientes", color = Color.Gray)
                                }
                            }
                        } else {
                            items(uiState.invitaciones) { invitacion ->
                                InvitacionCard(
                                    invitacion = invitacion,
                                    onAceptar = {
                                        invitacionPendienteId = invitacion.id
                                        mostrarDialogoRegistro = true
                                    },
                                    onRechazar = { viewModel.rechazarInvitacion(invitacion.id) }
                                )
                            }
                        }
                    }
                }
            }

            uiState.mensaje?.let { mensaje ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                ) { Text(mensaje) }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.error
                ) { Text(error) }
            }
        }
    }
}

@Composable
fun InvitacionCard(
    invitacion: InvitacionGrupo,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = invitacion.grupoNombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Invitado por: ${invitacion.invitadoPor}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(invitacion.fechaInvitacion)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAceptar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Unirme")
                }
                OutlinedButton(
                    onClick = onRechazar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rechazar")
                }
            }
        }
    }
}
