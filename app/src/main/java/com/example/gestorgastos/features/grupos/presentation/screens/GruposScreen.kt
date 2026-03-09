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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.features.grupos.domain.entities.Grupo
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GruposScreen(
    viewModel: GruposViewModel? = null,
    onLogout: () -> Unit,
    onNavigateToRuleta: (List<String>, Int) -> Unit = { _, _ -> },
    onNavigateToVerGanadores: () -> Unit = {}
) {
    // Usar el ViewModel pasado o intentar obtenerlo de Hilt
    val vm: GruposViewModel = viewModel ?: hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf("inicio") }
    var nombreGrupo by remember { mutableStateOf("") }
    var numPersonas by remember { mutableStateOf("") }
    val personasInput = remember { mutableStateListOf<String>() }
    var nuevaPersona by remember { mutableStateOf("") }
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
    
    // Guardar la pantalla actual antes de ir a la ruleta
    var screenBeforeRuleta by remember { mutableStateOf<String?>(null) }
    
    // Obtener el ganador de la ruleta desde savedStateHandle
    val navController = androidx.navigation.compose.rememberNavController()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<String>("ganadorRuleta")?.observeForever { ganador ->
            if (ganador != null) {
                vm.setGanadorRuleta(ganador)
                savedStateHandle.remove<String>("ganadorRuleta")
                // Restaurar la pantalla donde estábamos
                screenBeforeRuleta?.let { currentScreen = it }
            }
        }
    }
    
    // Wrapper para onNavigateToRuleta que guarda la pantalla actual
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
                "inicio" -> InicioScreen(state, vm, darkBlue, lightBlue, onLogout, { currentScreen = it }, onNavigateToVerGanadores)
                "crear_grupo" -> CrearGrupoScreen(nombreGrupo, { nombreGrupo = it }, numPersonas, { numPersonas = it }, darkBlue, lightBlue, personasInput, vm, { fotoTicketUri = it }, { currentScreen = it }, navigateToRuletaWrapper)
                "agregar_personas" -> AgregarPersonasScreen(nombreGrupo, personasInput, state, darkBlue, lightBlue) { currentScreen = it }
                "agregar_gasto_inicial" -> AgregarGastoInicialScreen(nombreGrupo, personasInput, montoTeDeben, { montoTeDeben = it }, descripcionTeDeben, { descripcionTeDeben = it }, state, vm, darkBlue, lightBlue, purple, { nombreGrupo = "" }, { numPersonas = "" }, fotoTicketUri, { currentScreen = it }, navigateToRuletaWrapper)
                "editar_grupo" -> EditarGrupoScreen(state, vm, nuevaPersona, { nuevaPersona = it }, darkBlue, lightBlue, purple) { currentScreen = it }
                "gastos" -> GastosDetailScreen(state, vm, montoTeDeben, { montoTeDeben = it }, descripcionTeDeben, { descripcionTeDeben = it }, montoTuDebes, { montoTuDebes = it }, descripcionTuDebes, { descripcionTuDebes = it }, personaTuDebes, { personaTuDebes = it }, expandedTuDebes, { expandedTuDebes = it }, darkBlue, lightBlue, purple) { currentScreen = it }
            }
        }
    }
}
@Composable
private fun InicioScreen(state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, onLogout: () -> Unit, navigate: (String) -> Unit, onNavigateToVerGanadores: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onLogout) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = darkBlue) }
        Text(text = "Mis Grupos", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(24.dp))
    if (state.isLoading) { CircularProgressIndicator(color = lightBlue) }
    else if (state.grupos.isEmpty()) { Text(text = "No tienes grupos aun", style = MaterialTheme.typography.bodyLarge, color = Color.Gray) }
    else {
        state.grupos.forEach { grupo ->
            Card(modifier = Modifier.fillMaxWidth().clickable { vm.seleccionarGrupo(grupo); navigate("gastos") }, colors = CardDefaults.cardColors(containerColor = lightBlue), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = grupo.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = grupo.personas.size.toString() + " personas", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
        Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Generar grupo", fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(12.dp))
    Button(onClick = onNavigateToVerGanadores, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)), shape = RoundedCornerShape(25.dp)) {
        Text("Personas", fontSize = 16.sp, color = Color(0xFF5C6BC0), fontWeight = FontWeight.Bold)
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
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var fotoCaptured by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Launcher para la cámara
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && fotoUri != null) {
            // Foto capturada exitosamente
            fotoCaptured = true
            onFotoUriChange(fotoUri.toString())
        } else {
            // No se capturó la foto
            fotoCaptured = false
            fotoUri = null
            onFotoUriChange(null)
        }
    }
    
    // Launcher para permisos
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido, abrir cámara
            val photoFile = java.io.File.createTempFile(
                "gasto_${System.currentTimeMillis()}",
                ".jpg",
                context.getExternalFilesDir(null)
            )
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            fotoUri = uri
            fotoCaptured = false
            cameraLauncher.launch(uri)
        }
    }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = "Crear Grupo", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(30.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            TextField(value = nombreGrupo, onValueChange = onNombreChange, label = { Text("Nombre del grupo") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = numPersonas, onValueChange = onNumChange, label = { Text("Cuantas personas") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    // Verificar y solicitar permiso de cámara
                    val permission = android.Manifest.permission.CAMERA
                    when {
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context, permission
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                            // Permiso ya concedido, abrir cámara
                            val photoFile = java.io.File.createTempFile(
                                "gasto_${System.currentTimeMillis()}",
                                ".jpg",
                                context.getExternalFilesDir(null)
                            )
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                photoFile
                            )
                            fotoUri = uri
                            fotoCaptured = false
                            cameraLauncher.launch(uri)
                        }
                        else -> {
                            // Solicitar permiso
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
                Text("Abrir cámara para mostrar el ticket", fontSize = 14.sp) 
            }
            
            // Mostrar foto capturada
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
                            Text("✓ Foto del ticket guardada", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Se adjuntará al grupo", color = Color(0xFF558B2F), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(onClick = { val num = numPersonas.toIntOrNull() ?: 0; if (nombreGrupo.isNotBlank() && num > 0) { personasInput.clear(); repeat(num) { personasInput.add("") }; navigate("agregar_personas") } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text("Continuar", fontSize = 16.sp) }
}

@Composable
private fun AgregarPersonasScreen(nombreGrupo: String, personasInput: MutableList<String>, state: GruposUiState, darkBlue: Color, lightBlue: Color, navigate: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("crear_grupo") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = nombreGrupo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Agregar Participantes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    Spacer(modifier = Modifier.height(24.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            personasInput.forEachIndexed { index, nombre -> TextField(value = nombre, onValueChange = { personasInput[index] = it }, label = { Text("Persona " + (index + 1).toString()) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp)); Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(onClick = { val validPersonas = personasInput.filter { it.isNotBlank() }; if (validPersonas.isNotEmpty()) { navigate("agregar_gasto_inicial") } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text("Next", fontSize = 16.sp) }
    if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
}

@Composable
private fun AgregarGastoInicialScreen(nombreGrupo: String, personasInput: MutableList<String>, montoTeDeben: String, onMontoChange: (String) -> Unit, descripcionTeDeben: String, onDescChange: (String) -> Unit, state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, purple: Color, clearNombre: () -> Unit, clearNum: () -> Unit, fotoTicketUri: String?, navigate: (String) -> Unit, onNavigateToRuleta: (List<String>, Int) -> Unit = { _, _ -> }) {
    val validPersonas = personasInput.filter { it.isNotBlank() }
    var grupoCreado by remember { mutableStateOf(false) }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("agregar_personas") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = nombreGrupo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Agregar Gasto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = darkBlue)
    Spacer(modifier = Modifier.height(24.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = validPersonas.size.toString() + " personas", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            validPersonas.forEach { persona -> Text(text = persona, fontSize = 16.sp, color = darkBlue) }
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = montoTeDeben, onValueChange = onMontoChange, label = { Text("Monto total") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            if (montoTeDeben.isNotBlank()) { val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0; val montoPorPersona = montoTotal / validPersonas.size; Spacer(modifier = Modifier.height(8.dp)); Text(text = "Cada uno te debe: $${String.format("%.2f", montoPorPersona)}", color = lightBlue, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = descripcionTeDeben, onValueChange = onDescChange, label = { Text("Concepto") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
        }
    }
    Spacer(modifier = Modifier.height(30.dp))
    
    if (state.isLoading) { CircularProgressIndicator(color = lightBlue) }
    else {
        // Botón para crear grupo
        Button(
            onClick = { 
                // Crear el grupo primero
                vm.crearGrupo(nombreGrupo, validPersonas, fotoTicketUri, null)
            }, 
            modifier = Modifier.fillMaxWidth().height(50.dp), 
            colors = ButtonDefaults.buttonColors(containerColor = darkBlue), 
            shape = RoundedCornerShape(25.dp)
        ) { 
            Text("Crear Grupo", fontSize = 16.sp) 
        }
    }
    
    // Navegar a la ruleta cuando se crea el grupo
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
            // Navegar a la ruleta con el ID del grupo creado
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
    var montoGasto by remember { mutableStateOf("") }
    var descripcionGasto by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Text(text = "Editar " + grupo.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text(text = "Personas del grupo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
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
        Button(onClick = { mostrarInputPersona = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Agregar persona", fontSize = 16.sp) }
    } else {
        Text(text = "Agregar persona", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = nuevaPersona, onValueChange = onNuevaPersonaChange, label = { Text("Nombre") }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { if (nuevaPersona.isNotBlank()) { vm.agregarPersona(nuevaPersona); onNuevaPersonaChange(""); mostrarInputPersona = false } }) { Icon(Icons.Default.Add, contentDescription = null, tint = lightBlue) }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    if (!mostrarInputGasto) {
        Button(onClick = { mostrarInputGasto = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = purple), shape = RoundedCornerShape(25.dp)) { Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Agregar gasto", fontSize = 16.sp) }
    } else {
        Text(text = "Agregar gasto", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = grupo.personas.size.toString() + " personas", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                grupo.personas.forEach { persona -> Text(text = persona, fontSize = 16.sp, color = darkBlue) }
                Spacer(modifier = Modifier.height(20.dp))
                TextField(value = montoGasto, onValueChange = { montoGasto = it }, label = { Text("Monto total") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
                if (montoGasto.isNotBlank()) { val mt = montoGasto.toDoubleOrNull() ?: 0.0; val mp = mt / grupo.personas.size; Spacer(modifier = Modifier.height(8.dp)); Text(text = "Cada uno te debe: $${String.format("%.2f", mp)}", color = lightBlue, fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = descripcionGasto, onValueChange = { descripcionGasto = it }, label = { Text("Concepto") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { val mt = montoGasto.toDoubleOrNull() ?: 0.0; if (mt > 0) { val mp = mt / grupo.personas.size; grupo.personas.forEach { persona -> vm.agregarGasto(persona, mp, descripcionGasto, "te_deben") }; montoGasto = ""; descripcionGasto = ""; mostrarInputGasto = false } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text("Registrar", fontSize = 16.sp) }
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
        Card(modifier = Modifier.fillMaxWidth().height(100.dp).clickable { tipoSeleccionado = "te_deben" }, colors = CardDefaults.cardColors(containerColor = lightBlue), shape = RoundedCornerShape(20.dp)) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = "Te deben", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) } }
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().height(100.dp).clickable { tipoSeleccionado = "tu_debes" }, colors = CardDefaults.cardColors(containerColor = purple), shape = RoundedCornerShape(20.dp)) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = "Tu debes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) } }
        Spacer(modifier = Modifier.height(32.dp))
        var gastoEditandoId by remember { mutableStateOf<Int?>(null) }
        var montoEditando by remember { mutableStateOf("") }





        // PAGINACION DE GASTOS
        var paginaActual by remember { mutableStateOf(0) }
        val gastosPorPagina = 3
        val totalPaginas = if (grupo.gastos.isEmpty()) 1 else (grupo.gastos.size + gastosPorPagina - 1) / gastosPorPagina
        // ==========================================
        if (grupo.gastos.isNotEmpty()) {
            Text(text = "Gastos registrados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))
            val gastosEnPagina = grupo.gastos.drop(paginaActual * gastosPorPagina).take(gastosPorPagina)
            gastosEnPagina.forEach { gasto ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = if (gasto.tipo == "te_deben") lightBlue.copy(alpha = 0.2f) else purple.copy(alpha = 0.2f)), shape = RoundedCornerShape(12.dp)) {
                    if (gastoEditandoId == gasto.id) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = if (gasto.tipo == "te_deben") gasto.persona + " te debe" else "Debes a " + gasto.persona, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextField(value = montoEditando, onValueChange = { montoEditando = it }, label = { Text("Monto") }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(8.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { val nm = montoEditando.toDoubleOrNull(); if (nm != null && nm > 0) { vm.editarGasto(gasto.id, nm); gastoEditandoId = null; montoEditando = "" } }, colors = ButtonDefaults.buttonColors(containerColor = lightBlue)) { Text("OK") }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = if (gasto.tipo == "te_deben") gasto.persona + " te debe" else "Debes a " + gasto.persona, fontWeight = FontWeight.Bold)
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
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }; Text(text = "Te deben", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = lightBlue) }
    Spacer(modifier = Modifier.height(20.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = grupo.personas.size.toString() + " personas", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            grupo.personas.forEach { persona -> Text(text = persona, fontSize = 16.sp, color = darkBlue) }
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = montoTeDeben, onValueChange = onMontoTeDebenChange, label = { Text("Monto total") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            if (montoTeDeben.isNotBlank()) { val mt = montoTeDeben.toDoubleOrNull() ?: 0.0; val mp = mt / grupo.personas.size; Spacer(modifier = Modifier.height(8.dp)); Text(text = "Cada uno te debe: $${String.format("%.2f", mp)}", color = lightBlue, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = descripcionTeDeben, onValueChange = onDescTeDebenChange, label = { Text("Por que") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(24.dp))
            if (state.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = lightBlue) }
            else { Button(onClick = { val mt = montoTeDeben.toDoubleOrNull() ?: 0.0; if (mt > 0) { val mp = mt / grupo.personas.size; grupo.personas.forEach { persona -> vm.agregarGasto(persona, mp, descripcionTeDeben, "te_deben") }; onMontoTeDebenChange(""); onDescTeDebenChange(""); onBack() } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text("Registrar", fontSize = 16.sp) } }
            
            // Mostrar foto del ticket si existe
            if (grupo.fotoTicketUri != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Ticket del grupo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                        contentDescription = "Ticket",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Text(text = "Foto guardada", color = Color.Gray, fontSize = 12.sp)
            }
            
            if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TuDebesSection(grupo: Grupo, state: GruposUiState, vm: GruposViewModel, montoTuDebes: String, onMontoTuDebesChange: (String) -> Unit, descripcionTuDebes: String, onDescTuDebesChange: (String) -> Unit, personaTuDebes: String, onPersonaTuDebesChange: (String) -> Unit, expandedTuDebes: Boolean, onExpandedChange: (Boolean) -> Unit, darkBlue: Color, purple: Color, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }; Text(text = "Tu debes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = purple) }
    Spacer(modifier = Modifier.height(20.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            ExposedDropdownMenuBox(expanded = expandedTuDebes, onExpandedChange = { onExpandedChange(!expandedTuDebes) }) {
                TextField(value = personaTuDebes, onValueChange = {}, readOnly = true, label = { Text("A quien le debes") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTuDebes) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
                ExposedDropdownMenu(expanded = expandedTuDebes, onDismissRequest = { onExpandedChange(false) }) { grupo.personas.forEach { persona -> DropdownMenuItem(text = { Text(persona) }, onClick = { onPersonaTuDebesChange(persona); onExpandedChange(false) }) } }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = montoTuDebes, onValueChange = onMontoTuDebesChange, label = { Text("Cuanto") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = descripcionTuDebes, onValueChange = onDescTuDebesChange, label = { Text("Por que") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(24.dp))
            if (state.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = purple) }
            else { Button(onClick = { val m = montoTuDebes.toDoubleOrNull() ?: 0.0; if (m > 0 && personaTuDebes.isNotBlank()) { vm.agregarGasto(personaTuDebes, m, descripcionTuDebes, "tu_debes"); onMontoTuDebesChange(""); onDescTuDebesChange(""); onPersonaTuDebesChange(""); onBack() } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = purple), shape = RoundedCornerShape(25.dp)) { Text("Registrar", fontSize = 16.sp) } }
            if (state.error != null) { Spacer(modifier = Modifier.height(8.dp)); Text(state.error ?: "", color = Color.Red) }
        }
    }
}
