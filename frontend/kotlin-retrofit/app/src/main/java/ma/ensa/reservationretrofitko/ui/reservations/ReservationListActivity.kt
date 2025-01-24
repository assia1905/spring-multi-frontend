package ma.ensa.reservationretrofitko.ui.reservations

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.adapters.ReservationAdapter
import ma.ensa.reservationretrofitko.api.ApiClient
import ma.ensa.reservationretrofitko.api.ApiInterface
import ma.ensa.reservationretrofitko.models.ReservationInput
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservationAdapter
    private lateinit var buttonAddReservation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddReservation = findViewById(R.id.buttonAddReservation)
        buttonAddReservation.setOnClickListener {
            val intent = Intent(this, AddReservationActivity::class.java)
            startActivity(intent)
        }

        loadReservations()
    }

    override fun onResume() {
        super.onResume()
        loadReservations() // Recharger les réservations à chaque retour à l'activité
    }

    private fun loadReservations() {
        // Temps de début
        val startTime = System.currentTimeMillis()

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getAllReservations()

        call.enqueue(object : Callback<List<ReservationInput>> {
            override fun onResponse(call: Call<List<ReservationInput>>, response: Response<List<ReservationInput>>) {
                // Temps de fin
                val endTime = System.currentTimeMillis()
                val durationMs = endTime - startTime // Temps écoulé en millisecondes

                // Afficher le temps de réponse dans les logs
                println("Temps de réponse pour getAllReservations : $durationMs ms")

                if (response.isSuccessful && response.body() != null) {
                    val reservations = response.body()!!
                    adapter = ReservationAdapter(reservations, this@ReservationListActivity)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@ReservationListActivity, "Failed to load reservations", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ReservationInput>>, t: Throwable) {
                // Temps de fin en cas d'échec
                val endTime = System.currentTimeMillis()
                val durationMs = endTime - startTime // Temps écoulé en millisecondes

                // Afficher le temps de réponse dans les logs
                println("Temps de réponse pour getAllReservations (échec) : $durationMs ms")

                Toast.makeText(this@ReservationListActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}