package ma.ensa.reservation.ui.chambres;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ma.ensa.reservation.R;
import ma.ensa.reservation.api.ApiClient;
import ma.ensa.reservation.api.ApiInterface;
import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.models.DispoChambre;
import ma.ensa.reservation.models.TypeChambre;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChambreDetailActivity extends AppCompatActivity {

    private Spinner spinnerTypeChambre, spinnerDispoChambre;
    private EditText editPrix;
    private Button btnUpdate, btnDelete;
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
        Long chambreId = getIntent().getLongExtra("CHAMBRE_ID", -1);
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
                this, android.R.layout.simple_spinner_item, TypeChambre.values());
        typeChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeChambre.setAdapter(typeChambreAdapter);

        // Adapter pour DispoChambre
        ArrayAdapter<DispoChambre> dispoChambreAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DispoChambre.values());
        dispoChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDispoChambre.setAdapter(dispoChambreAdapter);
    }

    private void loadChambreDetails(Long chambreId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Chambre> call = apiService.getChambreById(chambreId);
        call.enqueue(new Callback<Chambre>() {
            @Override
            public void onResponse(Call<Chambre> call, Response<Chambre> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chambre = response.body();
                    spinnerTypeChambre.setSelection(Arrays.asList(TypeChambre.values()).indexOf(chambre.getTypeChambre()));
                    editPrix.setText(String.valueOf(chambre.getPrix()));
                    spinnerDispoChambre.setSelection(Arrays.asList(DispoChambre.values()).indexOf(chambre.getDispoChambre()));
                } else {
                    Toast.makeText(ChambreDetailActivity.this, "Chambre introuvable", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Chambre> call, Throwable t) {
                Toast.makeText(ChambreDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Chambre> call = apiService.updateChambre(chambre.getId(), chambre);
        call.enqueue(new Callback<Chambre>() {
            @Override
            public void onResponse(Call<Chambre> call, Response<Chambre> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChambreDetailActivity.this, "Chambre mise à jour", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ChambreDetailActivity.this, "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Chambre> call, Throwable t) {
                Toast.makeText(ChambreDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteChambre() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la chambre")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette chambre ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<Void> call = apiService.deleteChambre(chambre.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ChambreDetailActivity.this, "Chambre supprimée", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(ChambreDetailActivity.this, "La chambre est réservée et ne peut pas être supprimée", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ChambreDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }
}