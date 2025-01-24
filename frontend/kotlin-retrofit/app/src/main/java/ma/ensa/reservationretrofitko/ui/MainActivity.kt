package ma.ensa.reservationretrofitko.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.ui.chambres.ChambreListActivity
import ma.ensa.reservationretrofitko.ui.reservations.ReservationListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bouton pour accéder à la gestion des chambres
        val btnGestionChambres: Button = findViewById(R.id.btnGestionChambres)
        btnGestionChambres.setOnClickListener {
            val intent = Intent(this, ChambreListActivity::class.java)
            startActivity(intent)
        }

        // Bouton pour accéder à la gestion des réservations
        val btnGestionReservations: Button = findViewById(R.id.btnGestionReservations)
        btnGestionReservations.setOnClickListener {
            val intent = Intent(this, ReservationListActivity::class.java)
            startActivity(intent)
        }
    }
}