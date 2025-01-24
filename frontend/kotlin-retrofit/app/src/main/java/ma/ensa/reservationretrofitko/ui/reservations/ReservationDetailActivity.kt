package ma.ensa.reservationretrofitko.ui.reservations

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.api.ApiClient
import ma.ensa.reservationretrofitko.api.ApiInterface
import ma.ensa.reservationretrofitko.models.ReservationInput
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationDetailActivity : AppCompatActivity() {

    private lateinit var textViewClientId: TextView
    private lateinit var textViewChambreId: TextView
    private lateinit var textViewDateDebut: TextView
    private lateinit var textViewDateFin: TextView
    private lateinit var textViewPreferences: TextView
    private lateinit var buttonEditReservation: Button
    private lateinit var buttonDeleteReservation: Button
    private var reservationId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        textViewClientId = findViewById(R.id.textViewClientId)
        textViewChambreId = findViewById(R.id.textViewChambreId)
        textViewDateDebut = findViewById(R.id.textViewDateDebut)
        textViewDateFin = findViewById(R.id.textViewDateFin)
        textViewPreferences = findViewById(R.id.textViewPreferences)
        buttonEditReservation = findViewById(R.id.buttonEditReservation)
        buttonDeleteReservation = findViewById(R.id.buttonDeleteReservation)

        // Récupérer l'ID de la réservation depuis l'intent
        reservationId = intent.getLongExtra("reservationId", -1)

        if (reservationId != -1L) {
            loadReservationDetails(reservationId)
        }

        // Gérer le clic sur le bouton "Modifier Réservation"
        buttonEditReservation.setOnClickListener {
            val intent = Intent(this, AddReservationActivity::class.java).apply {
                putExtra("reservationId", reservationId)
            }
            startActivity(intent)
        }

        // Gérer le clic sur le bouton "Supprimer Réservation"
        buttonDeleteReservation.setOnClickListener {
            deleteReservation(reservationId)
        }
    }

    /**
     * Charge les détails d'une réservation depuis l'API.
     */
    private fun loadReservationDetails(reservationId: Long) {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getReservationById(reservationId)

        call.enqueue(object : Callback<ReservationInput> {
            override fun onResponse(call: Call<ReservationInput>, response: Response<ReservationInput>) {
                if (response.isSuccessful && response.body() != null) {
                    val reservationInput = response.body()!!
                    // Afficher les détails de la réservation
                    textViewClientId.text = "Client ID: ${reservationInput.client?.id}"
                    textViewChambreId.text = "Chambre ID: ${reservationInput.chambre?.id}"
                    textViewDateDebut.text = "Date Début: ${reservationInput.dateDebut}"
                    textViewDateFin.text = "Date Fin: ${reservationInput.dateFin}"
                    textViewPreferences.text = "Préférences: ${reservationInput.preferences}"
                } else {
                    Toast.makeText(this@ReservationDetailActivity, "Failed to load reservation details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReservationInput>, t: Throwable) {
                Toast.makeText(this@ReservationDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Supprime une réservation existante.
     */
    private fun deleteReservation(reservationId: Long) {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.deleteReservation(reservationId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ReservationDetailActivity, "Reservation deleted successfully", Toast.LENGTH_SHORT).show()
                    finish() // Fermer l'activité après la suppression
                } else {
                    Toast.makeText(this@ReservationDetailActivity, "Failed to delete reservation", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ReservationDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}