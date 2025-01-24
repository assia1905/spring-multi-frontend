package ma.ensa.reservationvolley.ui.chambres;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import java.lang.reflect.Type;
import java.util.List;

import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.adapters.ChambreAdapter;
import ma.ensa.reservationvolley.api.VolleySingleton;
import ma.ensa.reservationvolley.models.Chambre;

public class ChambreListActivity extends AppCompatActivity implements ChambreAdapter.OnChambreListener {

    private RecyclerView recyclerView;
    private ChambreAdapter adapter;
    private Button btnAddChambre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chambre_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAddChambre = findViewById(R.id.btnAddChambre);
        btnAddChambre.setOnClickListener(v -> {
            Intent intent = new Intent(ChambreListActivity.this, AddChambreActivity.class);
            startActivityForResult(intent, 1);
        });

        loadChambres();
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

    private void loadChambres() {
        String url = "http://100.110.64.142:8083/api/chambres"; // URL de l'endpoint

        // Mesurer la mémoire avant la requête
        double memoryBefore = getMemoryUsage();
        System.out.println("Mémoire utilisée avant la requête: " + memoryBefore + " MB");

        // Mesurer la consommation CPU avant la requête
        double cpuBefore = getCpuUsage();
        System.out.println("Consommation CPU avant la requête: " + cpuBefore + " ms");

        // Capturer le temps de départ
        long startTime = System.currentTimeMillis();

        // Créer une requête GET avec Volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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

                            // Désérialiser la réponse JSON en une liste de chambres
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Chambre>>() {}.getType();
                            List<Chambre> chambres = gson.fromJson(response.toString(), listType);

                            // Mettre à jour l'adaptateur
                            adapter = new ChambreAdapter(chambres, ChambreListActivity.this, ChambreListActivity.this);
                            recyclerView.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChambreListActivity.this, "Erreur lors de la désérialisation", Toast.LENGTH_SHORT).show();
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

                        // Gérer l'erreur
                        Toast.makeText(ChambreListActivity.this, "Erreur: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Ajouter la requête à la file d'attente
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            loadChambres(); // Recharger les chambres après une modification ou un ajout
        }
    }

    @Override
    public void onChambreClick(int position) {
        Chambre chambre = adapter.getChambreAt(position); // Utiliser la méthode getChambreAt
        Intent intent = new Intent(ChambreListActivity.this, ChambreDetailActivity.class);
        intent.putExtra("CHAMBRE_ID", chambre.getId());
        startActivityForResult(intent, 2);
    }
}