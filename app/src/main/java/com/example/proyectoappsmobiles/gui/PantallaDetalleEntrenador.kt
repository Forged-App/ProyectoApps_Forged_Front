package com.example.proyectoappsmobiles.gui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoappsmobiles.R
import com.example.proyectoappsmobiles.navigation.Pantallas
import com.example.proyectoappsmobiles.ui.theme.Naranja
import com.example.proyectoappsmobiles.viewmodel.AppViewModel


private data class EntrenadorInfo(
    val id: Int,
    val nombre: String,
    val rol: String,
    val rating: String,
    val sobreMi: String,
    val especialidades: String,
    @DrawableRes val imagen: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaDetalleEntrenador(
    navController: NavController,
    entrenadorId: Int,
    viewModel: AppViewModel = viewModel()
) {

    val entrenadoresApi by viewModel.entrenadores.collectAsState()
    val entrenador = entrenadoresApi
        .find { it.id == entrenadorId }
        ?.let {
            EntrenadorInfo(
                id = it.id,
                nombre = it.nombre,
                rol = it.rol,
                rating = "${it.calificacion} (${it.resenas} reseñas)",
                sobreMi = it.sobreMi,
                especialidades = it.especialidades,
                imagen = imagenEntrenador(it.nombre)
            )
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETALLES DEL ENTRENADOR", color = Color.White, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Naranja)
            )
        }
    ) { padding ->
        if (entrenador == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Entrenador no encontrado")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = entrenador.imagen),
                contentDescription = "Foto de ${entrenador.nombre}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(entrenador.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(entrenador.rol, color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "⭐ ${entrenador.rating}",
                color = Naranja,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            val listaEspecialidades = entrenador.especialidades
                .split(",")
                .map { it.trim() }

            FlowRow {
                listaEspecialidades.forEach { especialidad ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Naranja),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = especialidad,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Sobre mí", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                entrenador.sobreMi,
                color = Color.DarkGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            BotonNaranja(
                texto = "Ver disponibilidad",
                onClick = {
                    // Guardamos el entrenador elegido para que la API de disponibilidad
                    // devuelva únicamente sus franjas horarias.
                    viewModel.entrenadorSeleccionadoId = entrenador.id
                    viewModel.entrenadorSeleccionadoNombre = entrenador.nombre
                    viewModel.reservaSeleccionada = viewModel.reservaSeleccionada?.copy(
                        idClase = null,
                        titulo = "Entrenamiento Personal",
                        entrenador = entrenador.nombre
                    )
                    navController.navigate(Pantallas.DISPONIBILIDAD)
                }
            )
        }
    }
}