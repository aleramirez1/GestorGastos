package com.example.gestorgastos.features.grupos.presentation.screens

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.gestorgastos.features.grupos.domain.entities.Grupo
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GruposScreen(
    viewModel: GruposViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToRuleta: (List<String>, Int) -> Unit = { _, _ -> },
    onNavigateToVerGanadores: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf("inicio") }
    var nombreGrupo by remember { mutableStateOf("") }
    var numPersonas by remember { mutableStateOf("") }
    val personasInput = remember { mutableStateListOf<String>() }
    var nuevaPersona by remember { mutableStateOf("") }
    val telefonosPendientes = remember { mutableStateListOf<String>() }
    var montoTeDeben by remember { mutableStateOf("") }
    var descripcionTeDeben by remember { mutableStateOf("") }
    var montoTuDebes by remember { mutableStateOf("") }
    var descripcionTuDebes by remember { mutableStateOf("") }
    var personaTuDebes by remember { mutableStateOf("") }
    var expandedTuDebes by remember { mutableStateOf(false) }
    var fotoTicketUri by remember { mutableStateOf<String?>(null) }
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)
    
    var screenBeforeRuleta by remember { mutableStateOf<String?>(null) }
    
    val navigateToRuletaWrapper: (List<String>, Int) -> Unit = { participantes, grupoId ->
        screenBeforeRuleta = currentScreen
        onNavigateToRuleta(participantes, grupoId)
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Canvas(modifier = Modifier.size(150.dp).offset(x = (-40).dp, y = (-40).dp)) { drawCircle(color = lightBlue) }
        Canvas(modifier = Modifier.size(100.dp).offset(x = 320.dp, y = 20.dp)) { drawCircle(color = darkBlue) }
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Spacer(modifier = Modifier.height(100.dp))
            when (currentScreen) {
                "inicio" -> InicioScreen(state, viewModel, darkBlue, lightBlue, onLogout, { currentScreen = it }, onNavigateToVerGanadores)
                "crear_grupo" -> CrearGrupoScreen(nombreGrupo, { nombreGrupo = it }, numPersonas, { numPersonas = it }, darkBlue, lightBlue, personasInput, viewModel, { fotoTicketUri = it }, { currentScreen = it }, navigateToRuletaWrapper)
                "agregar_personas" -> AgregarPersonasScreen(nombreGrupo, personasInput, state, viewModel, darkBlue, lightBlue, telefonosPendientes) { currentScreen = it }
                "agregar_gasto_inicial" -> AgregarGastoInicialScreen(nombreGrupo, personasInput, montoTeDeben, { montoTeDeben = it }, descripcionTeDeben, { descripcionTeDeben = it }, state, viewModel, darkBlue, lightBlue, purple, { nombreGrupo = "" }, { numPersonas = "" }, fotoTicketUri, telefonosPendientes, { currentScreen = it }, navigateToRuletaWrapper)
                "editar_grupo" -> EditarGrupoScreen(state, viewModel, nuevaPersona, { nuevaPersona = it }, darkBlue, lightBlue, purple) { currentScreen = it }
                "gastos" -> GastosDetailScreen(state, viewModel, montoTeDeben, { montoTeDeben = it }, descripcionTeDeben, { descripcionTeDeben = it }, montoTuDebes, { montoTuDebes = it }, descripcionTuDebes, { descripcionTuDebes = it }, personaTuDebes, { personaTuDebes = it }, expandedTuDebes, { expandedTuDebes = it }, darkBlue, lightBlue, purple) { currentScreen = it }
            }
        }
    }
}

@Composable
private fun InicioScreen(state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, onLogout: () -> Unit, navigate: (String) -> Unit, onNavigateToVerGanadores: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onLogout) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = darkBlue) }
        Text(text = stringResource(R.string.grupos_titulo), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(24.dp))
    if (state.isLoading) { CircularProgressIndicator(color = lightBlue) }
    else if (state.grupos.isEmpty()) { Text(text = stringResource(R.string.grupos_no_grupos), style = MaterialTheme.typography.bodyLarge, color = Color.Gray) }
    else {
        state.grupos.forEach { grupo ->
            Card(modifier = Modifier.fillMaxWidth().clickable { vm.seleccionarGrupo(grupo); navigate("gastos") }, colors = CardDefaults.cardColors(containerColor = lightBlue), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = grupo.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = stringResource(R.string.grupos_personas_count, grupo.personas.size), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    IconButton(onClick = { vm.seleccionarGrupo(grupo); navigate("editar_grupo") }) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White) }
                    IconButton(onClick = { vm.eliminarGrupo(grupo.id) }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = { navigate("crear_grupo") }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue), shape = RoundedCornerShape(25.dp)) {
        Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text(stringResource(R.string.grupos_btn_generar), fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(12.dp))
    Button(onClick = onNavigateToVerGanadores, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)), shape = RoundedCornerShape(25.dp)) {
        Text(stringResource(R.string.grupos_btn_personas), fontSize = 16.sp, color = Color(0xFF5C6BC0), fontWeight = FontWeight.Bold)
    }
    if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
}

@Composable
private fun CrearGrupoScreen(
    nombreGrupo: String, 
    onNombreChange: (String) -> Unit, 
    numPersonas: String, 
    onNumChange: (String) -> Unit, 
    darkBlue: Color, 
    lightBlue: Color, 
    personasInput: MutableList<String>, 
    vm: GruposViewModel,
    onFotoUriChange: (String?) -> Unit,
    navigate: (String) -> Unit,
    onNavigateToRuleta: (List<String>, Int) -> Unit = { _, _ -> }
) {
    var fotoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var fotoCaptured by remember { mutableStateOf(false) }
    var errorCamara by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    fun crearUriParaFoto(): android.net.Uri? {
        return try {
            val dir = context.cacheDir
            if (!dir.exists()) dir.mkdirs()
            val photoFile = java.io.File(dir, "ticket_${System.currentTimeMillis()}.jpg")
            photoFile.createNewFile()
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
        } catch (e: Exception) {
            errorCamara = "Error al preparar cámara: ${e.message}"
            null
        }
    }
    
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && fotoUri != null) {
            fotoCaptured = true
            onFotoUriChange(fotoUri.toString())
            errorCamara = null
        } else {
            fotoCaptured = false
            errorCamara = if (!success) "No se tomó la foto" else null
        }
    }
    
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = crearUriParaFoto()
            if (uri != null) {
                fotoUri = uri
                cameraLauncher.launch(uri)
            }
        } else {
            val showRationale = activity?.shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) ?: false
            if (!showRationale) {
                val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", context.packageName, null)
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                errorCamara = "Activa el permiso de cámara en Configuración"
            } else {
                errorCamara = "Se necesita permiso de cámara"
            }
        }
    }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = stringResource(R.string.grupos_crear_titulo), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(30.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            TextField(value = nombreGrupo, onValueChange = onNombreChange, label = { Text(stringResource(R.string.grupos_label_nombre)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = numPersonas, onValueChange = onNumChange, label = { Text(stringResource(R.string.grupos_label_cuantas_personas)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    val permission = android.Manifest.permission.CAMERA
                    when {
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context, permission
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                            val uri = crearUriParaFoto()
                            if (uri != null) {
                                fotoUri = uri
                                cameraLauncher.launch(uri)
                            }
                        }
                        else -> {
                            permissionLauncher.launch(permission)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue), 
                shape = RoundedCornerShape(12.dp)
            ) { 
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.grupos_btn_camara), fontSize = 14.sp) 
            }
            
            if (errorCamara != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorCamara!!, color = Color.Red, fontSize = 12.sp)
            }
            
            if (fotoCaptured && fotoUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CameraAlt, 
                            contentDescription = null, 
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.grupos_foto_ticket_success), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(stringResource(R.string.grupos_foto_ticket_desc), color = Color(0xFF558B2F), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(onClick = { val num = numPersonas.toIntOrNull() ?: 0; if (nombreGrupo.isNotBlank() && num > 0) { personasInput.clear(); repeat(num) { personasInput.add("") }; navigate("agregar_personas") } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text(stringResource(R.string.grupos_btn_continuar), fontSize = 16.sp) }
}

@Composable
private fun AgregarPersonasScreen(nombreGrupo: String, personasInput: MutableList<String>, state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, telefonosPendientes: MutableList<String>, navigate: (String) -> Unit) {
    val emailsInput = remember { mutableStateListOf<String>().apply { repeat(personasInput.size) { add("") } } }
    val invitacionesEnviadas = remember { mutableStateListOf<String>() }
    val context = androidx.compose.ui.platform.LocalContext.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("crear_grupo") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = nombreGrupo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = stringResource(R.string.grupos_agregar_participantes), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    Spacer(modifier = Modifier.height(24.dp))

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            personasInput.forEachIndexed { index, nombre ->
                Text(text = "Persona ${index + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = nombre,
                    onValueChange = { personasInput[index] = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = emailsInput.getOrElse(index) { "" },
                        onValueChange = { if (index < emailsInput.size) emailsInput[index] = it },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("ejemplo@correo.com") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val email = emailsInput.getOrElse(index) { "" }
                            if (email.isNotBlank()) {
                                if (!telefonosPendientes.contains(email)) telefonosPendientes.add(email)
                                if (!invitacionesEnviadas.contains(email)) invitacionesEnviadas.add(email)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(56.dp)
                    ) { Text("Invitar", fontSize = 12.sp, color = Color.White) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (invitacionesEnviadas.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Invitaciones pendientes (${invitacionesEnviadas.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(8.dp))
                invitacionesEnviadas.forEach { email ->
                    Text(text = email, fontSize = 13.sp, color = Color(0xFF558B2F))
                }
                Text(text = "Se enviarán por correo al crear el grupo", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }

    Spacer(modifier = Modifier.height(30.dp))
    Button(
        onClick = { val v = personasInput.filter { it.isNotBlank() }; if (v.isNotEmpty()) navigate("agregar_gasto_inicial") },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
        shape = RoundedCornerShape(25.dp)
    ) { Text(stringResource(R.string.grupos_btn_next), fontSize = 16.sp) }
    if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
}

@Composable
private fun AgregarGastoInicialScreen(nombreGrupo: String, personasInput: MutableList<String>, montoTeDeben: String, onMontoChange: (String) -> Unit, descripcionTeDeben: String, onDescChange: (String) -> Unit, state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, purple: Color, clearNombre: () -> Unit, clearNum: () -> Unit, fotoTicketUri: String?, telefonosPendientes: List<String> = emptyList(), navigate: (String) -> Unit, onNavigateToRuleta: (List<String>, Int) -> Unit = { _, _ -> }) {
    val validPersonas = personasInput.filter { it.isNotBlank() }
    var grupoCreado by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("agregar_personas") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = nombreGrupo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = stringResource(R.string.grupos_agregar_gasto), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    Spacer(modifier = Modifier.height(24.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = stringResource(R.string.grupos_personas_count, validPersonas.size), fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            validPersonas.forEach { persona -> Text(text = persona, fontSize = 16.sp, color = darkBlue) }
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = montoTeDeben, onValueChange = onMontoChange, label = { Text(stringResource(R.string.grupos_label_monto_total)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            if (montoTeDeben.isNotBlank()) { 
                val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0
                val montoPorPersona = montoTotal / validPersonas.size
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.grupos_te_deben_calc, String.format("%.2f", montoPorPersona)), color = lightBlue, fontWeight = FontWeight.Bold) 
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = descripcionTeDeben, onValueChange = onDescChange, label = { Text(stringResource(R.string.grupos_label_concepto)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
        }
    }
    Spacer(modifier = Modifier.height(30.dp))
    
    if (state.isLoading) { CircularProgressIndicator(color = lightBlue) }
    else {
        Button(
            onClick = { 
                vm.crearGrupo(nombreGrupo, validPersonas, fotoTicketUri, null)
            }, 
            modifier = Modifier.fillMaxWidth().height(50.dp), 
            colors = ButtonDefaults.buttonColors(containerColor = darkBlue), 
            shape = RoundedCornerShape(25.dp)
        ) { 
            Text(stringResource(R.string.grupos_btn_crear_grupo), fontSize = 16.sp) 
        }
    }
    
    LaunchedEffect(state.grupoActual) {
        if (state.grupoActual != null && state.grupoActual!!.nombre == nombreGrupo && !grupoCreado) {
            grupoCreado = true
            val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0
            if (montoTotal > 0) { 
                val montoPorPersona = montoTotal / validPersonas.size
                validPersonas.forEach { persona -> 
                    vm.agregarGasto(persona, montoPorPersona, descripcionTeDeben, "te_deben") 
                } 
            }
            telefonosPendientes.forEach { email ->
                vm.enviarInvitacionEmail(context, state.grupoActual!!.nombre, email, state.grupoActual!!.id)
            }
            onNavigateToRuleta(validPersonas, state.grupoActual!!.id)
        }
    }
    
    if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
}

@Composable
private fun EditarGrupoScreen(state: GruposUiState, vm: GruposViewModel, nuevaPersona: String, onNuevaPersonaChange: (String) -> Unit, darkBlue: Color, lightBlue: Color, purple: Color, navigate: (String) -> Unit) {
    val grupo = state.grupoActual ?: run { navigate("inicio"); return }
    var mostrarInputPersona by remember { mutableStateOf(false) }
    var mostrarInputGasto by remember { mutableStateOf(false) }
    var mostrarInputInvitacion by remember { mutableStateOf(false) }
    var emailInvitacion by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    var montoGasto by remember { mutableStateOf("") }
    var descripcionGasto by remember { mutableStateOf("") }
    val invitacionesEnviadas = remember { mutableStateListOf<String>() }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = stringResource(R.string.grupos_editar_titulo, grupo.nombre), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { mostrarInputInvitacion = !mostrarInputInvitacion },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text("Invitar por correo electrónico", fontSize = 16.sp, color = Color.White)
    }

    if (mostrarInputInvitacion) {
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = emailInvitacion,
                    onValueChange = { emailInvitacion = it },
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("ejemplo@correo.com") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (emailInvitacion.isNotBlank()) {
                            vm.enviarInvitacionEmail(context, grupo.nombre, emailInvitacion, grupo.id)
                            invitacionesEnviadas.add(emailInvitacion)
                            emailInvitacion = ""
                            mostrarInputInvitacion = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
                    shape = RoundedCornerShape(25.dp)
                ) { Text("Enviar invitación", color = Color.White) }
            }
        }
    }

    if (invitacionesEnviadas.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Invitaciones enviadas (${invitacionesEnviadas.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(4.dp))
                invitacionesEnviadas.forEach { email -> Text(text = email, fontSize = 13.sp, color = Color(0xFF558B2F)) }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    Text(text = stringResource(R.string.grupos_personas_del_grupo), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
    Spacer(modifier = Modifier.height(12.dp))
    grupo.personas.forEach { persona ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = persona, fontSize = 16.sp)
                IconButton(onClick = { vm.eliminarPersona(persona) }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    if (!mostrarInputPersona) {
        Button(onClick = { mostrarInputPersona = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text(stringResource(R.string.grupos_btn_agregar_persona), fontSize = 16.sp) }
    } else {
        Text(text = stringResource(R.string.grupos_btn_agregar_persona), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = nuevaPersona, onValueChange = onNuevaPersonaChange, label = { Text(stringResource(R.string.registro_nombre)) }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { if (nuevaPersona.isNotBlank()) { vm.agregarPersona(nuevaPersona); onNuevaPersonaChange(""); mostrarInputPersona = false } }) { Icon(Icons.Default.Add, contentDescription = null, tint = lightBlue) }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    if (!mostrarInputGasto) {
        Button(onClick = { mostrarInputGasto = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = purple), shape = RoundedCornerShape(25.dp)) { Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text(stringResource(R.string.grupos_btn_agregar_gasto), fontSize = 16.sp) }
    } else {
        Text(text = stringResource(R.string.grupos_btn_agregar_gasto), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = stringResource(R.string.grupos_personas_count, grupo.personas.size), fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                grupo.personas.forEach { persona -> Text(text = persona, fontSize = 16.sp, color = darkBlue) }
                Spacer(modifier = Modifier.height(20.dp))
                TextField(value = montoGasto, onValueChange = { montoGasto = it }, label = { Text(stringResource(R.string.grupos_label_monto_total)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
                if (montoGasto.isNotBlank()) { 
                    val mt = montoGasto.toDoubleOrNull() ?: 0.0
                    val mp = mt / grupo.personas.size
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.grupos_te_deben_calc, String.format("%.2f", mp)), color = lightBlue, fontWeight = FontWeight.Bold) 
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = descripcionGasto, onValueChange = { descripcionGasto = it }, label = { Text(stringResource(R.string.grupos_label_concepto)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { val mt = montoGasto.toDoubleOrNull() ?: 0.0; if (mt > 0) { val mp = mt / grupo.personas.size; grupo.personas.forEach { persona -> vm.agregarGasto(persona, mp, descripcionGasto, "te_deben") }; montoGasto = ""; descripcionGasto = ""; mostrarInputGasto = false } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text(stringResource(R.string.grupos_label_registrar), fontSize = 16.sp) }
            }
        }
    }
    if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GastosDetailScreen(state: GruposUiState, vm: GruposViewModel, montoTeDeben: String, onMontoTeDebenChange: (String) -> Unit, descripcionTeDeben: String, onDescTeDebenChange: (String) -> Unit, montoTuDebes: String, onMontoTuDebesChange: (String) -> Unit, descripcionTuDebes: String, onDescTuDebesChange: (String) -> Unit, personaTuDebes: String, onPersonaTuDebesChange: (String) -> Unit, expandedTuDebes: Boolean, onExpandedChange: (Boolean) -> Unit, darkBlue: Color, lightBlue: Color, purple: Color, navigate: (String) -> Unit) {
    val grupo = state.grupoActual ?: run { navigate("inicio"); return }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = grupo.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue, modifier = Modifier.weight(1f))
        IconButton(onClick = { navigate("editar_grupo") }) { Icon(Icons.Default.Edit, contentDescription = null, tint = darkBlue) }
    }
    Spacer(modifier = Modifier.height(24.dp))
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }
    if (tipoSeleccionado == null) {
        Card(modifier = Modifier.fillMaxWidth().height(100.dp).clickable { tipoSeleccionado = "te_deben" }, colors = CardDefaults.cardColors(containerColor = lightBlue), shape = RoundedCornerShape(20.dp)) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.grupos_te_deben), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) } }
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().height(100.dp).clickable { tipoSeleccionado = "tu_debes" }, colors = CardDefaults.cardColors(containerColor = purple), shape = RoundedCornerShape(20.dp)) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.grupos_tu_debes), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) } }
        Spacer(modifier = Modifier.height(32.dp))
        var gastoEditandoId by remember { mutableStateOf<Int?>(null) }
        var montoEditando by remember { mutableStateOf("") }

        var paginaActual by remember { mutableStateOf(0) }
        val gastosPorPagina = 3
        val totalPaginas = if (grupo.gastos.isEmpty()) 1 else (grupo.gastos.size + gastosPorPagina - 1) / gastosPorPagina
        
        if (grupo.gastos.isNotEmpty()) {
            Text(text = stringResource(R.string.grupos_gastos_registrados), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))
            val gastosEnPagina = grupo.gastos.drop(paginaActual * gastosPorPagina).take(gastosPorPagina)
            gastosEnPagina.forEach { gasto ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = if (gasto.tipo == "te_deben") lightBlue.copy(alpha = 0.2f) else purple.copy(alpha = 0.2f)), shape = RoundedCornerShape(12.dp)) {
                    if (gastoEditandoId == gasto.id) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = if (gasto.tipo == "te_deben") stringResource(R.string.grupos_te_debe_formato, gasto.persona) else stringResource(R.string.grupos_debes_a_formato, gasto.persona), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextField(value = montoEditando, onValueChange = { montoEditando = it }, label = { Text(stringResource(R.string.grupos_label_monto)) }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(8.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { val nm = montoEditando.toDoubleOrNull(); if (nm != null && nm > 0) { vm.editarGasto(gasto.id, nm); gastoEditandoId = null; montoEditando = "" } }, colors = ButtonDefaults.buttonColors(containerColor = lightBlue)) { Text("OK") }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = if (gasto.tipo == "te_deben") stringResource(R.string.grupos_te_debe_formato, gasto.persona) else stringResource(R.string.grupos_debes_a_formato, gasto.persona), fontWeight = FontWeight.Bold)
                                Text(text = gasto.monto.toString())
                                if (gasto.descripcion.isNotBlank()) { Text(text = gasto.descripcion, color = Color.Gray) }
                            }
                            IconButton(onClick = { gastoEditandoId = gasto.id; montoEditando = gasto.monto.toString() }) { Icon(Icons.Default.Edit, contentDescription = null) }
                            IconButton(onClick = { vm.eliminarGasto(gasto.id) }) { Icon(Icons.Default.Delete, contentDescription = null) }
                        }
                    }
                }
            }
            if (grupo.gastos.size > gastosPorPagina) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { if (paginaActual > 0) paginaActual-- }, enabled = paginaActual > 0, colors = ButtonDefaults.buttonColors(containerColor = darkBlue)) { Text("<") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = (paginaActual + 1).toString() + " / " + totalPaginas.toString(), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { if (paginaActual < totalPaginas - 1) paginaActual++ }, enabled = paginaActual < totalPaginas - 1, colors = ButtonDefaults.buttonColors(containerColor = darkBlue)) { Text(">") }
                }
            }
        }
    } else if (tipoSeleccionado == "te_deben") {
        TeDebenSection(grupo, state, vm, montoTeDeben, onMontoTeDebenChange, descripcionTeDeben, onDescTeDebenChange, darkBlue, lightBlue) { tipoSeleccionado = null }
    } else {
        TuDebesSection(grupo, state, vm, montoTuDebes, onMontoTuDebesChange, descripcionTuDebes, onDescTuDebesChange, personaTuDebes, onPersonaTuDebesChange, expandedTuDebes, onExpandedChange, darkBlue, purple) { tipoSeleccionado = null }
    }
}

@Composable
private fun TeDebenSection(grupo: Grupo, state: GruposUiState, vm: GruposViewModel, montoTeDeben: String, onMontoTeDebenChange: (String) -> Unit, descripcionTeDeben: String, onDescTeDebenChange: (String) -> Unit, darkBlue: Color, lightBlue: Color, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }; Text(text = stringResource(R.string.grupos_te_deben), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = lightBlue) }
    Spacer(modifier = Modifier.height(20.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = stringResource(R.string.grupos_personas_count, grupo.personas.size), fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            grupo.personas.forEach { persona -> Text(text = persona, fontSize = 16.sp, color = darkBlue) }
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = montoTeDeben, onValueChange = onMontoTeDebenChange, label = { Text(stringResource(R.string.grupos_label_monto_total)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            if (montoTeDeben.isNotBlank()) { 
                val mt = montoTeDeben.toDoubleOrNull() ?: 0.0
                val mp = mt / grupo.personas.size
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.grupos_te_deben_calc, String.format("%.2f", mp)), color = lightBlue, fontWeight = FontWeight.Bold) 
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = descripcionTeDeben, onValueChange = onDescTeDebenChange, label = { Text(stringResource(R.string.grupos_label_porque)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(24.dp))
            if (state.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = lightBlue) }
            else { Button(onClick = { val mt = montoTeDeben.toDoubleOrNull() ?: 0.0; if (mt > 0) { val mp = mt / grupo.personas.size; grupo.personas.forEach { persona -> vm.agregarGasto(persona, mp, descripcionTeDeben, "te_deben") }; onMontoTeDebenChange(""); onDescTeDebenChange(""); onBack() } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text(stringResource(R.string.grupos_label_registrar), fontSize = 16.sp) } }
            
            if (grupo.fotoTicketUri != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = stringResource(R.string.grupos_ticket_del_grupo), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    coil.compose.AsyncImage(
                        model = android.net.Uri.parse(grupo.fotoTicketUri),
                        contentDescription = "Foto del ticket",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Text(text = stringResource(R.string.grupos_foto_guardada), color = Color.Gray, fontSize = 12.sp)
            }
            
            if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TuDebesSection(grupo: Grupo, state: GruposUiState, vm: GruposViewModel, montoTuDebes: String, onMontoTuDebesChange: (String) -> Unit, descripcionTuDebes: String, onDescTuDebesChange: (String) -> Unit, personaTuDebes: String, onPersonaTuDebesChange: (String) -> Unit, expandedTuDebes: Boolean, onExpandedChange: (Boolean) -> Unit, darkBlue: Color, purple: Color, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }; Text(text = stringResource(R.string.grupos_tu_debes), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = purple) }
    Spacer(modifier = Modifier.height(20.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            ExposedDropdownMenuBox(expanded = expandedTuDebes, onExpandedChange = { onExpandedChange(!expandedTuDebes) }) {
                TextField(value = personaTuDebes, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.grupos_label_a_quien_debes)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTuDebes) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
                ExposedDropdownMenu(expanded = expandedTuDebes, onDismissRequest = { onExpandedChange(false) }) { grupo.personas.forEach { persona -> DropdownMenuItem(text = { Text(persona) }, onClick = { onPersonaTuDebesChange(persona); onExpandedChange(false) }) } }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = montoTuDebes, onValueChange = onMontoTuDebesChange, label = { Text(stringResource(R.string.grupos_label_cuanto)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = descripcionTuDebes, onValueChange = onDescTuDebesChange, label = { Text(stringResource(R.string.grupos_label_porque)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(24.dp))
            if (state.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = purple) }
            else { Button(onClick = { val m = montoTuDebes.toDoubleOrNull() ?: 0.0; if (m > 0 && personaTuDebes.isNotBlank()) { vm.agregarGasto(personaTuDebes, m, descripcionTuDebes, "tu_debes"); onMontoTuDebesChange(""); onDescTuDebesChange(""); onPersonaTuDebesChange(""); onBack() } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = purple), shape = RoundedCornerShape(25.dp)) { Text(stringResource(R.string.grupos_label_registrar), fontSize = 16.sp) } }
            if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
        }
    }
}
