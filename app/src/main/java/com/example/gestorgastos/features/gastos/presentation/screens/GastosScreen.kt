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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var quienPago by remember { mutableStateOf("") }
    var teDeben by remember { mutableStateOf(true) }
    var personaExpanded by remember { mutableStateOf(false) }

    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF5C6BC0)
    val purple = Color(0xFF7E57C2)
    val bgColor = Color(0xFFE3F2FD)

    val personas = listOf("Juan", "Maria", "Pedro", "Ana", "Carlos")

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

            if (selectedCategory == null) {
                Text(
                    text = "Gestor de Gastos",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )

                Text(
                    text = "Selecciona una categoria",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(40.dp))

                CategoryCard(
                    title = "Companeros",
                    icon = Icons.Default.Groups,
                    color = lightBlue,
                    onClick = { selectedCategory = "Companeros" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CategoryCard(
                    title = "Trabajo",
                    icon = Icons.Default.Work,
                    color = purple,
                    onClick = { selectedCategory = "Trabajo" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CategoryCard(
                    title = "Familia",
                    icon = Icons.Default.Home,
                    color = darkBlue,
                    onClick = { selectedCategory = "Familia" }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (state.resumen != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resumen",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Total: $${state.resumen!!.totalGastado}")
                            Text("Por persona: $${state.resumen!!.montoPorPersona}")
                        }
                    }
                }

            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedCategory = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        text = selectedCategory!!,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Nuevo Gasto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = darkBlue
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { teDeben = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (teDeben) lightBlue else Color.LightGray
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Te deben")
                            }
                            Button(
                                onClick = { teDeben = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!teDeben) purple else Color.LightGray
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Tu debes")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        ExposedDropdownMenuBox(
                            expanded = personaExpanded,
                            onExpandedChange = { personaExpanded = !personaExpanded }
                        ) {
                            TextField(
                                value = quienPago,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("A quien") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = personaExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = personaExpanded,
                                onDismissRequest = { personaExpanded = false }
                            ) {
                                personas.forEach { persona ->
                                    DropdownMenuItem(
                                        text = { Text(persona) },
                                        onClick = {
                                            quienPago = persona
                                            personaExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = monto,
                            onValueChange = { monto = it },
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
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("De que se gasto") },
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
                                    val m = monto.toDoubleOrNull() ?: 0.0
                                    val desc = "$selectedCategory - $descripcion"
                                    vm.crearGasto(m, desc, quienPago)
                                    monto = ""
                                    descripcion = ""
                                    quienPago = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text("Registrar Gasto", fontSize = 16.sp)
                            }
                        }

                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(state.error ?: "", color = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (state.resumen != null && state.resumen!!.deudas.isNotEmpty()) {
                    Text(
                        text = "Deudas en $selectedCategory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = darkBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    state.resumen!!.deudas.forEach { deuda ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = deuda.descripcion,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
