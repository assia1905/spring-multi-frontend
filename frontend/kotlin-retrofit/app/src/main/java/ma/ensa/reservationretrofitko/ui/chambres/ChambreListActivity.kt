package ma.ensa.reservationretrofitko.ui.chambres

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.adapters.ChambreAdapter
import ma.ensa.reservationretrofitko.api.ApiClient
import ma.ensa.reservationretrofitko.api.ApiInterface
import ma.ensa.reservationretrofitko.models.Chambre
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChambreListActivity : AppCompatActivity(), ChambreAdapter.OnChambreListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChambreAdapter
    private lateinit var btnAddChambre: Button

    // Utilisation de ActivityResultLauncher pour gérer les résultats d'activité
    private val addChambreLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            loadChambres() // Recharger les chambres après un ajout
        }
    }

    private val detailChambreLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            loadChambres() // Recharger les chambres après une modification
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chambre_list)

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerView)
        btnAddChambre = findViewById(R.id.btnAddChambre)

        // Configurer le RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurer le bouton "Ajouter une chambre"
        btnAddChambre.setOnClickListener {
            val intent = Intent(this, AddChambreActivity::class.java)
            addChambreLauncher.launch(intent)
        }

        // Charger les chambres depuis l'API
        loadChambres()
    }

    /**
     * Charge la liste des chambres depuis l'API.
     */
    private fun loadChambres() {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getAllChambres()
        call.enqueue(object : Callback<List<Chambre>> {
            override fun onResponse(call: Call<List<Chambre>>, response: Response<List<Chambre>>) {
                if (response.isSuccessful && response.body() != null) {
                    val chambres = response.body()!!

                    // Initialiser l'adaptateur avec la liste des chambres
                    adapter = ChambreAdapter(chambres, this@ChambreListActivity, this@ChambreListActivity)
                    recyclerView.adapter = adapter
                } else {
                    // Gérer le cas où la réponse est vide ou non réussie
                    Toast.makeText(this@ChambreListActivity, "Erreur lors du chargement des chambres", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chambre>>, t: Throwable) {
                // Gérer l'erreur de connexion
                Toast.makeText(this@ChambreListActivity, "Erreur de connexion : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onChambreClick(position: Int) {
        // Récupérer la chambre sélectionnée
        val chambre = adapter.getChambreAt(position)

        // Ouvrir l'activité de détail de la chambre
        val intent = Intent(this, ChambreDetailActivity::class.java).apply {
            putExtra("CHAMBRE_ID", chambre.id)
        }
        detailChambreLauncher.launch(intent)
    }
}