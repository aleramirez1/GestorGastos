package com.example.gestorgastos.features.personas.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.R
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonasScreen(
    viewModel: GruposViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.cargarGrupos()
    }
    
    val personasQueDebenCincuenta = state.grupos.mapNotNull { grupo ->
        grupo.ganadorRuleta?.let { ganador ->
            PersonaInfo(
                nombre = ganador,
                nombreGrupo = grupo.nombre,
                fecha = grupo.fechaCreacion
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ver_ganadores_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.comun_volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5C6BC0),
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
                .background(Color(0xFFE3F2FD))
        ) {
            if (personasQueDebenCincuenta.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            stringResource(R.string.ver_ganadores_vacio),
                            fontSize = 20.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.ver_ganadores_vacio_desc),
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
                    items(personasQueDebenCincuenta) { personaInfo ->
                        PersonaCard(personaInfo)
                    }
                }
            }
        }
    }
}

data class PersonaInfo(
    val nombre: String,
    val nombreGrupo: String,
    val fecha: String
)

@Composable
private fun PersonaCard(personaInfo: PersonaInfo) {
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
                    personaInfo.nombre,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C6BC0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.grupos_label_grupo_formato, personaInfo.nombreGrupo),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    personaInfo.fecha,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Box(
                modifier = Modifier
                    .background(Color(0xFFE57373), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    stringResource(R.string.ver_ganadores_monto_extra),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
