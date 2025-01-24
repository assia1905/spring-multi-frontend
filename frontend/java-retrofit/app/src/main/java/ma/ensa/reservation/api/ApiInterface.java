package ma.ensa.reservation.api;
import ma.ensa.reservation.models.AuthRequest;
import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.models.Client;
import java.util.List;

import ma.ensa.reservation.models.ReservationInput;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // Endpoint pour la connexion
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);

    // Endpoint pour l'inscription
    @POST("api/auth/signup")
    Call<AuthResponse> signup(@Body AuthRequest authRequest);


    // Récupérer tous les clients
    @GET("api/clients")
    Call<List<Client>> getAllClients();

    // Créer un nouveau client
    @POST("api/clients")
    Call<Client> createClient(@Body Client client);

    // Récupérer un client par son ID
    @GET("api/clients/{id}")
    Call<Client> getClientById(@Path("id") Long id);

    // Mettre à jour un client
    @PUT("api/clients/{id}")
    Call<Client> updateClient(@Path("id") Long id, @Body Client client);

    // Supprimer un client
    @DELETE("api/clients/{id}")
    Call<Void> deleteClient(@Path("id") Long id);

    // Récupérer les noms des clients
    @GET("api/clients/names")
    Call<List<String>> getClientsNames();

    // Trouver un client par nom et prénom
    @GET("api/clients/find")
    Call<Client> getClientByNameAndSurname(@Query("name") String name, @Query("surname") String surname);

    // Récupérer le profil du client connecté
    @GET("api/clients/profile")
    Call<Client> getClientProfile(@Header("Authorization") String token);

    // Supprimer le profil du client connecté
    @DELETE("api/clients/profile")
    Call<Void> deleteClientProfile(@Header("Authorization") String token);

    // Mettre à jour le profil du client connecté
    @PUT("api/clients/profile")
    Call<Client> updateClientProfile(@Header("Authorization") String token, @Body Client client);
    @GET("/api/chambres")
    Call<List<Chambre>> getAllChambres();

    @POST("/api/chambres")
    Call<Chambre> createChambre(@Body Chambre chambre);

    @GET("/api/chambres/{id}")
    Call<Chambre> getChambreById(@Path("id") Long id);

    @PUT("/api/chambres/{id}")
    Call<Chambre> updateChambre(@Path("id") Long id, @Body Chambre chambre);

    @DELETE("/api/chambres/{id}")
    Call<Void> deleteChambre(@Path("id") Long id);

    @GET("/api/reservations")
    Call<List<ReservationInput>> getAllReservations();

    @POST("/api/reservations") // Assurez-vous que l'endpoint est correct
    Call<ReservationInput> createReservation(@Body ReservationInput reservationInput);

    @GET("/api/reservations/{id}")
    Call<ReservationInput> getReservationById(@Path("id") Long id);

    @PUT("/api/reservations/{id}")
    Call<ReservationInput> updateReservation(@Path("id") Long id, @Body ReservationInput reservationInput);

    @DELETE("/api/reservations/{id}")
    Call<Void> deleteReservation(@Path("id") Long id);

    @GET("api/chambres/available")
    Call<List<Chambre>> getAvailableChambres();
}

