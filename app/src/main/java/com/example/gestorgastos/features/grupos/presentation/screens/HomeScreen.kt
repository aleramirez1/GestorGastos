package com.example.gestorgastos.features.grupos.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

@Composable
fun HomeScreen(
    onNavigateToGrupos: () -> Unit,
    onNavigateToCrearGrupo: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogout: () -> Unit,
    viewModel: GruposViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val bgColor = Color(0xFFE3F2FD)

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Canvas(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp)) { drawCircle(color = lightBlue) }
        Canvas(modifier = Modifier.size(120.dp).offset(x = 300.dp, y = 100.dp)) { drawCircle(color = darkBlue) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de Perfil Dinámica
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigateToPerfil() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.fotoPerfil != null) {
                        AsyncImage(
                            model = state.fotoPerfil,
                            contentDescription = "Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(text = "Bienvenido de nuevo,", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = "¡Hola, ${state.nombreUsuario ?: "Usuario"}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", tint = Color.Red.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clickable { onNavigateToGrupos() },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = lightBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp), modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = lightBlue, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Mis Grupos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                        Text("${state.grupos.size} grupos registrados", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clickable { onNavigateToCrearGrupo() },
                colors = CardDefaults.cardColors(containerColor = darkBlue),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp), modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Nuevo Grupo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Empieza a ahorrar o dividir gastos", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Text("Gestor de Gastos v1.0", fontSize = 11.sp, color = Color.Gray.copy(alpha = 0.4f))
        }
    }
}
