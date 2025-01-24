package ma.ensa.reservationvolley.ui.reservations;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.models.Chambre;
import ma.ensa.reservationvolley.models.Client;
import ma.ensa.reservationvolley.models.ReservationInput;
import ma.ensa.reservationvolley.models.TypeChambre;
import ma.ensa.reservationvolley.models.DispoChambre;

public class ReservationDetailActivity extends AppCompatActivity {

    private TextView textViewClientId, textViewChambreId, textViewDateDebut, textViewDateFin, textViewPreferences;
    private Button buttonEditReservation, buttonDeleteReservation;
    private Long reservationId;
    private RequestQueue requestQueue; // File d'attente des requêtes Volley

    // URL de base de l'API
    private static final String BASE_URL = "http://100.110.64.142:8083/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        textViewClientId = findViewById(R.id.textViewClientId);
        textViewChambreId = findViewById(R.id.textViewChambreId);
        textViewDateDebut = findViewById(R.id.textViewDateDebut);
        textViewDateFin = findViewById(R.id.textViewDateFin);
        textViewPreferences = findViewById(R.id.textViewPreferences);
        buttonEditReservation = findViewById(R.id.buttonEditReservation);
        buttonDeleteReservation = findViewById(R.id.buttonDeleteReservation);

        // Initialiser la file d'attente Volley
        requestQueue = Volley.newRequestQueue(this);

        // Récupérer l'ID de la réservation depuis l'intent
        reservationId = getIntent().getLongExtra("reservationId", -1);

        if (reservationId != -1) {
            loadReservationDetails(reservationId);
        }

        // Gérer le clic sur le bouton "Modifier Réservation"
        buttonEditReservation.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationDetailActivity.this, AddReservationActivity.class);
            intent.putExtra("reservationId", reservationId);
            startActivity(intent);
        });

        // Gérer le clic sur le bouton "Supprimer Réservation"
        buttonDeleteReservation.setOnClickListener(v -> deleteReservation(reservationId));
    }

    /**
     * Charge les détails d'une réservation depuis l'API.
     */
    private void loadReservationDetails(Long reservationId) {
        String url = BASE_URL + "api/reservations/" + reservationId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Extraire les données de la réponse JSON
                        ReservationInput reservationInput = new ReservationInput();

                        // Extraire les informations du client
                        JSONObject clientJson = response.getJSONObject("client");
                        Client client = new Client();
                        client.setId(clientJson.getLong("id"));
                        reservationInput.setClient(client);

                        // Extraire les informations de la chambre
                        JSONObject chambreJson = response.getJSONObject("chambre");
                        Chambre chambre = new Chambre();
                        chambre.setId(chambreJson.getLong("id"));
                        chambre.setTypeChambre(TypeChambre.valueOf(chambreJson.getString("typeChambre")));
                        chambre.setPrix(chambreJson.getDouble("prix"));
                        chambre.setDispoChambre(DispoChambre.valueOf(chambreJson.getString("dispoChambre")));
                        reservationInput.setChambre(chambre);

                        // Extraire les autres informations
                        reservationInput.setDateDebut(response.getString("dateDebut"));
                        reservationInput.setDateFin(response.getString("dateFin"));
                        reservationInput.setPreferences(response.getString("preferences"));

                        // Afficher les détails de la réservation
                        textViewClientId.setText("Client ID: " + client.getId());
                        textViewChambreId.setText("Chambre ID: " + chambre.getId());
                        textViewDateDebut.setText("Date Début: " + reservationInput.getDateDebut());
                        textViewDateFin.setText("Date Fin: " + reservationInput.getDateFin());
                        textViewPreferences.setText("Préférences: " + reservationInput.getPreferences());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ReservationDetailActivity.this, "Failed to parse reservation details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(ReservationDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    /**
     * Supprime une réservation existante.
     */
    private void deleteReservation(Long reservationId) {
        String url = BASE_URL + "api/reservations/" + reservationId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(ReservationDetailActivity.this, "Reservation deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Fermer l'activité après la suppression
                },
                error -> {
                    Toast.makeText(ReservationDetailActivity.this, "Failed to delete reservation", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }
}