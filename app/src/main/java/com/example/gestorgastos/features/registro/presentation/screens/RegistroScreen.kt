package com.example.gestorgastos.features.registro.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.R
import com.example.gestorgastos.features.registro.presentation.viewmodels.RegistroViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = hiltViewModel(),
    onRegistroSuccess: () -> Unit,
    onGoToLogin: () -> Unit,
    codigoInvitacion: String = ""
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)
    
    val codigoEfectivo = codigoInvitacion.ifBlank {
        context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
            .getString("codigo_para_registro", "") ?: ""
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            if (codigoEfectivo.isNotBlank()) {
                context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString("codigo_aceptado", codigoEfectivo)
                    .putString("nombre_nuevo_usuario", nombre)
                    .remove("codigo_para_registro")
                    .apply()
            }
            viewModel.resetState()
            onRegistroSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Canvas(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp)) {
            drawCircle(color = lightBlue)
        }
        Canvas(modifier = Modifier.size(150.dp).offset(x = 300.dp, y = 30.dp)) {
            drawCircle(color = darkBlue)
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(lightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            
            if (codigoEfectivo.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Código de invitación válido", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF2E7D32))
                            Text(codigoEfectivo, fontSize = 12.sp, color = Color(0xFF558B2F))
                            Text("Al registrarte te unirás al grupo automáticamente", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
            
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text(stringResource(R.string.registro_nombre), color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = purple.copy(alpha = 0.7f),
                    unfocusedContainerColor = purple.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(25.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(stringResource(R.string.registro_email), color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = purple.copy(alpha = 0.7f),
                    unfocusedContainerColor = purple.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(25.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(stringResource(R.string.registro_password), color = Color.White) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = purple.copy(alpha = 0.7f),
                    unfocusedContainerColor = purple.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(25.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            if (state.isLoading) {
                CircularProgressIndicator(color = lightBlue)
            } else {
                Button(
                    onClick = { viewModel.registro(nombre, email, password) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(stringResource(R.string.registro_btn), fontSize = 16.sp)
                }
            }
            if (state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onGoToLogin) {
                Text(stringResource(R.string.registro_go_to_login), color = darkBlue)
            }
        }
    }
}
