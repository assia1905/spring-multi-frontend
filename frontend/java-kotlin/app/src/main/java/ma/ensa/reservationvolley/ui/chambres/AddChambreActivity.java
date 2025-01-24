package ma.ensa.reservationvolley.ui.chambres;

import android.os.Bundle;
import android.os.Debug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.api.VolleySingleton;
import ma.ensa.reservationvolley.models.Chambre;
import ma.ensa.reservationvolley.models.DispoChambre;
import ma.ensa.reservationvolley.models.TypeChambre;

public class AddChambreActivity extends AppCompatActivity {

    private Spinner spinnerTypeChambre;
    private Spinner spinnerDispoChambre;
    private EditText editPrix;
    private Button btnSave;

    // Liste pour stocker les temps de réponse
    private final List<Long> responseTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chambre);

        // Initialisation des vues
        spinnerTypeChambre = findViewById(R.id.spinnerTypeChambre);
        editPrix = findViewById(R.id.editPrix);
        spinnerDispoChambre = findViewById(R.id.spinnerDispoChambre);
        btnSave = findViewById(R.id.btnSave);

        // Configurer les Spinners
        setupSpinners();

        // Bouton de sauvegarde
        btnSave.setOnClickListener(v -> saveChambre());
    }

    private void setupSpinners() {
        // Adapter pour TypeChambre
        ArrayAdapter<TypeChambre> typeChambreAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, TypeChambre.values()
        );
        typeChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeChambre.setAdapter(typeChambreAdapter);

        // Adapter pour DispoChambre
        ArrayAdapter<DispoChambre> dispoChambreAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DispoChambre.values()
        );
        dispoChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDispoChambre.setAdapter(dispoChambreAdapter);
    }

    private double getMemoryUsage() {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);

        // Mémoire utilisée en Mo
        double usedMemory = memoryInfo.getTotalPss() / 1024.0;
        return usedMemory;
    }

    private double getCpuUsage() {
        long threadCpuTime = Debug.threadCpuTimeNanos(); // Temps CPU en nanosecondes
        double cpuUsage = threadCpuTime / 1_000_000.0; // Convertir en millisecondes
        return cpuUsage;
    }

    private void saveChambre() {
        String prixStr = editPrix.getText().toString();

        // Validation des champs
        if (prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Mesurer la mémoire avant la requête
            double memoryBefore = getMemoryUsage();
            System.out.println("Mémoire utilisée avant la requête: " + memoryBefore + " MB");

            // Mesurer la consommation CPU avant la requête
            double cpuBefore = getCpuUsage();
            System.out.println("Consommation CPU avant la requête: " + cpuBefore + " ms");

            // Créer une nouvelle chambre
            Chambre chambre = new Chambre();
            chambre.setTypeChambre((TypeChambre) spinnerTypeChambre.getSelectedItem());
            chambre.setPrix(Double.parseDouble(prixStr));
            chambre.setDispoChambre((DispoChambre) spinnerDispoChambre.getSelectedItem());

            // Convertir l'objet Chambre en JSON
            Gson gson = new Gson();
            String jsonBody = gson.toJson(chambre);

            // Mesurer la taille de la requête
            int requestSize = jsonBody.getBytes().length;
            double requestSizeKB = requestSize / 1024.0;
            System.out.println("Taille de la requête: " + requestSizeKB + " KB");

            // URL de l'endpoint pour ajouter une chambre
            String url = "http://100.110.64.142:8083/api/chambres";

            // Capturer le temps de départ
            long startTime = System.currentTimeMillis();

            // Créer une requête POST avec Volley
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, new JSONObject(jsonBody),
                    response -> {
                        // Capturer le temps de fin
                        long endTime = System.currentTimeMillis();
                        long responseTime = endTime - startTime;
                        responseTimes.add(responseTime); // Ajouter le temps de réponse à la liste

                        // Calculer le temps moyen de réponse
                        double averageResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
                        System.out.println("Temps de réponse actuel: " + responseTime + " ms");
                        System.out.println("Temps moyen de réponse: " + averageResponseTime + " ms");

                        // Mesurer la mémoire après la requête
                        double memoryAfter = getMemoryUsage();
                        System.out.println("Mémoire utilisée après la requête: " + memoryAfter + " MB");

                        // Mesurer la consommation CPU après la requête
                        double cpuAfter = getCpuUsage();
                        System.out.println("Consommation CPU après la requête: " + cpuAfter + " ms");

                        // Mesurer la taille de la réponse
                        String responseJson = response.toString();
                        int responseSize = responseJson.getBytes().length;
                        double responseSizeKB = responseSize / 1024.0;
                        System.out.println("Taille de la réponse: " + responseSizeKB + " KB");

                        // Gérer la réponse JSON
                        Toast.makeText(AddChambreActivity.this, "Chambre ajoutée", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    },
                    error -> {
                        // Capturer le temps de fin en cas d'erreur
                        long endTime = System.currentTimeMillis();
                        long responseTime = endTime - startTime;
                        responseTimes.add(responseTime); // Ajouter le temps de réponse en cas d'erreur

                        // Calculer le temps moyen de réponse
                        double averageResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
                        System.out.println("Temps de réponse actuel (erreur): " + responseTime + " ms");
                        System.out.println("Temps moyen de réponse: " + averageResponseTime + " ms");

                        // Mesurer la mémoire en cas d'erreur
                        double memoryAfter = getMemoryUsage();
                        System.out.println("Mémoire utilisée après une erreur: " + memoryAfter + " MB");

                        // Mesurer la consommation CPU en cas d'erreur
                        double cpuAfter = getCpuUsage();
                        System.out.println("Consommation CPU après une erreur: " + cpuAfter + " ms");

                        // Gérer l'erreur
                        Toast.makeText(
                                AddChambreActivity.this,
                                "Erreur d'ajout: " + error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );

            // Ajouter la requête à la file d'attente
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création de la chambre", Toast.LENGTH_SHORT).show();
        }
    }
}