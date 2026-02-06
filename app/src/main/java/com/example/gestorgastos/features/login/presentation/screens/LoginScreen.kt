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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.features.login.presentation.viewmodels.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit, onGoToRegistro: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)

    LaunchedEffect(state.isSuccess) { if (state.isSuccess) { onLoginSuccess() } }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Canvas(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp)) { drawCircle(color = lightBlue) }
        Canvas(modifier = Modifier.size(150.dp).offset(x = 300.dp, y = 30.dp)) { drawCircle(color = darkBlue) }
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(120.dp))
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(lightBlue), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.height(50.dp))
            TextField(value = nombre, onValueChange = { nombre = it }, placeholder = { Text("Nombre", color = Color.White) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = purple.copy(alpha = 0.7f), unfocusedContainerColor = purple.copy(alpha = 0.7f), focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(25.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password", color = Color.White) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = purple.copy(alpha = 0.7f), unfocusedContainerColor = purple.copy(alpha = 0.7f), focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(25.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it }, colors = CheckboxDefaults.colors(checkedColor = lightBlue, uncheckedColor = lightBlue))
                Text("Remember me", color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(30.dp))
            if (state.isLoading) { CircularProgressIndicator(color = lightBlue) }
            else { Button(onClick = { viewModel.login(nombre, password) }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text("Sign in", fontSize = 16.sp) } }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { }) { Text("Forgot password?", color = darkBlue) }
            if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(text = state.error ?: "", color = Color.Red, fontSize = 14.sp) }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onGoToRegistro) { Text("No tienes cuenta? Registrate", color = darkBlue) }
        }
    }
}
