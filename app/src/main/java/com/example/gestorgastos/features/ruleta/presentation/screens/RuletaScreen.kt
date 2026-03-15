package com.example.gestorgastos.features.ruleta.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate as drawScopeRotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestorgastos.R
import com.example.gestorgastos.features.ruleta.presentation.viewmodels.RuletaViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuletaScreen(
    participantes: List<String>,
    onBack: () -> Unit,
    onGanadorSeleccionado: (String) -> Unit,
    viewModel: RuletaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(participantes) {
        viewModel.setParticipantes(participantes)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ruleta_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.comun_volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5C6BC0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.participantes.isEmpty()) {
                Text(stringResource(R.string.ruleta_no_participantes), fontSize = 18.sp)
            } else {
                Box(
                    modifier = Modifier.size(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .rotate(state.currentRotation),
                        contentAlignment = Alignment.Center
                    ) {
                        RuletaWheel(participantes = state.participantes)
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(y = (-160).dp)
                            .background(Color(0xFFFF5252), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▼", fontSize = 24.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (state.ganador != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.ruleta_ganador_label),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                state.ganador!!,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(R.string.ruleta_ganador_mensaje),
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            onGanadorSeleccionado(state.ganador!!)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                    ) {
                        Text(stringResource(R.string.ruleta_btn_confirmar), fontSize = 18.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.girarRuleta() },
                        enabled = !state.isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                    ) {
                        Text(
                            if (state.isSpinning) stringResource(R.string.ruleta_btn_girando) else stringResource(R.string.ruleta_btn_girar),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RuletaWheel(participantes: List<String>) {
    val colors = listOf(
        Color(0xFFE57373),
        Color(0xFF64B5F6),
        Color(0xFF81C784),
        Color(0xFFFFD54F),
        Color(0xFFBA68C8),
        Color(0xFFFF8A65),
        Color(0xFF4DD0E1),
        Color(0xFFAED581)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        val anglePerSegment = 360f / participantes.size

        participantes.forEachIndexed { index, nombre ->
            val startAngle = index * anglePerSegment - 90f
            val color = colors[index % colors.size]

            val path = Path().apply {
                moveTo(center.x, center.y)
                val startRad = Math.toRadians(startAngle.toDouble())
                val endRad = Math.toRadians((startAngle + anglePerSegment).toDouble())

                lineTo(
                    center.x + (radius * cos(startRad)).toFloat(),
                    center.y + (radius * sin(startRad)).toFloat()
                )

                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        center.x - radius,
                        center.y - radius,
                        center.x + radius,
                        center.y + radius
                    ),
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = anglePerSegment,
                    forceMoveTo = false
                )

                close()
            }

            drawPath(path, color)
            drawPath(path, Color.White, style = Stroke(width = 4f))
            
            val textAngle = startAngle + anglePerSegment / 2
            val textRadius = radius * 0.65f
            val textX = center.x + textRadius * cos(Math.toRadians(textAngle.toDouble())).toFloat()
            val textY = center.y + textRadius * sin(Math.toRadians(textAngle.toDouble())).toFloat()
            
            drawContext.canvas.nativeCanvas.apply {
                save()
                translate(textX, textY)
                rotate(textAngle + 90f)
                
                val paint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 40f
                    this.color = android.graphics.Color.WHITE
                    isFakeBoldText = true
                    setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                }
                
                drawText(nombre, 0f, 0f, paint)
                restore()
            }
        }

        drawCircle(
            color = Color.White,
            radius = 30f,
            center = center
        )
        drawCircle(
            color = Color(0xFF5C6BC0),
            radius = 25f,
            center = center
        )
    }
}
