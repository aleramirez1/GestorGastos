package com.example.gestorgastos.features.gastos.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestorgastos.features.gastos.presentation.viewmodels.GastosViewModel
import com.example.gestorgastos.features.gastos.presentation.viewmodels.GastosViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(key: Int = 0, factory: GastosViewModelFactory, onLogout: () -> Unit) {
    val vm: GastosViewModel = viewModel(key = "gastos_$key", factory = factory)
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

    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .offset(x = (-40).dp, y = (-40).dp)
        ) {
            drawCircle(color = lightBlue)
        }

        Canvas(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 320.dp, y = 20.dp)
        ) {
            drawCircle(color = darkBlue)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            when (currentScreen) {
                "inicio" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = darkBlue)
                        }
                        Text(
                            text = "Mis Grupos",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(color = lightBlue)
                    } else if (state.grupos.isEmpty()) {
                        Text(
                            text = "No tienes grupos aun",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    } else {
                        state.grupos.forEach { grupo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        vm.seleccionarGrupo(grupo)
                                        currentScreen = "gastos"
                                    },
                                colors = CardDefaults.cardColors(containerColor = lightBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = grupo.nombre,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${grupo.personas.size} personas",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                    IconButton(onClick = {
                                        vm.seleccionarGrupo(grupo)
                                        currentScreen = "editar_grupo"
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                                    }
                                    IconButton(onClick = { vm.eliminarGrupo(grupo.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { currentScreen = "crear_grupo" },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generar grupo", fontSize = 16.sp)
                    }

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "", color = Color.Red)
                    }
                }

                "crear_grupo" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "inicio" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(
                            text = "Crear Grupo",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            TextField(
                                value = nombreGrupo,
                                onValueChange = { nombreGrupo = it },
                                label = { Text("Nombre del grupo") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            TextField(
                                value = numPersonas,
                                onValueChange = { numPersonas = it },
                                label = { Text("Cuantas personas") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            val num = numPersonas.toIntOrNull() ?: 0
                            if (nombreGrupo.isNotBlank() && num > 0) {
                                personasInput.clear()
                                repeat(num) { personasInput.add("") }
                                currentScreen = "agregar_personas"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Continuar", fontSize = 16.sp)
                    }
                }

                "agregar_personas" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "crear_grupo" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(
                            text = nombreGrupo,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Agregar Participantes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            personasInput.forEachIndexed { index, nombre ->
                                TextField(
                                    value = nombre,
                                    onValueChange = { personasInput[index] = it },
                                    label = { Text("Persona ${index + 1}") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            val validPersonas = personasInput.filter { it.isNotBlank() }
                            if (validPersonas.isNotEmpty()) {
                                currentScreen = "agregar_gasto_inicial"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Next", fontSize = 16.sp)
                    }

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "", color = Color.Red)
                    }
                }

                "agregar_gasto_inicial" -> {
                    val validPersonas = personasInput.filter { it.isNotBlank() }
                    var grupoCreado by remember { mutableStateOf(false) }

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
                            montoTeDeben = ""
                            descripcionTeDeben = ""
                            nombreGrupo = ""
                            numPersonas = ""
                            personasInput.clear()
                            currentScreen = "gastos"
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "agregar_personas" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(
                            text = nombreGrupo,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Agregar Gasto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = "${validPersonas.size} personas", fontSize = 16.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            validPersonas.forEach { persona ->
                                Text(text = "• $persona", fontSize = 16.sp, color = darkBlue)
                            }
                            Spacer(modifier = Modifier.height(20.dp))

                            TextField(
                                value = montoTeDeben,
                                onValueChange = { montoTeDeben = it },
                                label = { Text("Monto total") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (montoTeDeben.isNotBlank()) {
                                val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0
                                val montoPorPersona = montoTotal / validPersonas.size
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Cada uno te debe: ${String.format("%.2f", montoPorPersona)}", color = lightBlue, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            TextField(
                                value = descripcionTeDeben,
                                onValueChange = { descripcionTeDeben = it },
                                label = { Text("Concepto") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = lightBlue)
                    } else {
                        Button(
                            onClick = {
                                vm.crearGrupo(nombreGrupo, validPersonas)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text("Crear Grupo", fontSize = 16.sp)
                        }
                    }

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "", color = Color.Red)
                    }
                }

                "editar_grupo" -> {
                    val grupo = state.grupoActual
                    if (grupo == null) {
                        currentScreen = "inicio"
                        return@Column
                    }

                    var mostrarInputPersona by remember { mutableStateOf(false) }
                    var mostrarInputGasto by remember { mutableStateOf(false) }
                    var montoGasto by remember { mutableStateOf("") }
                    var descripcionGasto by remember { mutableStateOf("") }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "inicio" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(
                            text = "Editar ${grupo.nombre}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Personas del grupo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = darkBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    grupo.personas.forEach { persona ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = persona, fontSize = 16.sp)
                                IconButton(onClick = { vm.eliminarPersona(persona) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    if (!mostrarInputPersona) {
                        Button(
                            onClick = { mostrarInputPersona = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar persona", fontSize = 16.sp)
                        }
                    } else {
                        Text(
                            text = "Agregar persona",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = darkBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = nuevaPersona,
                                onValueChange = { nuevaPersona = it },
                                label = { Text("Nombre") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (nuevaPersona.isNotBlank()) {
                                        vm.agregarPersona(nuevaPersona)
                                        nuevaPersona = ""
                                        mostrarInputPersona = false
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = lightBlue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!mostrarInputGasto) {
                        Button(
                            onClick = { mostrarInputGasto = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = purple),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar gasto", fontSize = 16.sp)
                        }
                    } else {
                        Text(
                            text = "Agregar gasto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = darkBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(text = "${grupo.personas.size} personas", fontSize = 16.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))

                                grupo.personas.forEach { persona ->
                                    Text(text = "• $persona", fontSize = 16.sp, color = darkBlue)
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                TextField(
                                    value = montoGasto,
                                    onValueChange = { montoGasto = it },
                                    label = { Text("Monto total") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (montoGasto.isNotBlank()) {
                                    val montoTotal = montoGasto.toDoubleOrNull() ?: 0.0
                                    val montoPorPersona = montoTotal / grupo.personas.size
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Cada uno te debe: ${String.format("%.2f", montoPorPersona)}", color = lightBlue, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                TextField(
                                    value = descripcionGasto,
                                    onValueChange = { descripcionGasto = it },
                                    label = { Text("Concepto") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        val montoTotal = montoGasto.toDoubleOrNull() ?: 0.0
                                        if (montoTotal > 0) {
                                            val montoPorPersona = montoTotal / grupo.personas.size
                                            grupo.personas.forEach { persona ->
                                                vm.agregarGasto(persona, montoPorPersona, descripcionGasto, "te_deben")
                                            }
                                            montoGasto = ""
                                            descripcionGasto = ""
                                            mostrarInputGasto = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Text("Registrar", fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "", color = Color.Red)
                    }
                }

                "gastos" -> {
                    val grupo = state.grupoActual
                    if (grupo == null) {
                        currentScreen = "inicio"
                        return@Column
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "inicio" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(
                            text = grupo.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { currentScreen = "editar_grupo" }) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = darkBlue)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }

                    if (tipoSeleccionado == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(100.dp).clickable { tipoSeleccionado = "te_deben" },
                            colors = CardDefaults.cardColors(containerColor = lightBlue),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Te deben", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth().height(100.dp).clickable { tipoSeleccionado = "tu_debes" },
                            colors = CardDefaults.cardColors(containerColor = purple),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Tu debes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))

                        var gastoEditandoId by remember { mutableStateOf<Int?>(null) }
                        var montoEditando by remember { mutableStateOf("") }
                        var paginaActual by remember { mutableStateOf(0) }
                        val gastosPorPagina = 3
                        val totalPaginas = (grupo.gastos.size + gastosPorPagina - 1) / gastosPorPagina

                        if (grupo.gastos.isNotEmpty()) {
                            Text(text = "Gastos registrados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            val gastosEnPagina = grupo.gastos.drop(paginaActual * gastosPorPagina).take(gastosPorPagina)

                            gastosEnPagina.forEach { gasto ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (gasto.tipo == "te_deben") lightBlue.copy(alpha = 0.2f) else purple.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (gastoEditandoId == gasto.id) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                            Text(
                                                text = if (gasto.tipo == "te_deben") "${gasto.persona} te debe" else "Debes a ${gasto.persona}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                TextField(
                                                    value = montoEditando,
                                                    onValueChange = { montoEditando = it },
                                                    label = { Text("Monto") },
                                                    modifier = Modifier.weight(1f),
                                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = {
                                                        val nuevoMonto = montoEditando.toDoubleOrNull()
                                                        if (nuevoMonto != null && nuevoMonto > 0) {
                                                            vm.editarGasto(gasto.id, nuevoMonto)
                                                            gastoEditandoId = null
                                                            montoEditando = ""
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = lightBlue)
                                                ) {
                                                    Text("OK")
                                                }
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (gasto.tipo == "te_deben") "${gasto.persona} te debe" else "Debes a ${gasto.persona}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = "$${gasto.monto}")
                                                if (gasto.descripcion.isNotBlank()) {
                                                    Text(text = gasto.descripcion, color = Color.Gray)
                                                }
                                            }
                                            IconButton(onClick = {
                                                gastoEditandoId = gasto.id
                                                montoEditando = gasto.monto.toString()
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = null)
                                            }
                                            IconButton(onClick = { vm.eliminarGasto(gasto.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                            }

                            if (grupo.gastos.size > gastosPorPagina) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { if (paginaActual > 0) paginaActual-- },
                                        enabled = paginaActual > 0,
                                        colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                                    ) {
                                        Text("<")
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = "${paginaActual + 1} / $totalPaginas", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Button(
                                        onClick = { if (paginaActual < totalPaginas - 1) paginaActual++ },
                                        enabled = paginaActual < totalPaginas - 1,
                                        colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                                    ) {
                                        Text(">")
                                    }
                                }
                            }
                        }
                    } else if (tipoSeleccionado == "te_deben") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { tipoSeleccionado = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                            Text(text = "Te deben", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = lightBlue)
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(text = "${grupo.personas.size} personas", fontSize = 16.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))

                                grupo.personas.forEach { persona ->
                                    Text(text = "• $persona", fontSize = 16.sp, color = darkBlue)
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                TextField(
                                    value = montoTeDeben,
                                    onValueChange = { montoTeDeben = it },
                                    label = { Text("Monto total") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (montoTeDeben.isNotBlank()) {
                                    val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0
                                    val montoPorPersona = montoTotal / grupo.personas.size
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Cada uno te debe: ${String.format("%.2f", montoPorPersona)}", color = lightBlue, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                TextField(
                                    value = descripcionTeDeben,
                                    onValueChange = { descripcionTeDeben = it },
                                    label = { Text("Por que") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                if (state.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = lightBlue)
                                } else {
                                    Button(
                                        onClick = {
                                            val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0
                                            if (montoTotal > 0) {
                                                val montoPorPersona = montoTotal / grupo.personas.size
                                                grupo.personas.forEach { persona ->
                                                    vm.agregarGasto(persona, montoPorPersona, descripcionTeDeben, "te_deben")
                                                }
                                                montoTeDeben = ""
                                                descripcionTeDeben = ""
                                                tipoSeleccionado = null
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                                        shape = RoundedCornerShape(25.dp)
                                    ) {
                                        Text("Registrar", fontSize = 16.sp)
                                    }
                                }

                                if (state.error != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(state.error ?: "", color = Color.Red)
                                }
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { tipoSeleccionado = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                            Text(text = "Tu debes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = purple)
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedTuDebes,
                                    onExpandedChange = { expandedTuDebes = !expandedTuDebes }
                                ) {
                                    TextField(
                                        value = personaTuDebes,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("A quien le debes") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTuDebes) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    ExposedDropdownMenu(expanded = expandedTuDebes, onDismissRequest = { expandedTuDebes = false }) {
                                        grupo.personas.forEach { persona ->
                                            DropdownMenuItem(
                                                text = { Text(persona) },
                                                onClick = {
                                                    personaTuDebes = persona
                                                    expandedTuDebes = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                TextField(
                                    value = montoTuDebes,
                                    onValueChange = { montoTuDebes = it },
                                    label = { Text("Cuanto") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                TextField(
                                    value = descripcionTuDebes,
                                    onValueChange = { descripcionTuDebes = it },
                                    label = { Text("Por que") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5F5), unfocusedContainerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                if (state.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = purple)
                                } else {
                                    Button(
                                        onClick = {
                                            val m = montoTuDebes.toDoubleOrNull() ?: 0.0
                                            if (m > 0 && personaTuDebes.isNotBlank()) {
                                                vm.agregarGasto(personaTuDebes, m, descripcionTuDebes, "tu_debes")
                                                montoTuDebes = ""
                                                descripcionTuDebes = ""
                                                personaTuDebes = ""
                                                tipoSeleccionado = null
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = purple),
                                        shape = RoundedCornerShape(25.dp)
                                    ) {
                                        Text("Registrar", fontSize = 16.sp)
                                    }
                                }

                                if (state.error != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(state.error ?: "", color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
