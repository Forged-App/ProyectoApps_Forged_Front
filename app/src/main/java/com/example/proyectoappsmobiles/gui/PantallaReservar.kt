package com.example.proyectoappsmobiles.gui


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.proyectoappsmobiles.R
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun PantallaReservar(navController: NavController, viewModel: AppViewModel = viewModel()) {
    var tab by remember { mutableIntStateOf(0) }
    var diaSeleccionado by remember { mutableStateOf("18") }
    val tabs = listOf("Grupales", "Personal", "Mis reservas")

    val clases by viewModel.clases.collectAsState()
    val entrenadores by viewModel.entrenadores.collectAsState()

    Scaffold(
        bottomBar = { MenuInferior(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("RESERVAR", fontSize = 22.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(12.dp))
            TiraDias(diaSeleccionado = diaSeleccionado, onSeleccion = { diaSeleccionado = it })

            Spacer(modifier = Modifier.height(16.dp))
            PestañasSecundarias(tabs = tabs, seleccionado = tab, onSeleccion = { tab = it })
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (tab) {
                    0 -> {
                        if (clases.isEmpty()) {
                            Text(
                                "No hay clases disponibles. Verifica la conexión con la API.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        clases.forEach { clase ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = imagenClase(clase.nombre)),
                                        contentDescription = clase.nombre,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(clase.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(clase.horario, color = Color.Gray, fontSize = 12.sp)
                                        Text(clase.entrenador, color = Color.Gray, fontSize = 12.sp)
                                        Text(
                                            "${clase.cuposOcupados}/${clase.cuposTotal} cupos",
                                            color = Naranja,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            navController.navigate("${Pantallas.DETALLE_CLASE}/${clase.id}")
                                        },
                                        enabled = clase.cuposOcupados < clase.cuposTotal,
                                        colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            if (clase.cuposOcupados < clase.cuposTotal) "Reservar" else "Llena",
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        if (entrenadores.isEmpty()) {
                            Text(
                                "No hay entrenadores disponibles. Verifica la conexión con la API.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        entrenadores.forEach { entrenador ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = imagenEntrenador(entrenador.nombre)),
                                        contentDescription = entrenador.nombre,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(entrenador.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(entrenador.rol, color = Color.Gray, fontSize = 12.sp)
                                        Text(
                                            "⭐ ${entrenador.calificacion} (${entrenador.resenas} reseñas)",
                                            color = Naranja,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            navController.navigate("${Pantallas.DETALLE_ENTRENADOR}/${entrenador.id}")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Reservar", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        MisClasesContenido(navController, viewModel, conScroll = false)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@DrawableRes
private fun imagenClase(nombre: String): Int = when {
    nombre.contains("spinning", ignoreCase = true) -> R.drawable.spinning
    nombre.contains("yoga", ignoreCase = true) -> R.drawable.yoga
    else -> R.drawable.funcional
}

@DrawableRes
internal fun imagenEntrenador(nombre: String): Int = when {
    nombre.contains("laura", ignoreCase = true) -> R.drawable.entrenadora
    nombre.contains("carlos", ignoreCase = true) -> R.drawable.entrenador11
    else -> R.drawable.entrenador2
}

@Composable
fun PestañasSecundarias(
    tabs: List<String>,
    seleccionado: Int,
    onSeleccion: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEEEEEE))
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { indice, tab ->
            val activo = indice == seleccionado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activo) Naranja else Color.Transparent)
                    .clickable { onSeleccion(indice) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    tab,
                    color = if (activo) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
