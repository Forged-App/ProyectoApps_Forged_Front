package com.example.proyectoappsmobiles.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoappsmobiles.navigation.Pantallas
import com.example.proyectoappsmobiles.ui.theme.Naranja
import com.example.proyectoappsmobiles.viewmodel.AppViewModel

@Composable
fun PantallaRutina(
    navController: NavController,
    viewModel: AppViewModel = viewModel()
) {
    var diaSeleccionado by remember { mutableStateOf("18") }
    val rutinas by viewModel.rutinas.collectAsState()
    val rutinaDestacada = rutinas.firstOrNull()

    LaunchedEffect(Unit) {
        viewModel.cargarRutinas()
    }

    Scaffold(
        bottomBar = { MenuInferior(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("MI RUTINA", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(12.dp))
            TiraDias(diaSeleccionado = diaSeleccionado, onSeleccion = { diaSeleccionado = it })
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            rutinaDestacada?.let {
                                viewModel.rutinaSeleccionadaId = it.id
                                navController.navigate(Pantallas.RUTINA_HOY)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Naranja, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FitnessCenter, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                rutinaDestacada?.nombre ?: "Rutina de hoy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (rutinaDestacada != null) {
                                Text(
                                    "${rutinaDestacada.tipo} - Nivel ${rutinaDestacada.nivel}",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "${rutinaDestacada.duracionMin} min - ${rutinaDestacada.numeroEjercicios} ejercicios",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            } else {
                                Text(
                                    "Sin rutinas disponibles en la API",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (rutinas.isEmpty()) {
                    Text(
                        "No se encontraron rutinas. Verifica que la API esté corriendo.",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }

                rutinas.forEach { rutina ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                viewModel.rutinaSeleccionadaId = rutina.id
                                navController.navigate(Pantallas.RUTINA_HOY)
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF546E7A), Color(0xFF37474F))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    rutina.tipo.ifBlank { "Rutina" },
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rutina.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(rutina.detalle, color = Color.Gray, fontSize = 13.sp)
                                Text(
                                    "${rutina.duracionMin} min - ${rutina.numeroEjercicios} ejercicios",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                                if (rutina.subtitulo.isNotEmpty()) {
                                    Text(
                                        rutina.subtitulo,
                                        color = Naranja,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(">", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
