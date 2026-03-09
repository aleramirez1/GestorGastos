package com.example.gestorgastos.features.ruleta.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestorgastos.features.grupos.domain.entities.Grupo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerGanadoresRuletaScreen(
    grupos: List<Grupo>,
    onBack: () -> Unit
) {
    val ganadoresRuleta = grupos.mapNotNull { grupo ->
        grupo.ganadorRuleta?.let { ganador ->
            GanadorInfo(
                nombreGrupo = grupo.nombre,
                ganador = ganador,
                fecha = grupo.fechaCreacion
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personas que deben +$50") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5C6BC0)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE3F2FD))
        ) {
            if (ganadoresRuleta.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "No hay ganadores de ruleta aún",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Juega la ruleta al crear un grupo",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ganadoresRuleta) { ganadorInfo ->
                        GanadorCard(ganadorInfo)
                    }
                }
            }
        }
    }
}

data class GanadorInfo(
    val nombreGrupo: String,
    val ganador: String,
    val fecha: String
)

@Composable
private fun GanadorCard(ganadorInfo: GanadorInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de moneda
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFFFD54F), shape = RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    ganadorInfo.ganador,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C6BC0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Grupo: ${ganadorInfo.nombreGrupo}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    ganadorInfo.fecha,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Badge de +50
            Box(
                modifier = Modifier
                    .background(Color(0xFFE57373), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "+$50",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
