package ma.ensa.reservationretrofitko.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.api.ApiClient
import ma.ensa.reservationretrofitko.api.ApiInterface
import ma.ensa.reservationretrofitko.api.AuthResponse
import ma.ensa.reservationretrofitko.models.AuthRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var editTextNom: EditText
    private lateinit var editTextPrenom: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextTelephone: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        editTextNom = findViewById(R.id.editTextNom)
        editTextPrenom = findViewById(R.id.editTextPrenom)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextTelephone = findViewById(R.id.editTextTelephone)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignup = findViewById(R.id.buttonSignup)

        buttonSignup.setOnClickListener { signupUser() }
    }

    private fun signupUser() {
        val nom = editTextNom.text.toString().trim()
        val prenom = editTextPrenom.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val telephone = editTextTelephone.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val authRequest = AuthRequest().apply {
            this.nom = nom
            this.prenom = prenom
            this.email = email
            this.telephone = telephone
            this.password = password
        }

        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiInterface.signup(authRequest)

        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@SignupActivity, "Inscription réussie", Toast.LENGTH_SHORT).show()
                    finish() // Retour à l'écran de connexion
                } else {
                    Toast.makeText(this@SignupActivity, "Échec de l'inscription", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}