package ma.ensa.reservationvolley.ui.auth;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.api.AuthResponse;
import ma.ensa.reservationvolley.api.VolleySingleton;
import ma.ensa.reservationvolley.models.AuthRequest;

public class SignupActivity extends AppCompatActivity {
    private EditText editTextNom, editTextPrenom, editTextEmail, editTextTelephone, editTextPassword;
    private Button buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialiser les vues
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTelephone = findViewById(R.id.editTextTelephone);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignup = findViewById(R.id.buttonSignup);

        // Gérer le clic sur le bouton d'inscription
        buttonSignup.setOnClickListener(v -> signupUser());
    }

    private void signupUser() {
        // Récupérer les valeurs saisies par l'utilisateur
        String nom = editTextNom.getText().toString().trim();
        String prenom = editTextPrenom.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String telephone = editTextTelephone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Vérifier que les champs ne sont pas vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer un objet AuthRequest
        AuthRequest authRequest = new AuthRequest();
        authRequest.setNom(nom);
        authRequest.setPrenom(prenom);
        authRequest.setEmail(email);
        authRequest.setTelephone(telephone);
        authRequest.setPassword(password);

        // Convertir l'objet AuthRequest en JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nom", authRequest.getNom());
            jsonBody.put("prenom", authRequest.getPrenom());
            jsonBody.put("email", authRequest.getEmail());
            jsonBody.put("telephone", authRequest.getTelephone());
            jsonBody.put("password", authRequest.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // URL de l'endpoint d'inscription
        String url = "http://100.110.64.142:8083/api/auth/signup";

        // Créer une requête POST avec Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Désérialiser la réponse JSON en AuthResponse
                            Gson gson = new Gson();
                            AuthResponse authResponse = gson.fromJson(response.toString(), AuthResponse.class);

                            // Afficher un message de succès
                            Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            // Retour à l'écran de connexion
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SignupActivity.this, "Erreur lors de la désérialisation", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérer l'erreur
                        Toast.makeText(SignupActivity.this, "Erreur: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Ajouter la requête à la file d'attente
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}