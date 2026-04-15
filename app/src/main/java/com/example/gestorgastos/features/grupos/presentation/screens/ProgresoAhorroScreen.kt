package com.example.gestorgastos.features.grupos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoAhorroScreen(
    grupoId: Int,
    onBack: () -> Unit,
    viewModel: GruposViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val grupo = state.grupos.find { it.id == grupoId } ?: run {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progreso de Ahorro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F8E9))
                .padding(20.dp)
        ) {
            // Card de Resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(grupo.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val yaRecibieron = grupo.personasQueYaRecibieron.size
                    val total = grupo.personas.size
                    val progress = if (total > 0) yaRecibieron.toFloat() / total.toFloat() else 0f
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF4CAF50),
                            strokeWidth = 12.dp,
                            trackColor = Color(0xFFC8E6C9)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${(progress * 100).toInt()}%", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("Completado", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Meta Mensual", color = Color.Gray, fontSize = 12.sp)
                            Text("$${grupo.metaAhorro}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Participantes", color = Color.Gray, fontSize = 12.sp)
                            Text("$total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Lista de Turnos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF388E3C))
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(grupo.personas) { index, persona ->
                    val yaRecibio = persona in grupo.personasQueYaRecibieron
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (yaRecibio) Color(0xFFE8F5E9) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (yaRecibio) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    persona, 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 16.sp,
                                    color = if (yaRecibio) Color(0xFF2E7D32) else Color.Black
                                )
                                Text(
                                    if (yaRecibio) "Abono entregado" else "Pendiente de recibir",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Icon(
                                imageVector = if (yaRecibio) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (yaRecibio) Color(0xFF4CAF50) else Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}
