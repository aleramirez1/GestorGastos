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
fun GastosScreen(factory: GastosViewModelFactory) {
    val vm: GastosViewModel = viewModel(factory = factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf("inicio") }
    var nombreGrupo by remember { mutableStateOf("") }
    var numPersonas by remember { mutableStateOf("") }
    val personasInput = remember { mutableStateListOf<String>() }

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
            Spacer(modifier = Modifier.height(60.dp))

            when (currentScreen) {
                "inicio" -> {
                    Text(
                        text = "Mis Grupos",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
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
                                    Column {
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
                                    IconButton(onClick = { vm.eliminarGrupo(grupo.id) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { currentScreen = "crear_grupo" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
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

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = lightBlue
                        )
                    } else {
                        Button(
                            onClick = {
                                val validPersonas = personasInput.filter { it.isNotBlank() }
                                if (validPersonas.isNotEmpty()) {
                                    vm.crearGrupo(nombreGrupo, validPersonas)
                                    currentScreen = "gastos"
                                    nombreGrupo = ""
                                    numPersonas = ""
                                    personasInput.clear()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
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
                            color = darkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }

                    if (tipoSeleccionado == null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable { tipoSeleccionado = "te_deben" },
                            colors = CardDefaults.cardColors(containerColor = lightBlue),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Te deben",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable { tipoSeleccionado = "tu_debes" },
                            colors = CardDefaults.cardColors(containerColor = purple),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tu debes",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))

                        if (grupo.gastos.isNotEmpty()) {
                            Text(
                                text = "Gastos registrados",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = darkBlue
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            grupo.gastos.forEach { gasto ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (gasto.tipo == "te_deben") lightBlue.copy(alpha = 0.2f) else purple.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = if (gasto.tipo == "te_deben") "${gasto.persona} te debe" else "Debes a ${gasto.persona}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(text = "$${gasto.monto}")
                                            if (gasto.descripcion.isNotBlank()) {
                                                Text(text = gasto.descripcion, color = Color.Gray)
                                            }
                                        }
                                        IconButton(onClick = { vm.eliminarGasto(gasto.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null)
                                        }
                                    }
                                }
                            }
                        }

                    } else if (tipoSeleccionado == "te_deben") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { tipoSeleccionado = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                            Text(
                                text = "Te deben",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = lightBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "${grupo.personas.size} personas",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                grupo.personas.forEach { persona ->
                                    Text(
                                        text = "• $persona",
                                        fontSize = 16.sp,
                                        color = darkBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                TextField(
                                    value = montoTeDeben,
                                    onValueChange = { montoTeDeben = it },
                                    label = { Text("Monto total") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (montoTeDeben.isNotBlank()) {
                                    val montoTotal = montoTeDeben.toDoubleOrNull() ?: 0.0
                                    val montoPorPersona = montoTotal / grupo.personas.size
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Cada uno te debe: ${String.format("%.2f", montoPorPersona)}",
                                        color = lightBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                TextField(
                                    value = descripcionTeDeben,
                                    onValueChange = { descripcionTeDeben = it },
                                    label = { Text("Por que") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        color = lightBlue
                                    )
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
                            Text(
                                text = "Tu debes",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = purple
                            )
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
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTuDebes)
                                        },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFFF5F5F5),
                                            unfocusedContainerColor = Color(0xFFF5F5F5)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedTuDebes,
                                        onDismissRequest = { expandedTuDebes = false }
                                    ) {
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
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                TextField(
                                    value = descripcionTuDebes,
                                    onValueChange = { descripcionTuDebes = it },
                                    label = { Text("Por que") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        color = purple
                                    )
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
