package ma.ensa.reservationvolley.ui.auth;
import android.content.Intent;
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
import ma.ensa.reservationvolley.ui.MainActivity;
import ma.ensa.reservationvolley.utils.SharedPreferencesManager;


public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser les vues
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        // Gérer le clic sur le bouton de connexion
        buttonLogin.setOnClickListener(v -> loginUser());

        // Gérer le clic sur le bouton d'inscription
        buttonSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Récupérer les valeurs saisies par l'utilisateur
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Vérifier que les champs ne sont pas vides
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer un objet AuthRequest
        AuthRequest authRequest = new AuthRequest(username, password);

        // Convertir l'objet AuthRequest en JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", authRequest.getEmail());
            jsonBody.put("password", authRequest.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // URL de l'endpoint de connexion
        String url = "http://100.110.64.142:8083/api/auth/login";

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

                            // Stocker le token dans SharedPreferences
                            SharedPreferencesManager.getInstance(LoginActivity.this).saveToken(authResponse.getToken());

                            // Afficher un message de succès
                            Toast.makeText(LoginActivity.this, "\n" +
                                    "Successful login", Toast.LENGTH_SHORT).show();

                            // Rediriger vers MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erreur lors de la désérialisation", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérer l'erreur
                        Toast.makeText(LoginActivity.this, "Erreur: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Ajouter la requête à la file d'attente
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}