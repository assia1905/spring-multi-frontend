package ma.ensa.reservationvolley.ui.chambres;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.api.VolleySingleton;
import ma.ensa.reservationvolley.models.Chambre;
import ma.ensa.reservationvolley.models.DispoChambre;
import ma.ensa.reservationvolley.models.TypeChambre;

public class ChambreDetailActivity extends AppCompatActivity {

    private Spinner spinnerTypeChambre;
    private Spinner spinnerDispoChambre;
    private EditText editPrix;
    private Button btnUpdate;
    private Button btnDelete;
    private Chambre chambre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chambre_detail);

        // Initialisation des vues
        spinnerTypeChambre = findViewById(R.id.spinnerTypeChambre);
        editPrix = findViewById(R.id.editPrix);
        spinnerDispoChambre = findViewById(R.id.spinnerDispoChambre);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        // Configurer les Spinners
        setupSpinners();

        // Récupérer l'ID de la chambre depuis l'intent
        long chambreId = getIntent().getLongExtra("CHAMBRE_ID", -1);
        if (chambreId == -1) {
            Toast.makeText(this, "Chambre introuvable", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            loadChambreDetails(chambreId);
        }

        // Bouton de mise à jour
        btnUpdate.setOnClickListener(v -> updateChambre());

        // Bouton de suppression
        btnDelete.setOnClickListener(v -> deleteChambre());
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

    private double getCpuUsagePercentage(double cpuTimeMs, long elapsedTimeMs) {
        return (cpuTimeMs / elapsedTimeMs) * 100.0;
    }

    private void loadChambreDetails(long chambreId) {
        String url = "http://100.110.64.142:8083/api/chambres/" + chambreId;

        // Mesurer la mémoire avant la requête
        double memoryBefore = getMemoryUsage();
        System.out.println("Mémoire utilisée avant la requête: " + memoryBefore + " MB");

        // Mesurer la consommation CPU avant la requête
        double cpuBefore = getCpuUsage();
        System.out.println("Consommation CPU avant la requête: " + cpuBefore + " ms");

        // Capturer le temps de départ
        long startTime = System.currentTimeMillis();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Capturer le temps de fin
                            long endTime = System.currentTimeMillis();
                            long elapsedTimeMs = endTime - startTime;

                            // Mesurer la consommation CPU après la requête
                            double cpuAfter = getCpuUsage();
                            double cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs);
                            System.out.println("Consommation CPU après la requête: " + cpuUsagePercentage + " %");

                            // Mesurer la mémoire après la requête
                            double memoryAfter = getMemoryUsage();
                            System.out.println("Mémoire utilisée après la requête: " + memoryAfter + " MB");

                            // Mesurer la taille des données reçues
                            String jsonString = response.toString();
                            int sizeInBytes = jsonString.getBytes().length; // Taille en octets
                            double sizeInKB = sizeInBytes / 1024.0; // Convertir en KB
                            System.out.println("Taille des données reçues : " + sizeInKB + " KB");

                            // Mesurer le temps de réponse
                            long responseTime = endTime - startTime;
                            System.out.println("Temps de réponse: " + responseTime + " ms");

                            // Désérialiser la réponse JSON en un objet Chambre
                            Gson gson = new Gson();
                            chambre = gson.fromJson(response.toString(), Chambre.class);

                            // Mettre à jour les vues avec les détails de la chambre
                            spinnerTypeChambre.setSelection(Arrays.asList(TypeChambre.values()).indexOf(chambre.getTypeChambre()));
                            editPrix.setText(String.valueOf(chambre.getPrix()));
                            spinnerDispoChambre.setSelection(Arrays.asList(DispoChambre.values()).indexOf(chambre.getDispoChambre()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChambreDetailActivity.this, "Erreur lors de la lecture de la réponse JSON", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Capturer le temps de fin en cas d'erreur
                        long endTime = System.currentTimeMillis();
                        long elapsedTimeMs = endTime - startTime;

                        // Mesurer la consommation CPU en cas d'erreur
                        double cpuAfter = getCpuUsage();
                        double cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs);
                        System.out.println("Consommation CPU après une erreur: " + cpuUsagePercentage + " %");

                        // Mesurer la mémoire en cas d'erreur
                        double memoryAfter = getMemoryUsage();
                        System.out.println("Mémoire utilisée après une erreur: " + memoryAfter + " MB");

                        Toast.makeText(ChambreDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void updateChambre() {
        String prixStr = editPrix.getText().toString();

        if (prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            chambre.setTypeChambre((TypeChambre) spinnerTypeChambre.getSelectedItem());
            chambre.setPrix(Double.parseDouble(prixStr));
            chambre.setDispoChambre((DispoChambre) spinnerDispoChambre.getSelectedItem());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir l'objet Chambre en JSON
        Gson gson = new Gson();
        String jsonBody = gson.toJson(chambre);

        try {
            JSONObject jsonObject = new JSONObject(jsonBody);
            String url = "http://100.110.64.142:8083/api/chambres/" + chambre.getId();

            // Mesurer la mémoire avant la requête
            double memoryBefore = getMemoryUsage();
            System.out.println("Mémoire utilisée avant la requête: " + memoryBefore + " MB");

            // Mesurer la consommation CPU avant la requête
            double cpuBefore = getCpuUsage();
            System.out.println("Consommation CPU avant la requête: " + cpuBefore + " ms");

            // Capturer le temps de départ
            long startTime = System.currentTimeMillis();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Capturer le temps de fin
                            long endTime = System.currentTimeMillis();
                            long elapsedTimeMs = endTime - startTime;

                            // Mesurer la consommation CPU après la requête
                            double cpuAfter = getCpuUsage();
                            double cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs);
                            System.out.println("Consommation CPU après la requête: " + cpuUsagePercentage + " %");

                            // Mesurer la mémoire après la requête
                            double memoryAfter = getMemoryUsage();
                            System.out.println("Mémoire utilisée après la requête: " + memoryAfter + " MB");

                            // Mesurer la taille des données reçues
                            String jsonString = response.toString();
                            int sizeInBytes = jsonString.getBytes().length; // Taille en octets
                            double sizeInKB = sizeInBytes / 1024.0; // Convertir en KB
                            System.out.println("Taille des données reçues : " + sizeInKB + " KB");

                            // Mesurer le temps de réponse
                            long responseTime = endTime - startTime;
                            System.out.println("Temps de réponse: " + responseTime + " ms");

                            Toast.makeText(ChambreDetailActivity.this, "Chambre mise à jour", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Capturer le temps de fin en cas d'erreur
                            long endTime = System.currentTimeMillis();
                            long elapsedTimeMs = endTime - startTime;

                            // Mesurer la consommation CPU en cas d'erreur
                            double cpuAfter = getCpuUsage();
                            double cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs);
                            System.out.println("Consommation CPU après une erreur: " + cpuUsagePercentage + " %");

                            // Mesurer la mémoire en cas d'erreur
                            double memoryAfter = getMemoryUsage();
                            System.out.println("Mémoire utilisée après une erreur: " + memoryAfter + " MB");

                            Toast.makeText(ChambreDetailActivity.this, "Erreur de mise à jour: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création du JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteChambre() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la chambre")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette chambre ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    String url = "http://100.110.64.142:8083/api/chambres/" + chambre.getId();

                    // Mesurer la mémoire avant la requête
                    double memoryBefore = getMemoryUsage();
                    System.out.println("Mémoire utilisée avant la requête: " + memoryBefore + " MB");

                    // Mesurer la consommation CPU avant la requête
                    double cpuBefore = getCpuUsage();
                    System.out.println("Consommation CPU avant la requête: " + cpuBefore + " ms");

                    // Capturer le temps de départ
                    long startTime = System.currentTimeMillis();

                    StringRequest stringRequest = new StringRequest(
                            Request.Method.DELETE, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Capturer le temps de fin
                                    long endTime = System.currentTimeMillis();
                                    long elapsedTimeMs = endTime - startTime;

                                    // Mesurer la consommation CPU après la requête
                                    double cpuAfter = getCpuUsage();
                                    double cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs);
                                    System.out.println("Consommation CPU après la requête: " + cpuUsagePercentage + " %");

                                    // Mesurer la mémoire après la requête
                                    double memoryAfter = getMemoryUsage();
                                    System.out.println("Mémoire utilisée après la requête: " + memoryAfter + " MB");

                                    // Mesurer le temps de réponse
                                    long responseTime = endTime - startTime;
                                    System.out.println("Temps de réponse: " + responseTime + " ms");

                                    // Si la réponse est vide, afficher un message de succès
                                    Toast.makeText(ChambreDetailActivity.this, "Chambre supprimée", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Capturer le temps de fin en cas d'erreur
                                    long endTime = System.currentTimeMillis();
                                    long elapsedTimeMs = endTime - startTime;

                                    // Mesurer la consommation CPU en cas d'erreur
                                    double cpuAfter = getCpuUsage();
                                    double cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs);
                                    System.out.println("Consommation CPU après une erreur: " + cpuUsagePercentage + " %");

                                    // Mesurer la mémoire en cas d'erreur
                                    double memoryAfter = getMemoryUsage();
                                    System.out.println("Mémoire utilisée après une erreur: " + memoryAfter + " MB");

                                    if (error.networkResponse != null) {
                                        int statusCode = error.networkResponse.statusCode;
                                        switch (statusCode) {
                                            case 204:
                                                // Réponse vide (No Content)
                                                Toast.makeText(ChambreDetailActivity.this, "Chambre supprimée", Toast.LENGTH_SHORT).show();
                                                setResult(RESULT_OK);
                                                finish();
                                                break;
                                            case 400:
                                                // Erreur de contrainte de clé étrangère
                                                Toast.makeText(ChambreDetailActivity.this, "La chambre est réservée et ne peut pas être supprimée.", Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                // Autre erreur
                                                Toast.makeText(ChambreDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    } else {
                                        // Erreur réseau
                                        Toast.makeText(ChambreDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );

                    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
                })
                .setNegativeButton("Non", null)
                .show();
    }
}