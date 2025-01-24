package ma.ensa.reservation.ui.chambres;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.ensa.reservation.R;
import ma.ensa.reservation.api.ApiClient;
import ma.ensa.reservation.api.ApiInterface;
import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.models.DispoChambre;
import ma.ensa.reservation.models.TypeChambre;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChambreActivity extends AppCompatActivity {

    private Spinner spinnerTypeChambre, spinnerDispoChambre;
    private EditText editPrix;
    private Button btnSave;

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
                this, android.R.layout.simple_spinner_item, TypeChambre.values());
        typeChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeChambre.setAdapter(typeChambreAdapter);

        // Adapter pour DispoChambre
        ArrayAdapter<DispoChambre> dispoChambreAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DispoChambre.values());
        dispoChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDispoChambre.setAdapter(dispoChambreAdapter);
    }

    private void saveChambre() {
        String prixStr = editPrix.getText().toString();

        // Validation des champs
        if (prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Créer une nouvelle chambre
            Chambre chambre = new Chambre();
            chambre.setTypeChambre((TypeChambre) spinnerTypeChambre.getSelectedItem());
            chambre.setPrix(Double.parseDouble(prixStr));
            chambre.setDispoChambre((DispoChambre) spinnerDispoChambre.getSelectedItem());

            // Envoyer la chambre à l'API
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<Chambre> call = apiService.createChambre(chambre);
            call.enqueue(new Callback<Chambre>() {
                @Override
                public void onResponse(Call<Chambre> call, Response<Chambre> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddChambreActivity.this, "Chambre ajoutée", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish(); // Fermer l'activité après l'ajout
                    } else {
                        Toast.makeText(AddChambreActivity.this, "Erreur d'ajout", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Chambre> call, Throwable t) {
                    Toast.makeText(AddChambreActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show();
        }
    }
}