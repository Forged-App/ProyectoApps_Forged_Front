package com.example.proyectoappsmobiles.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoappsmobiles.data.api.ApiProvider
import com.example.proyectoappsmobiles.data.api.DisponibilidadDto
import com.example.proyectoappsmobiles.data.api.EjercicioRutinaDto
import com.example.proyectoappsmobiles.data.api.LoginRequest
import com.example.proyectoappsmobiles.data.api.MembresiaRequest
import com.example.proyectoappsmobiles.data.api.PagoDto
import com.example.proyectoappsmobiles.data.api.PagoRequest
import com.example.proyectoappsmobiles.data.api.RegistroRequest
import com.example.proyectoappsmobiles.data.api.ReservaRequest
import com.example.proyectoappsmobiles.data.api.RutinaDto
import com.example.proyectoappsmobiles.data.api.UsuarioDto
import com.example.proyectoappsmobiles.data.entity.ClaseEntity
import com.example.proyectoappsmobiles.data.entity.EntrenadorEntity
import com.example.proyectoappsmobiles.data.entity.MembresiaEntity
import com.example.proyectoappsmobiles.data.entity.ReservaEntity
import com.example.proyectoappsmobiles.data.entity.UsuarioEntity
import com.example.proyectoappsmobiles.data.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * ViewModel de la app. Toda la información de negocio (clases, reservas,
 * membresía, entrenador, usuarios) se obtiene de la API Forged.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiProvider.api
    private val sessionManager = SessionManager(application)

    private val _clases = MutableStateFlow<List<ClaseEntity>>(emptyList())
    val clases: StateFlow<List<ClaseEntity>> = _clases

    private val _reservas = MutableStateFlow<List<ReservaEntity>>(emptyList())
    val reservas: StateFlow<List<ReservaEntity>> = _reservas

    private val _membresia = MutableStateFlow<MembresiaEntity?>(null)
    val membresia: StateFlow<MembresiaEntity?> = _membresia

    private val _entrenador = MutableStateFlow<EntrenadorEntity?>(null)
    val entrenador: StateFlow<EntrenadorEntity?> = _entrenador

    private val _entrenadores = MutableStateFlow<List<EntrenadorEntity>>(emptyList())
    val entrenadores: StateFlow<List<EntrenadorEntity>> = _entrenadores

    private val _disponibilidad = MutableStateFlow<List<DisponibilidadDto>>(emptyList())
    val disponibilidad: StateFlow<List<DisponibilidadDto>> = _disponibilidad

    private val _rutinas = MutableStateFlow<List<RutinaDto>>(emptyList())
    val rutinas: StateFlow<List<RutinaDto>> = _rutinas

    private val _ejercicios = MutableStateFlow<List<EjercicioRutinaDto>>(emptyList())
    val ejercicios: StateFlow<List<EjercicioRutinaDto>> = _ejercicios

    private val _pagos = MutableStateFlow<List<PagoDto>>(emptyList())
    val pagos: StateFlow<List<PagoDto>> = _pagos

    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    /** Último error de autenticación, para mostrar un mensaje claro en el login. */
    var mensajeError by mutableStateOf<String?>(null)
        private set

    /** Indica si ya se intentó recuperar la sesión guardada en SharedPreferences. */
    var sesionRestaurada by mutableStateOf(false)
        private set

    var reservaSeleccionada by mutableStateOf<ReservaEntity?>(null)

    /** Entrenador elegido en el detalle, usado por la pantalla de disponibilidad. */
    var entrenadorSeleccionadoId by mutableStateOf<Int?>(null)
    var entrenadorSeleccionadoNombre by mutableStateOf<String?>(null)

    /** Rutina elegida en la lista, usada por la pantalla de ejercicios. */
    var rutinaSeleccionadaId by mutableStateOf<Int?>(null)

    /** Último pago registrado en la API, usado por la confirmación de pago. */
    var ultimoPago by mutableStateOf<PagoDto?>(null)

    /** Plan elegido en la pantalla de planes, usado al registrar el pago. */
    var planSeleccionado by mutableStateOf("Plan premium")
    var montoSeleccionado by mutableStateOf("$25.000")

    init {
        viewModelScope.launch {
            val emailGuardado = sessionManager.obtenerEmailSesion()
            if (emailGuardado != null) {
                usuarioActual = try {
                    api.usuario(emailGuardado).toEntity()
                } catch (e: Exception) {
                    Log.e("API", "No se pudo restaurar la sesión del usuario", e)
                    null
                }
                if (usuarioActual == null) {
                    // El usuario ya no existe en la API o el servidor no responde.
                    sessionManager.cerrarSesion()
                }
            }
            sesionRestaurada = true
            cargarTodo()
        }
    }

    private fun cargarTodo() {
        cargarClases()
        cargarEntrenador()
        cargarEntrenadores()
        cargarMembresia()
        cargarReservas()
        cargarDisponibilidad()
        cargarRutinas()
        cargarPagos()
    }

    fun cargarClases() {
        viewModelScope.launch {
            try {
                _clases.value = api.clases()
            } catch (e: Exception) {
                Log.e("API", "No se pudieron cargar las clases", e)
            }
        }
    }

    fun cargarEntrenador() {
        viewModelScope.launch {
            try {
                _entrenador.value = api.entrenadorPrincipal()
            } catch (e: Exception) {
                Log.e("API", "No se pudo cargar el entrenador", e)
            }
        }
    }

    fun cargarEntrenadores() {
        viewModelScope.launch {
            try {
                _entrenadores.value = api.entrenadores()
            } catch (e: Exception) {
                Log.e("API", "No se pudieron cargar los entrenadores", e)
            }
        }
    }

    fun cargarMembresia() {
        viewModelScope.launch {
            try {
                // 204 = el usuario todavía no tiene membresía registrada.
                _membresia.value = api.membresiaActual(usuarioActual?.email).body()
            } catch (e: Exception) {
                Log.e("API", "No se pudo cargar la membresía", e)
            }
        }
    }

    fun cargarReservas() {
        viewModelScope.launch {
            try {
                _reservas.value = api.reservas(usuarioActual?.email)
            } catch (e: Exception) {
                Log.e("API", "No se pudieron cargar las reservas", e)
            }
        }
    }

    fun cargarDisponibilidad(idEntrenador: Int? = entrenadorSeleccionadoId, dia: String? = null) {
        viewModelScope.launch {
            try {
                _disponibilidad.value = api.disponibilidad(idEntrenador, dia)
            } catch (e: Exception) {
                Log.e("API", "No se pudo cargar la disponibilidad", e)
            }
        }
    }

    fun cargarRutinas() {
        viewModelScope.launch {
            try {
                _rutinas.value = api.rutinas()
            } catch (e: Exception) {
                Log.e("API", "No se pudieron cargar las rutinas", e)
            }
        }
    }

    fun cargarEjercicios(idRutina: Int? = rutinaSeleccionadaId) {
        viewModelScope.launch {
            try {
                _ejercicios.value = api.ejercicios(idRutina)
            } catch (e: Exception) {
                Log.e("API", "No se pudieron cargar los ejercicios", e)
            }
        }
    }

    fun cargarPagos() {
        viewModelScope.launch {
            try {
                _pagos.value = api.pagos(usuarioActual?.email)
            } catch (e: Exception) {
                Log.e("API", "No se pudieron cargar los pagos", e)
            }
        }
    }

    fun marcarEjercicio(id: Int, completado: Boolean, onResult: (Boolean) -> Unit = {}) {
        val ejercicio = _ejercicios.value.firstOrNull { it.id == id }
        if (ejercicio == null) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            try {
                api.actualizarEjercicio(id, ejercicio.copy(completado = completado))
                cargarEjercicios(ejercicio.idRutina)
                onResult(true)
            } catch (e: Exception) {
                Log.e("API", "No se pudo actualizar el ejercicio", e)
                onResult(false)
            }
        }
    }

    fun completarRutina(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val idRutina = _ejercicios.value.firstOrNull()?.idRutina ?: rutinaSeleccionadaId
                _ejercicios.value.filter { !it.completado }.forEach { ejercicio ->
                    api.actualizarEjercicio(ejercicio.id, ejercicio.copy(completado = true))
                }
                cargarEjercicios(idRutina)
                onResult(true)
            } catch (e: Exception) {
                Log.e("API", "No se pudo completar la rutina", e)
                onResult(false)
            }
        }
    }

    /**
     * Registra un pago real en la API y actualiza el historial.
     * Las fechas se calculan con el calendario del dispositivo.
     */
    fun registrarPago(
        plan: String,
        monto: String,
        metodoPago: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val formato = SimpleDateFormat("dd MMM yyyy", Locale("es", "CO"))
                val inicio = Calendar.getInstance()
                val vencimiento = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }

                val pago = api.crearPago(
                    PagoRequest(
                        plan = plan,
                        monto = monto,
                        fechaInicio = formato.format(inicio.time),
                        fechaVencimiento = formato.format(vencimiento.time),
                        metodoPago = metodoPago,
                        estado = "Pagado",
                        emailUsuario = usuarioActual?.email
                    )
                )
                ultimoPago = pago
                cargarPagos()
                // La membresía actual del usuario también queda guardada en la API.
                // Si falla, el pago ya quedó registrado y no se debe mostrar error.
                try {
                    api.crearMembresia(
                        MembresiaRequest(
                            tipoPlan = plan,
                            duracion = "1 mes",
                            costo = monto,
                            fechaInicio = pago.fechaInicio,
                            fechaVencimiento = pago.fechaVencimiento,
                            metodoPago = metodoPago,
                            emailUsuario = usuarioActual?.email
                        )
                    )
                    cargarMembresia()
                } catch (e: Exception) {
                    Log.e("API", "No se pudo actualizar la membresía", e)
                }
                onResult(true)
            } catch (e: Exception) {
                Log.e("API", "No se pudo registrar el pago", e)
                onResult(false)
            }
        }
    }

    fun registrar(nombre: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        mensajeError = null
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            mensajeError = "Completa todos los campos"
            onResult(false)
            return
        }
        viewModelScope.launch {
            try {
                val usuario = api.registro(
                    RegistroRequest(
                        nombre = nombre.trim(),
                        email = email.trim(),
                        password = password
                    )
                )
                usuarioActual = usuario.toEntity()
                sessionManager.guardarSesion(usuario.email)
                cargarTodo()
                onResult(true)
            } catch (e: HttpException) {
                Log.e("API", "Registro rechazado por la API (${e.code()})", e)
                mensajeError = if (e.code() == 409) {
                    "Ese correo ya está registrado"
                } else {
                    "La API respondió con error ${e.code()}"
                }
                onResult(false)
            } catch (e: Exception) {
                Log.e("API", "No se pudo conectar con la API al registrar", e)
                mensajeError =
                    "No se pudo conectar con la API. Verifica que iniciar-api.bat esté abierto."
                onResult(false)
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        mensajeError = null
        viewModelScope.launch {
            try {
                val usuario = api.login(LoginRequest(email.trim(), password))
                usuarioActual = usuario.toEntity()
                sessionManager.guardarSesion(usuario.email)
                cargarTodo()
                onResult(true)
            } catch (e: HttpException) {
                Log.e("API", "Login rechazado por la API (${e.code()})", e)
                usuarioActual = null
                mensajeError = if (e.code() == 401) {
                    "Correo o contraseña incorrectos. Prueba con carlos@mail.com / 1234"
                } else {
                    "La API respondió con error ${e.code()}"
                }
                onResult(false)
            } catch (e: Exception) {
                Log.e("API", "No se pudo conectar con la API al iniciar sesión", e)
                usuarioActual = null
                mensajeError =
                    "No se pudo conectar con la API. Verifica que iniciar-api.bat esté abierto."
                onResult(false)
            }
        }
    }

    fun cerrarSesion() {
        sessionManager.cerrarSesion()
        usuarioActual = null
        mensajeError = null
        _reservas.value = emptyList()
        _membresia.value = null
        _disponibilidad.value = emptyList()
        _rutinas.value = emptyList()
        _ejercicios.value = emptyList()
        _pagos.value = emptyList()
        entrenadorSeleccionadoId = null
        entrenadorSeleccionadoNombre = null
        rutinaSeleccionadaId = null
        ultimoPago = null
    }

    fun recuperarPassword(email: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank()) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            try {
                onResult(api.recuperar(email.trim()).existe)
            } catch (e: Exception) {
                Log.e("API", "No se pudo verificar el correo", e)
                onResult(false)
            }
        }
    }

    fun reservarClaseGrupal(claseId: Int, fecha: String, hora: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val clase = api.clase(claseId)
                if (clase.cuposOcupados >= clase.cuposTotal) {
                    onResult(false)
                    return@launch
                }
                val nueva = api.crearReserva(
                    ReservaRequest(
                        idClase = clase.id,
                        titulo = clase.nombre,
                        fecha = fecha,
                        hora = hora,
                        entrenador = clase.entrenador,
                        duracion = "${clase.duracionMin} min",
                        esGrupal = true,
                        emailUsuario = usuarioActual?.email
                    )
                )
                reservaSeleccionada = nueva
                cargarClases()
                cargarReservas()
                onResult(true)
            } catch (e: Exception) {
                Log.e("API", "No se pudo reservar la clase grupal", e)
                onResult(false)
            }
        }
    }

    fun reservarSesionPersonal(
        fecha: String,
        hora: String,
        entrenadorNombre: String? = null,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val nombreEntrenador = entrenadorNombre
                    ?: entrenadorSeleccionadoNombre
                    ?: _entrenador.value?.nombre
                    ?: runCatching { api.entrenadorPrincipal() }.getOrNull()?.nombre
                    ?: "Carlos Arturo"

                val nueva = api.crearReserva(
                    ReservaRequest(
                        idClase = null,
                        titulo = "Sesión personal",
                        fecha = fecha,
                        hora = hora,
                        entrenador = nombreEntrenador,
                        duracion = "60 min",
                        esGrupal = false,
                        emailUsuario = usuarioActual?.email
                    )
                )
                reservaSeleccionada = nueva
                cargarReservas()
                onResult(true)
            } catch (e: Exception) {
                Log.e("API", "No se pudo reservar la sesión personal", e)
                onResult(false)
            }
        }
    }

    fun cancelarReserva(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val respuesta = api.eliminarReserva(id)
                if (respuesta.isSuccessful) {
                    reservaSeleccionada = _reservas.value.firstOrNull { it.id == id }
                    cargarClases()
                    cargarReservas()
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("API", "No se pudo cancelar la reserva", e)
                onResult(false)
            }
        }
    }

    private fun UsuarioDto.toEntity() = UsuarioEntity(
        email = email,
        nombre = nombre,
        password = ""
    )
}
