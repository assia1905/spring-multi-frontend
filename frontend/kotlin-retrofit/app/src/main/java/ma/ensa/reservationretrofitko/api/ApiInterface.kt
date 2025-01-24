package ma.ensa.reservationretrofitko.api
import ma.ensa.reservationretrofitko.models.AuthRequest
import ma.ensa.reservationretrofitko.models.Chambre
import ma.ensa.reservationretrofitko.models.ReservationInput
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
interface ApiInterface {

    // Endpoint pour la connexion
    @POST("api/auth/login")
    fun login(@Body authRequest: AuthRequest): Call<AuthResponse>

    // Endpoint pour l'inscription
    @POST("api/auth/signup")
    fun signup(@Body authRequest: AuthRequest): Call<AuthResponse>

    // Endpoint pour obtenir toutes les chambres
    @GET("/api/chambres")
    fun getAllChambres(): Call<List<Chambre>>

    // Endpoint pour créer une chambre
    @POST("/api/chambres")
    fun createChambre(@Body chambre: Chambre): Call<Chambre>

    // Endpoint pour obtenir une chambre par son ID
    @GET("/api/chambres/{id}")
    fun getChambreById(@Path("id") id: Long): Call<Chambre>

    // Endpoint pour mettre à jour une chambre
    @PUT("/api/chambres/{id}")
    fun updateChambre(@Path("id") id: Long, @Body chambre: Chambre): Call<Chambre>

    // Endpoint pour supprimer une chambre
    @DELETE("/api/chambres/{id}")
    fun deleteChambre(@Path("id") id: Long): Call<Void>

    // Endpoint pour obtenir toutes les réservations
    @GET("/api/reservations")
    fun getAllReservations(): Call<List<ReservationInput>>

    // Endpoint pour créer une réservation
    @POST("/api/reservations")
    fun createReservation(@Body reservationInput: ReservationInput): Call<ReservationInput>

    // Endpoint pour obtenir une réservation par son ID
    @GET("/api/reservations/{id}")
    fun getReservationById(@Path("id") id: Long): Call<ReservationInput>

    // Endpoint pour mettre à jour une réservation
    @PUT("/api/reservations/{id}")
    fun updateReservation(@Path("id") id: Long, @Body reservationInput: ReservationInput): Call<ReservationInput>

    // Endpoint pour supprimer une réservation
    @DELETE("/api/reservations/{id}")
    fun deleteReservation(@Path("id") id: Long): Call<Void>

    // Endpoint pour obtenir les chambres disponibles
    @GET("api/chambres/available")
    fun getAvailableChambres(): Call<List<Chambre>>
}