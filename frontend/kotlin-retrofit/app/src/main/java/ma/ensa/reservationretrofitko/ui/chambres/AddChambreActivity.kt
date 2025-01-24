package ma.ensa.reservationretrofitko.ui.chambres

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.api.ApiClient
import ma.ensa.reservationretrofitko.api.ApiInterface
import ma.ensa.reservationretrofitko.models.Chambre
import ma.ensa.reservationretrofitko.models.DispoChambre
import ma.ensa.reservationretrofitko.models.TypeChambre
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddChambreActivity : AppCompatActivity() {

    private lateinit var spinnerTypeChambre: Spinner
    private lateinit var editPrix: EditText
    private lateinit var spinnerDispoChambre: Spinner
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chambre)

        // Initialisation des vues
        spinnerTypeChambre = findViewById(R.id.spinnerTypeChambre)
        editPrix = findViewById(R.id.editPrix)
        spinnerDispoChambre = findViewById(R.id.spinnerDispoChambre)
        btnSave = findViewById(R.id.btnSave)

        // Configurer les Spinners
        setupSpinners()

        // Bouton de sauvegarde
        btnSave.setOnClickListener { saveChambre() }
    }

    private fun setupSpinners() {
        // Adapter pour TypeChambre
        val typeChambreAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, TypeChambre.values()
        )
        typeChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypeChambre.adapter = typeChambreAdapter

        // Adapter pour DispoChambre
        val dispoChambreAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, DispoChambre.values()
        )
        dispoChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDispoChambre.adapter = dispoChambreAdapter
    }

    private fun saveChambre() {
        val prixStr = editPrix.text.toString()

        // Validation des champs
        if (prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Créer une nouvelle chambre
            val chambre = Chambre().apply {
                typeChambre = spinnerTypeChambre.selectedItem as TypeChambre
                prix = prixStr.toDouble()
                dispoChambre = spinnerDispoChambre.selectedItem as DispoChambre
            }

            // Envoyer la chambre à l'API
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.createChambre(chambre)
            call.enqueue(object : Callback<Chambre> {
                override fun onResponse(call: Call<Chambre>, response: Response<Chambre>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddChambreActivity, "Chambre ajoutée", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish() // Fermer l'activité après l'ajout
                    } else {
                        Toast.makeText(this@AddChambreActivity, "Erreur d'ajout", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Chambre>, t: Throwable) {
                    Toast.makeText(this@AddChambreActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show()
            }
        }
}