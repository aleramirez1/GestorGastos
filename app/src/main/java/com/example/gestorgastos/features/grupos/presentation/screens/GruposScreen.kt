package com.example.gestorgastos.features.grupos.presentation.screens

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gestorgastos.R
import com.example.gestorgastos.features.grupos.domain.entities.Grupo
import com.example.gestorgastos.features.grupos.presentation.viewmodels.GruposViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GruposScreen(
    viewModel: GruposViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToRuleta: (List<String>, Int, List<String>) -> Unit = { _, _, _ -> },
    onNavigateToVerGanadores: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf("inicio") }
    var nombreGrupo by remember { mutableStateOf("") }
    var numPersonas by remember { mutableStateOf("") }
    val personasInput = remember { mutableStateListOf<String>() }
    var nuevaPersona by remember { mutableStateOf("") }
    var montoTeDeben by remember { mutableStateOf("") }
    var descripcionTeDeben by remember { mutableStateOf("") }
    
    // Estados para "Tu debes"
    var montoTuDebes by remember { mutableStateOf("") }
    var descripcionTuDebes by remember { mutableStateOf("") }
    var personaTuDebes by remember { mutableStateOf("") }
    var expandedTuDebes by remember { mutableStateOf(false) }

    // Estados para Ahorro
    var isAhorro by remember { mutableStateOf(false) }
    var metaAhorro by remember { mutableStateOf("") }
    var fechaLimite by remember { mutableStateOf("") }

    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Canvas(modifier = Modifier.size(150.dp).offset(x = (-40).dp, y = (-40).dp)) { drawCircle(color = lightBlue) }
        Canvas(modifier = Modifier.size(100.dp).offset(x = 320.dp, y = 20.dp)) { drawCircle(color = darkBlue) }
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Spacer(modifier = Modifier.height(100.dp))
            when (currentScreen) {
                "inicio" -> InicioScreen(state, viewModel, darkBlue, lightBlue, onLogout, onNavigateToPerfil, { currentScreen = it }, onNavigateToVerGanadores, onNavigateToRuleta)
                "crear_grupo" -> CrearGrupoScreen(nombreGrupo, { nombreGrupo = it }, numPersonas, { numPersonas = it }, isAhorro, { isAhorro = it }, metaAhorro, { metaAhorro = it }, fechaLimite, { fechaLimite = it }, darkBlue, lightBlue, personasInput, viewModel, { currentScreen = it })
                "agregar_personas" -> AgregarPersonasScreen(nombreGrupo, personasInput, state, darkBlue, lightBlue) { currentScreen = it }
                "agregar_gasto_inicial" -> AgregarGastoInicialScreen(nombreGrupo, personasInput, montoTeDeben, { montoTeDeben = it }, descripcionTeDeben, { descripcionTeDeben = it }, isAhorro, metaAhorro, fechaLimite, state, viewModel, darkBlue, lightBlue, { currentScreen = it }, onNavigateToRuleta)
                "editar_grupo" -> EditarGrupoScreen(state, viewModel, nuevaPersona, { nuevaPersona = it }, darkBlue, lightBlue) { currentScreen = it }
                "gastos" -> GastosDetailScreen(state, viewModel, montoTeDeben, { montoTeDeben = it }, descripcionTeDeben, { descripcionTeDeben = it }, montoTuDebes, { montoTuDebes = it }, descripcionTuDebes, { descripcionTuDebes = it }, personaTuDebes, { personaTuDebes = it }, expandedTuDebes, { expandedTuDebes = it }, darkBlue, lightBlue, purple) { currentScreen = it }
            }
        }
    }
}

@Composable
private fun InicioScreen(state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, onLogout: () -> Unit, onNavigateToPerfil: () -> Unit, navigate: (String) -> Unit, onNavigateToVerGanadores: () -> Unit, onNavigateToRuleta: (List<String>, Int, List<String>) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onLogout) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = darkBlue) }
        Text(text = "Mis Grupos", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = darkBlue, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White).clickable { onNavigateToPerfil() }, contentAlignment = Alignment.Center) {
            if (state.fotoPerfil != null) { AsyncImage(model = state.fotoPerfil, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
            else { Icon(Icons.Default.Person, contentDescription = null, tint = darkBlue) }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    TextField(value = state.searchQuery, onValueChange = { vm.onSearchQueryChange(it) }, placeholder = { Text("Buscar grupo...") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = lightBlue, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
    Spacer(modifier = Modifier.height(24.dp))
    if (state.isLoading) { CircularProgressIndicator(color = lightBlue) }
    else if (state.gruposFiltrados.isEmpty()) { Text(text = "No hay grupos", color = Color.Gray) }
    else {
        state.gruposFiltrados.forEach { grupo ->
            Card(modifier = Modifier.fillMaxWidth().clickable { vm.seleccionarGrupo(grupo); navigate("gastos") }, colors = CardDefaults.cardColors(containerColor = if (grupo.isAhorro) Color(0xFF81C784) else lightBlue), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = grupo.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        if (grupo.isAhorro) { 
                            Text(text = "Meta: $${grupo.metaAhorro}", fontSize = 14.sp, color = Color.White)
                            LinearProgressIndicator(progress = { grupo.progreso }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), color = Color.White, trackColor = Color.White.copy(alpha = 0.3f))
                        } else {
                            Text(text = "${grupo.personas.size} personas", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    if (!grupo.isAhorro) {
                        IconButton(onClick = { vm.seleccionarGrupo(grupo); onNavigateToRuleta(grupo.personas, grupo.id, grupo.personasQueYaRecibieron) }) { Icon(Icons.Default.Casino, contentDescription = "Ruleta", tint = Color.White) }
                    }
                    IconButton(onClick = { vm.seleccionarGrupo(grupo); navigate("editar_grupo") }) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = { navigate("crear_grupo") }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue), shape = RoundedCornerShape(25.dp)) { Icon(Icons.Default.Add, contentDescription = null); Text("Nuevo Grupo") }
}

@Composable
private fun CrearGrupoScreen(nombre: String, onNombre: (String) -> Unit, num: String, onNum: (String) -> Unit, ahorro: Boolean, onAhorro: (Boolean) -> Unit, meta: String, onMeta: (String) -> Unit, fecha: String, onFecha: (String) -> Unit, darkBlue: Color, lightBlue: Color, personas: MutableList<String>, vm: GruposViewModel, navigate: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }; Text(text = "Crear Grupo", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = darkBlue) }
    Spacer(modifier = Modifier.height(30.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            TextField(value = nombre, onValueChange = onNombre, label = { Text("Nombre del grupo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = num, onValueChange = onNum, label = { Text("¿Cuántas personas?") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = ahorro, onCheckedChange = onAhorro); Text("Es un Grupo de Ahorro", fontWeight = FontWeight.Bold) }
            if (ahorro) {
                TextField(value = meta, onValueChange = onMeta, label = { Text("Meta Total de Dinero ($)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                TextField(value = fecha, onValueChange = onFecha, label = { Text("Fecha Límite (DD/MM/AAAA)") }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(onClick = { val n = num.toIntOrNull() ?: 0; if (nombre.isNotBlank() && n > 0) { personas.clear(); repeat(n) { personas.add("") }; navigate("agregar_personas") } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = lightBlue), shape = RoundedCornerShape(25.dp)) { Text("Continuar") }
}

@Composable
private fun AgregarPersonasScreen(nombre: String, personas: MutableList<String>, state: GruposUiState, darkBlue: Color, lightBlue: Color, navigate: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navigate("crear_grupo") }) { Icon(Icons.Default.ArrowBack, null) }
            Text(text = "Participantes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
        }
        Spacer(modifier = Modifier.height(20.dp))
        personas.forEachIndexed { index, p ->
            TextField(value = p, onValueChange = { personas[index] = it }, label = { Text("Persona ${index+1}") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { if (personas.all { it.isNotBlank() }) navigate("agregar_gasto_inicial") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = lightBlue)) { Text("Siguiente") }
    }
}

@Composable
private fun AgregarGastoInicialScreen(nombre: String, personas: List<String>, monto: String, onMonto: (String) -> Unit, desc: String, onDesc: (String) -> Unit, ahorro: Boolean, meta: String, fecha: String, state: GruposUiState, vm: GruposViewModel, darkBlue: Color, lightBlue: Color, navigate: (String) -> Unit, onRuleta: (List<String>, Int, List<String>) -> Unit) {
    var creado by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navigate("agregar_personas") }) { Icon(Icons.Default.ArrowBack, null) }
            Text("Confirmar Grupo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkBlue)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Grupo: $nombre", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(16.dp))
                if (ahorro) { 
                    Text("Meta: $$meta", fontWeight = FontWeight.Medium)
                    Text("Fecha: $fecha", fontWeight = FontWeight.Medium) 
                } else {
                    Text("Configura el gasto inicial que se dividirá entre todos:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = monto,
                        onValueChange = onMonto,
                        label = { Text("Monto total que te deben ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = desc,
                        onValueChange = onDesc,
                        label = { Text("Descripción (ej. Comida, Cine)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (monto.isNotBlank()) {
                        val m = monto.toDoubleOrNull() ?: 0.0
                        if (m > 0) {
                            val individual = m / personas.size
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "A cada uno le toca pagar: $${String.format(Locale.getDefault(), "%.2f", individual)}",
                                color = lightBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { vm.crearGrupo(nombre, personas, null, null, ahorro, meta.toDoubleOrNull() ?: 0.0, fecha) }, 
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
            shape = RoundedCornerShape(25.dp)
        ) { Text("¡CREAR GRUPO!", fontWeight = FontWeight.Bold) }
    }
    LaunchedEffect(state.grupoActual) {
        if (state.grupoActual != null && state.grupoActual!!.nombre == nombre && !creado) {
            creado = true
            if (!ahorro && monto.isNotBlank()) {
                val individual = (monto.toDoubleOrNull() ?: 0.0) / personas.size
                personas.forEach { vm.agregarGasto(it, individual, desc, "te_deben") }
            }
            if (!ahorro) onRuleta(personas, state.grupoActual!!.id, emptyList()) else navigate("inicio")
        }
    }
}

@Composable
private fun GastosDetailScreen(
    state: GruposUiState, 
    vm: GruposViewModel, 
    montoTeDeben: String, onMontoTeDeben: (String) -> Unit,
    descTeDeben: String, onDescTeDeben: (String) -> Unit,
    montoTuDebes: String, onMontoTuDebes: (String) -> Unit,
    descTuDebes: String, onDescTuDebes: (String) -> Unit,
    personaTuDebes: String, onPersonaTuDebes: (String) -> Unit,
    expandedTuDebes: Boolean, onExpandedTuDebes: (Boolean) -> Unit,
    darkBlue: Color, lightBlue: Color, purple: Color, navigate: (String) -> Unit
) {
    val grupo = state.grupoActual ?: run { navigate("inicio"); return }
    var mostrandoDialogoAbono by remember { mutableStateOf(false) }
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }
    
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            Text(text = grupo.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = { navigate("editar_grupo") }) { Icon(Icons.Default.Edit, contentDescription = null, tint = darkBlue) }
        }
        
        if (grupo.isAhorro) {
            AhorroView(grupo, vm, { mostrandoDialogoAbono = true })
        } else {
            if (tipoSeleccionado == null) {
                // Pantalla principal del grupo: Lista de personas y Botones
                NormalGroupView(grupo, vm, lightBlue, darkBlue, purple) { tipoSeleccionado = it }
            } else if (tipoSeleccionado == "te_deben") {
                TeDebenSection(grupo, state, vm, montoTeDeben, onMontoTeDeben, descTeDeben, onDescTeDeben, darkBlue, lightBlue) { tipoSeleccionado = null }
            } else {
                TuDebesSection(grupo, state, vm, montoTuDebes, onMontoTuDebes, descTuDebes, onDescTuDebes, personaTuDebes, onPersonaTuDebes, expandedTuDebes, onExpandedTuDebes, darkBlue, purple) { tipoSeleccionado = null }
            }
        }
    }

    if (mostrandoDialogoAbono) {
        AbonoDialog(grupo, vm) { mostrandoDialogoAbono = false }
    }
}

@Composable
private fun NormalGroupView(grupo: Grupo, vm: GruposViewModel, lightBlue: Color, darkBlue: Color, purple: Color, onTipoSeleccionado: (String) -> Unit) {
    Column {
        Text(text = "Resumen del grupo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))
        
        grupo.personas.forEach { persona ->
            val teDebe = grupo.gastos.filter { it.persona == persona && it.tipo == "te_deben" }.sumOf { it.monto }
            val leDebes = grupo.gastos.filter { it.persona == persona && it.tipo == "tu_debes" }.sumOf { it.monto }
            val balance = teDebe - leDebes
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = persona, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Text(
                        text = when {
                            balance > 0 -> "Te debe: $${String.format(Locale.getDefault(), "%.2f", balance)}"
                            balance < 0 -> "Le debes: $${String.format(Locale.getDefault(), "%.2f", -balance)}"
                            else -> "Al día"
                        },
                        color = when {
                            balance > 0 -> lightBlue
                            balance < 0 -> purple
                            else -> Color.Gray
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onTipoSeleccionado("te_deben") },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Me deben", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onTipoSeleccionado("tu_debes") },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Tú debes", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (grupo.gastos.isNotEmpty()) {
            Text(text = "Historial de gastos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))
            
            var gastoEditandoId by remember { mutableStateOf<Int?>(null) }
            var montoEditando by remember { mutableStateOf("") }
            
            grupo.gastos.forEach { gasto ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = if (gasto.tipo == "te_deben") lightBlue.copy(alpha = 0.1f) else purple.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (gastoEditandoId == gasto.id) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = if (gasto.tipo == "te_deben") "${gasto.persona} te debe" else "Debes a ${gasto.persona}", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextField(value = montoEditando, onValueChange = { montoEditando = it }, label = { Text("Monto") }, modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { 
                                    val nm = montoEditando.toDoubleOrNull()
                                    if (nm != null && nm > 0) { vm.editarGasto(gasto.id, nm); gastoEditandoId = null; montoEditando = "" }
                                }) { Text("OK") }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = if (gasto.tipo == "te_deben") "${gasto.persona} te debe" else "Debes a ${gasto.persona}", fontWeight = FontWeight.Bold)
                                Text(text = "$${gasto.monto}", color = if (gasto.tipo == "te_deben") darkBlue else purple)
                                if (gasto.descripcion.isNotBlank()) { Text(text = gasto.descripcion, color = Color.Gray, fontSize = 12.sp) }
                            }
                            IconButton(onClick = { gastoEditandoId = gasto.id; montoEditando = gasto.monto.toString() }) { Icon(Icons.Default.Edit, null) }
                            IconButton(onClick = { vm.eliminarGasto(gasto.id) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeDebenSection(grupo: Grupo, state: GruposUiState, vm: GruposViewModel, monto: String, onMonto: (String) -> Unit, desc: String, onDesc: (String) -> Unit, darkBlue: Color, lightBlue: Color, onBack: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Text(text = "Me deben", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = lightBlue)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Se dividirá entre los ${grupo.personas.size} participantes:", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                grupo.personas.forEach { Text(it, fontSize = 14.sp, color = darkBlue) }
                Spacer(modifier = Modifier.height(20.dp))
                TextField(value = monto, onValueChange = onMonto, label = { Text("Monto total que pagaste") }, modifier = Modifier.fillMaxWidth())
                if (monto.isNotBlank()) {
                    val mt = monto.toDoubleOrNull() ?: 0.0
                    val mp = mt / grupo.personas.size
                    Text(text = "A cada uno le toca: $${String.format(Locale.getDefault(), "%.2f", mp)}", color = lightBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = desc, onValueChange = onDesc, label = { Text("¿Por qué concepto?") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { 
                    val mt = monto.toDoubleOrNull() ?: 0.0
                    if (mt > 0) {
                        val mp = mt / grupo.personas.size
                        grupo.personas.forEach { vm.agregarGasto(it, mp, desc, "te_deben") }
                        onMonto(""); onDesc(""); onBack()
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = lightBlue)) { Text("Registrar Gasto") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TuDebesSection(grupo: Grupo, state: GruposUiState, vm: GruposViewModel, monto: String, onMonto: (String) -> Unit, desc: String, onDesc: (String) -> Unit, persona: String, onPersona: (String) -> Unit, expanded: Boolean, onExpanded: (Boolean) -> Unit, darkBlue: Color, purple: Color, onBack: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Text(text = "Tú debes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = purple)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onExpanded(!expanded) }) {
                    TextField(value = persona, onValueChange = {}, readOnly = true, label = { Text("¿A quién le debes?") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpanded(false) }) {
                        grupo.personas.forEach { p -> DropdownMenuItem(text = { Text(p) }, onClick = { onPersona(p); onExpanded(false) }) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = monto, onValueChange = onMonto, label = { Text("Monto que debes") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = desc, onValueChange = onDesc, label = { Text("¿Por qué?") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { 
                    val m = monto.toDoubleOrNull() ?: 0.0
                    if (m > 0 && persona.isNotBlank()) {
                        vm.agregarGasto(persona, m, desc, "tu_debes")
                        onMonto(""); onDesc(""); onPersona(""); onBack()
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = purple)) { Text("Registrar Deuda") }
            }
        }
    }
}

@Composable
private fun AhorroView(grupo: Grupo, vm: GruposViewModel, onAbonar: () -> Unit) {
    Column {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Progreso de la Meta", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Text("$${grupo.montoAcumulado} / $${grupo.metaAhorro}", fontSize = 24.sp, fontWeight = FontWeight.Black)
                LinearProgressIndicator(progress = { grupo.progreso }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape).padding(vertical = 8.dp), color = Color(0xFF4CAF50))
                Text("Fecha límite: ${grupo.fechaLimite ?: "No definida"}", fontSize = 12.sp)
                
                if (grupo.progreso >= 1f) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { vm.finalizarMeta(grupo.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F))) {
                        Icon(Icons.Default.Star, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("¡META OBTENIDA!", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onAbonar, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))) {
            Text("ABONAR A LA META")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Historial de Abonos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        grupo.gastos.filter { it.tipo == "abono" }.forEach { abono ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(abono.persona, fontWeight = FontWeight.Bold)
                        Text("Abonó: $${abono.monto}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        Text(abono.fecha, fontSize = 11.sp, color = Color.Gray)
                    }
                    if (abono.comprobanteUri != null) {
                        AsyncImage(model = abono.comprobanteUri, contentDescription = null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AbonoDialog(grupo: Grupo, vm: GruposViewModel, onDismiss: () -> Unit) {
    var monto by remember { mutableStateOf("") }
    var persona by remember { mutableStateOf(grupo.personas.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Abono") },
        text = {
            Column {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    TextField(value = persona, onValueChange = {}, readOnly = true, label = { Text("¿Quién abona?") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        grupo.personas.forEach { p -> DropdownMenuItem(text = { Text(p) }, onClick = { persona = p; expanded = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto del abono ($)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { 
                val m = monto.toDoubleOrNull() ?: 0.0
                if (m > 0) { vm.agregarGasto(persona, m, "Abono a meta", "abono", null); onDismiss() }
            }) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditarGrupoScreen(state: GruposUiState, vm: GruposViewModel, nueva: String, onNueva: (String) -> Unit, darkBlue: Color, lightBlue: Color, navigate: (String) -> Unit) {
    val grupo = state.grupoActual ?: run { navigate("inicio"); return }
    Column {
        Text("Editar ${grupo.nombre}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Participantes:", fontWeight = FontWeight.Bold)
        grupo.personas.forEach { p ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(p, modifier = Modifier.weight(1f))
                IconButton(onClick = { vm.eliminarPersona(p) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = nueva, onValueChange = onNueva, label = { Text("Nueva persona") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { if (nueva.isNotBlank()) { vm.agregarPersona(nueva); onNueva("") } }, modifier = Modifier.padding(top = 8.dp)) { Text("Agregar Persona") }
        
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { vm.eliminarGrupo(grupo.id); navigate("inicio") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.fillMaxWidth()) { Text("Eliminar Grupo") }
        Spacer(modifier = Modifier.height(20.dp))
        IconButton(onClick = { navigate("inicio") }) { Icon(Icons.Default.ArrowBack, null) }
    }
}
