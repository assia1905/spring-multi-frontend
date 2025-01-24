package ma.ensa.reservation.ui.reservations;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.reservation.R;
import ma.ensa.reservation.adapters.ReservationAdapter;
import ma.ensa.reservation.api.ApiClient;
import ma.ensa.reservation.api.ApiInterface;
import ma.ensa.reservation.models.ReservationInput;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private Button buttonAddReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonAddReservation = findViewById(R.id.buttonAddReservation);
        buttonAddReservation.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationListActivity.this, AddReservationActivity.class);
            startActivity(intent);
        });

        loadReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations(); // Recharger les réservations à chaque retour à l'activité
    }

    private void loadReservations() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<ReservationInput>> call = apiService.getAllReservations();

        call.enqueue(new Callback<List<ReservationInput>>() {
            @Override
            public void onResponse(Call<List<ReservationInput>> call, Response<List<ReservationInput>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReservationInput> reservations = response.body();
                    adapter = new ReservationAdapter(reservations, ReservationListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ReservationListActivity.this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReservationInput>> call, Throwable t) {
                Toast.makeText(ReservationListActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}