package ma.ensa.reservationvolley.ui.reservations;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.api.VolleySingleton;
import ma.ensa.reservationvolley.models.Chambre;
import ma.ensa.reservationvolley.models.Client;
import ma.ensa.reservationvolley.models.DispoChambre;
import ma.ensa.reservationvolley.models.ReservationInput;
import ma.ensa.reservationvolley.models.TypeChambre;

public class AddReservationActivity extends AppCompatActivity {

    private EditText editTextClientId, editTextDateDebut, editTextDateFin, editTextPreferences;
    private Spinner spinnerChambres;
    private Button buttonSaveReservation;
    private Long reservationId; // Pour stocker l'ID de la réservation en mode édition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        // Initialiser les vues
        editTextClientId = findViewById(R.id.editTextClientId);
        editTextDateDebut = findViewById(R.id.editTextDateDebut);
        editTextDateFin = findViewById(R.id.editTextDateFin);
        editTextPreferences = findViewById(R.id.editTextPreferences);
        spinnerChambres = findViewById(R.id.spinnerChambres);
        buttonSaveReservation = findViewById(R.id.buttonSaveReservation);

        // Récupérer l'ID de la réservation depuis l'intent (si en mode édition)
        reservationId = getIntent().getLongExtra("reservationId", -1);

        // Charger les chambres disponibles
        loadAvailableChambres();

        // Si en mode édition, charger les détails de la réservation
        if (reservationId != -1) {
            loadReservationDetails(reservationId);
        }
        // Gérer le clic sur le bouton "Sauvegarder Réservation"
        buttonSaveReservation.setOnClickListener(v -> {
            if (reservationId != -1) {
                updateReservation(reservationId); // Mode édition
            } else {
                saveReservation(); // Mode création
            }
        });
    }

    /**
     * Charge les chambres disponibles depuis l'API et les affiche dans le Spinner.
     */
    private void loadAvailableChambres() {
        String url = "http://100.110.64.142:8083/api/chambres/available";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Chambre> chambres = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);

                            // Parsing de la chambre
                            Chambre chambre = new Chambre();
                            chambre.setId(jsonObject.getLong("id"));

                            // Parsing de TypeChambre (enum)
                            String typeChambreStr = jsonObject.getString("typeChambre");
                            TypeChambre typeChambre = TypeChambre.valueOf(typeChambreStr);
                            chambre.setTypeChambre(typeChambre);

                            // Parsing de DispoChambre (enum)
                            String dispoChambreStr = jsonObject.getString("dispoChambre");
                            DispoChambre dispoChambre = DispoChambre.valueOf(dispoChambreStr);
                            chambre.setDispoChambre(dispoChambre);

                            chambre.setPrix(jsonObject.getDouble("prix"));
                            chambres.add(chambre);
                        }

                        // Afficher les chambres dans le Spinner
                        ArrayAdapter<Chambre> adapter = new ArrayAdapter<>(
                                AddReservationActivity.this,
                                android.R.layout.simple_spinner_item,
                                chambres
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerChambres.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddReservationActivity.this, "Failed to parse chambres", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddReservationActivity.this, "Network error", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Charge les détails d'une réservation existante pour les afficher dans le formulaire.
     */
    private void loadReservationDetails(Long reservationId) {
        String url = "http://100.110.64.142:8083/api/reservations/" + reservationId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        ReservationInput reservationInput = new ReservationInput();
                        reservationInput.setDateDebut(response.getString("dateDebut"));
                        reservationInput.setDateFin(response.getString("dateFin"));
                        reservationInput.setPreferences(response.getString("preferences"));

                        // Parsing du client
                        JSONObject clientJson = response.getJSONObject("client");
                        Client client = new Client();
                        client.setId(clientJson.getLong("id"));
                        reservationInput.setClient(client);

                        // Parsing de la chambre
                        JSONObject chambreJson = response.getJSONObject("chambre");
                        Chambre chambre = new Chambre();
                        chambre.setId(chambreJson.getLong("id"));

                        // Parsing de TypeChambre (enum)
                        String typeChambreStr = chambreJson.getString("typeChambre");
                        TypeChambre typeChambre = TypeChambre.valueOf(typeChambreStr);
                        chambre.setTypeChambre(typeChambre);

                        // Parsing de DispoChambre (enum)
                        String dispoChambreStr = chambreJson.getString("dispoChambre");
                        DispoChambre dispoChambre = DispoChambre.valueOf(dispoChambreStr);
                        chambre.setDispoChambre(dispoChambre);

                        chambre.setPrix(chambreJson.getDouble("prix"));
                        reservationInput.setChambre(chambre);

                        // Remplir les champs du formulaire
                        editTextClientId.setText(String.valueOf(client.getId()));
                        editTextDateDebut.setText(reservationInput.getDateDebut());
                        editTextDateFin.setText(reservationInput.getDateFin());
                        editTextPreferences.setText(reservationInput.getPreferences());

                        // Sélectionner la chambre dans le Spinner
                        for (int i = 0; i < spinnerChambres.getCount(); i++) {
                            Chambre chambreSpinner = (Chambre) spinnerChambres.getItemAtPosition(i);
                            if (chambreSpinner.getId().equals(chambre.getId())) {
                                spinnerChambres.setSelection(i);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddReservationActivity.this, "Failed to parse reservation details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddReservationActivity.this, "Network error", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Sauvegarde une nouvelle réservation en utilisant les données saisies par l'utilisateur.
     */
    private void saveReservation() {
        // Validation des champs
        if (editTextClientId.getText().toString().isEmpty() ||
                editTextDateDebut.getText().toString().isEmpty() ||
                editTextDateFin.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(editTextDateDebut.getText().toString()) || !isValidDate(editTextDateFin.getText().toString())) {
            Toast.makeText(this, "Format de date invalide. Utilisez yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupération des données
        Long clientId = Long.parseLong(editTextClientId.getText().toString());
        Chambre selectedChambre = (Chambre) spinnerChambres.getSelectedItem();

        if (selectedChambre == null) {
            Toast.makeText(this, "Veuillez sélectionner une chambre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création des objets
        Client client = new Client();
        client.setId(clientId);

        String dateDebut = editTextDateDebut.getText().toString();
        String dateFin = editTextDateFin.getText().toString();
        String preferences = editTextPreferences.getText().toString();

        // Création de l'objet ReservationInput
        ReservationInput reservationInput = new ReservationInput(client, selectedChambre, dateDebut, dateFin, preferences);

        // Envoi de la requête
        String url = "http://100.110.64.142:8083/api/reservations";
        Gson gson = new Gson();
        String json = gson.toJson(reservationInput);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(AddReservationActivity.this, "Reservation saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String errorMessage = "Failed to save reservation: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            errorMessage += new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception e) {
                            errorMessage += "Unknown error";
                            e.printStackTrace();
                        }
                    } else {
                        errorMessage += "No error body";
                    }
                    Toast.makeText(AddReservationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                return json.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Valide le format de la date (yyyy-MM-dd).
     */
    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    /**
     * Met à jour une réservation existante en utilisant les données saisies par l'utilisateur.
     */
    private void updateReservation(Long reservationId) {
        // Validation des champs
        if (editTextClientId.getText().toString().isEmpty() ||
                editTextDateDebut.getText().toString().isEmpty() ||
                editTextDateFin.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(editTextDateDebut.getText().toString()) || !isValidDate(editTextDateFin.getText().toString())) {
            Toast.makeText(this, "Format de date invalide. Utilisez yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupération des données
        Long clientId = Long.parseLong(editTextClientId.getText().toString());
        Chambre selectedChambre = (Chambre) spinnerChambres.getSelectedItem();

        if (selectedChambre == null) {
            Toast.makeText(this, "Veuillez sélectionner une chambre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création des objets
        Client client = new Client();
        client.setId(clientId);

        String dateDebut = editTextDateDebut.getText().toString();
        String dateFin = editTextDateFin.getText().toString();
        String preferences = editTextPreferences.getText().toString();

        // Création de l'objet ReservationInput
        ReservationInput reservationInput = new ReservationInput(client, selectedChambre, dateDebut, dateFin, preferences);

        // Envoi de la requête PUT pour mettre à jour la réservation
        String url = "http://100.110.64.142:8083/api/reservations/" + reservationId;
        Gson gson = new Gson();
        String json = gson.toJson(reservationInput);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null,
                response -> {
                    Toast.makeText(AddReservationActivity.this, "Reservation updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Fermer l'activité après la mise à jour
                },
                error -> {
                    String errorMessage = "Failed to update reservation: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            errorMessage += new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception e) {
                            errorMessage += "Unknown error";
                            e.printStackTrace();
                        }
                    } else {
                        errorMessage += "No error body";
                    }
                    Toast.makeText(AddReservationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                return json.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}