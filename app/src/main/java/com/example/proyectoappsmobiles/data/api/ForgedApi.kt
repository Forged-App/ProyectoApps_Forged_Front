package com.example.proyectoappsmobiles.data.api

import com.example.proyectoappsmobiles.data.entity.ClaseEntity
import com.example.proyectoappsmobiles.data.entity.EntrenadorEntity
import com.example.proyectoappsmobiles.data.entity.MembresiaEntity
import com.example.proyectoappsmobiles.data.entity.ReservaEntity
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato completo con la API Forged (Spring Boot).
 * La app deja de depender de Room para los datos de negocio: todo pasa por aquí.
 */
interface ForgedApi {

    // ---------- Clases ----------
    @GET("clases")
    suspend fun clases(): List<ClaseEntity>

    @GET("clases/{id}")
    suspend fun clase(@Path("id") id: Int): ClaseEntity

    // ---------- Autenticación / usuarios ----------
    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): UsuarioDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): UsuarioDto

    @GET("auth/usuario")
    suspend fun usuario(@Query("email") email: String): UsuarioDto

    @GET("auth/recuperar")
    suspend fun recuperar(@Query("email") email: String): RecuperarResponse

    // ---------- Entrenadores ----------
    @GET("entrenadores")
    suspend fun entrenadores(): List<EntrenadorEntity>

    @GET("entrenadores/primero")
    suspend fun entrenadorPrincipal(): EntrenadorEntity?

    // ---------- Reservas ----------
    @GET("reservas")
    suspend fun reservas(@Query("email") email: String? = null): List<ReservaEntity>

    @POST("reservas")
    suspend fun crearReserva(@Body request: ReservaRequest): ReservaEntity

    @DELETE("reservas/{id}")
    suspend fun eliminarReserva(@Path("id") id: Int): Response<Unit>

    // ---------- Membresías ----------
    @GET("membresias/actual")
    suspend fun membresiaActual(@Query("email") email: String? = null): Response<MembresiaEntity>

    @POST("membresias")
    suspend fun crearMembresia(@Body request: MembresiaRequest): MembresiaEntity

    // ---------- Disponibilidad ----------
    @GET("disponibilidad")
    suspend fun disponibilidad(
        @Query("idEntrenador") idEntrenador: Int? = null,
        @Query("dia") dia: String? = null
    ): List<DisponibilidadDto>

    // ---------- Rutinas ----------
    @GET("rutinas")
    suspend fun rutinas(): List<RutinaDto>

    @GET("rutinas/{id}")
    suspend fun rutina(@Path("id") id: Int): RutinaDto

    // ---------- Ejercicios de rutina ----------
    @GET("ejercicios")
    suspend fun ejercicios(@Query("idRutina") idRutina: Int? = null): List<EjercicioRutinaDto>

    @PUT("ejercicios/{id}")
    suspend fun actualizarEjercicio(
        @Path("id") id: Int,
        @Body ejercicio: EjercicioRutinaDto
    ): EjercicioRutinaDto

    // ---------- Pagos ----------
    @GET("pagos")
    suspend fun pagos(@Query("email") email: String? = null): List<PagoDto>

    @POST("pagos")
    suspend fun crearPago(@Body request: PagoRequest): PagoDto
}

object ApiProvider {
    // Primero se intenta el túnel de adb (127.0.0.1), que funciona igual en el
    // emulador y en un celular conectado por USB:
    //   adb reverse tcp:8080 tcp:8080
    // Si el túnel no está activo, la app reintenta sola contra 10.0.2.2, que es
    // el localhost del PC visto desde el emulador.
    private const val BASE_URL = "http://127.0.0.1:8080/"
    private const val HOST_RESPALDO = "10.0.2.2"

    private val respaldoEmulador = okhttp3.Interceptor { chain ->
        val peticion = chain.request()
        try {
            chain.proceed(peticion)
        } catch (e: java.io.IOException) {
            if (peticion.url.host == "127.0.0.1") {
                val alternativa = peticion.url.newBuilder().host(HOST_RESPALDO).build()
                chain.proceed(peticion.newBuilder().url(alternativa).build())
            } else {
                throw e
            }
        }
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(respaldoEmulador)
        .retryOnConnectionFailure(true)
        // Si 127.0.0.1 no responde, el reintento contra 10.0.2.2 debe ser rápido.
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ForgedApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ForgedApi::class.java)
    }
}
