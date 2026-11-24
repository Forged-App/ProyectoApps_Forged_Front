package com.example.proyectoappsmobiles.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoappsmobiles.navigation.Pantallas
import com.example.proyectoappsmobiles.ui.theme.AmarilloGradiente
import com.example.proyectoappsmobiles.ui.theme.Cian
import com.example.proyectoappsmobiles.ui.theme.Naranja
import com.example.proyectoappsmobiles.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PantallaRenovar(
    navController: NavController,
    viewModel: AppViewModel = viewModel()
) {
    val membresia by viewModel.membresia.collectAsState(initial = null)
    val pagos by viewModel.pagos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMembresia()
        viewModel.cargarPagos()
    }

    // La fecha sale de la membresía actual en la API; si todavía no existe,
    // se usa el último pago registrado.
    val fechaVencimiento = membresia?.fechaVencimiento?.takeIf { it.isNotBlank() }
        ?: pagos.lastOrNull()?.fechaVencimiento
    val dias = diasParaVencer(fechaVencimiento)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("RENOVAR", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Cian.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Cian.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        null,
                        tint = AmarilloGradiente,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Tu membresía vence en", color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                when {
                    dias == null -> "Sin membresía"
                    dias == 1L -> "1 Día"
                    else -> "$dias Días"
                },
                color = Naranja,
                fontSize = if (dias == null) 32.sp else 56.sp,
                fontWeight = FontWeight.Black
            )
            if (fechaVencimiento != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Vence: $fechaVencimiento", color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "No pierdas el acceso a tus beneficios",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            BotonNaranja(
                texto = "Renovar ahora",
                onClick = { navController.navigate(Pantallas.PAGO) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            BotonContorno(
                texto = "Más tarde",
                onClick = {
                    navController.navigate(Pantallas.PAGOS) {
                        popUpTo(Pantallas.INICIO)
                    }
                }
            )
        }
    }
}

/** Días que faltan desde hoy hasta la fecha "dd MMM yyyy" de la API. */
private fun diasParaVencer(fecha: String?): Long? {
    if (fecha.isNullOrBlank()) return null
    return try {
        val formato = SimpleDateFormat("dd MMM yyyy", Locale("es", "CO"))
        val vencimiento = formato.parse(fecha) ?: return null
        val hoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val fin = Calendar.getInstance().apply {
            time = vencimiento
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        ((fin.timeInMillis - hoy.timeInMillis) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    } catch (e: Exception) {
        null
    }
}
