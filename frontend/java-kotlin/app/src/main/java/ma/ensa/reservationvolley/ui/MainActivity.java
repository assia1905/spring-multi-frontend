package ma.ensa.reservationvolley.ui;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import ma.ensa.reservationvolley.R;
import ma.ensa.reservationvolley.ui.chambres.ChambreListActivity;
import ma.ensa.reservationvolley.ui.reservations.ReservationListActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bouton pour accéder à la gestion des chambres
        Button btnGestionChambres = findViewById(R.id.btnGestionChambres);
        btnGestionChambres.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChambreListActivity.class);
            startActivity(intent);
        });

        // Bouton pour accéder à la gestion des réservations
        Button btnGestionReservations = findViewById(R.id.btnGestionReservations);
        btnGestionReservations.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReservationListActivity.class);
            startActivity(intent);
        });
    }
}