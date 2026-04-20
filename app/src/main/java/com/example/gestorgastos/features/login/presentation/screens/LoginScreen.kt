package com.example.gestorgastos.features.login.presentation.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.R
import com.example.gestorgastos.features.login.presentation.viewmodels.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onGoToRegistro: () -> Unit,
    onGoToRegistroConCodigo: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var mostrarDialogoUnirse by remember { mutableStateOf(false) }
    var codigoInvitacion by remember { mutableStateOf("") }
    var codigoError by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
        val codigoPendiente = prefs.getString("codigo_pendiente", null)
        if (codigoPendiente != null) {
            codigoInvitacion = codigoPendiente
            mostrarDialogoUnirse = true
            prefs.edit().remove("codigo_pendiente").apply()
        }
    }
    
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            val prefs = context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
            val codigoPendiente = prefs.getString("codigo_para_registro", null)
            if (!codigoPendiente.isNullOrBlank()) {
                prefs.edit()
                    .putString("codigo_aceptado", codigoPendiente)
                    .putString("nombre_nuevo_usuario", nombre)
                    .remove("codigo_para_registro")
                    .apply()
            }
            onLoginSuccess()
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
            Spacer(modifier = Modifier.height(120.dp))
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
            Spacer(modifier = Modifier.height(50.dp))
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text(stringResource(R.string.login_email), color = Color.White) },
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
                placeholder = { Text(stringResource(R.string.login_password), color = Color.White) },
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = lightBlue, uncheckedColor = lightBlue)
                )
                Text(stringResource(R.string.login_remember_me), color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(30.dp))
            if (state.isLoading) {
                CircularProgressIndicator(color = lightBlue)
            } else {
                Button(
                    onClick = { viewModel.login(nombre, password) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(stringResource(R.string.login_btn), fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { }) {
                Text(stringResource(R.string.login_forgot_password), color = darkBlue)
            }
            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onGoToRegistro) {
                Text(stringResource(R.string.login_go_to_registro), color = darkBlue)
            }
        }
        
        // Diálogo para unirse a grupo
        if (mostrarDialogoUnirse) {
            DialogoUnirseGrupo(
                codigoInvitacion = codigoInvitacion,
                onCodigoChange = { codigoInvitacion = it; codigoError = null },
                error = codigoError,
                onDismiss = { 
                    mostrarDialogoUnirse = false
                    codigoInvitacion = ""
                    codigoError = null
                },
                onUnirse = { codigo ->
                    val codigoLimpio = codigo.trim().uppercase()
                    if (codigoLimpio.isNotBlank()) {
                        val prefs = context.getSharedPreferences("invitaciones", android.content.Context.MODE_PRIVATE)
                        prefs.edit().putString("codigo_para_registro", codigoLimpio).apply()
                        mostrarDialogoUnirse = false
                        onGoToRegistroConCodigo(codigoLimpio)
                    } else {
                        codigoError = "Ingresa un código válido"
                    }
                }
            )
        }
    }
}


@Composable
fun DialogoUnirseGrupo(
    codigoInvitacion: String,
    onCodigoChange: (String) -> Unit,
    error: String? = null,
    onDismiss: () -> Unit,
    onUnirse: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 48.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text("Unirse a Grupo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF5C6BC0))
            }
        },
        text = {
            Column {
                Text(
                    text = "Ingresa el código de invitación que recibiste por WhatsApp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = codigoInvitacion,
                    onValueChange = onCodigoChange,
                    label = { Text("Código de Invitación") },
                    placeholder = { Text("Ej: GG-GRUP-1234") },                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = error != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C6BC0),
                        focusedLabelColor = Color(0xFF5C6BC0)
                    )
                )
                if (error != null) {
                    Text(text = error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                } else {
                    Text("💡 El código empieza con GG-", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (codigoInvitacion.isNotBlank()) onUnirse(codigoInvitacion) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
                enabled = codigoInvitacion.isNotBlank()
            ) { Text("Continuar al registro") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
