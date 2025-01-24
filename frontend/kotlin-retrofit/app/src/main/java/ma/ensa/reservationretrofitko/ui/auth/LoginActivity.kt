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
import ma.ensa.reservationretrofitko.ui.MainActivity
import ma.ensa.reservationretrofitko.utils.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignup = findViewById(R.id.buttonSignup)

        buttonLogin.setOnClickListener { loginUser() }
        buttonSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val authRequest = AuthRequest(username, password)
        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiInterface.login(authRequest)

        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    if (token != null) {
                        // Stocker le token dans SharedPreferences
                        SharedPreferencesManager.getInstance(this@LoginActivity).saveToken(token)
                        Toast.makeText(this@LoginActivity, "Connexion réussie", Toast.LENGTH_SHORT).show()
                        // Rediriger vers MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Token non reçu", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Échec de la connexion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}