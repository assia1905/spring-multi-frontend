package ma.ensa.reservationretrofitko.ui.reservations

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
import ma.ensa.reservationretrofitko.models.Client
import ma.ensa.reservationretrofitko.models.ReservationInput
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat

class AddReservationActivity : AppCompatActivity() {

    private lateinit var editTextClientId: EditText
    private lateinit var editTextDateDebut: EditText
    private lateinit var editTextDateFin: EditText
    private lateinit var editTextPreferences: EditText
    private lateinit var spinnerChambres: Spinner
    private lateinit var buttonSaveReservation: Button
    private var reservationId: Long = -1 // Pour stocker l'ID de la réservation en mode édition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)

        // Initialiser les vues
        editTextClientId = findViewById(R.id.editTextClientId)
        editTextDateDebut = findViewById(R.id.editTextDateDebut)
        editTextDateFin = findViewById(R.id.editTextDateFin)
        editTextPreferences = findViewById(R.id.editTextPreferences)
        spinnerChambres = findViewById(R.id.spinnerChambres)
        buttonSaveReservation = findViewById(R.id.buttonSaveReservation)

        // Récupérer l'ID de la réservation depuis l'intent (si en mode édition)
        reservationId = intent.getLongExtra("reservationId", -1)

        // Charger les chambres disponibles
        loadAvailableChambres()

        // Si en mode édition, charger les détails de la réservation
        if (reservationId != -1L) {
            loadReservationDetails(reservationId)
        }

        // Gérer le clic sur le bouton "Sauvegarder Réservation"
        buttonSaveReservation.setOnClickListener {
            if (reservationId != -1L) {
                updateReservation(reservationId) // Mode édition
            } else {
                saveReservation() // Mode création
            }
        }
    }

    /**
     * Charge les chambres disponibles depuis l'API et les affiche dans le Spinner.
     */
    private fun loadAvailableChambres() {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getAvailableChambres() // Appel à l'endpoint des chambres disponibles

        call.enqueue(object : Callback<List<Chambre>> {
            override fun onResponse(call: Call<List<Chambre>>, response: Response<List<Chambre>>) {
                if (response.isSuccessful && response.body() != null) {
                    val chambres = response.body()!!

                    // Créer un adaptateur pour le Spinner
                    val adapter = ArrayAdapter(
                        this@AddReservationActivity,
                        android.R.layout.simple_spinner_item,
                        chambres
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerChambres.adapter = adapter
                } else {
                    Toast.makeText(this@AddReservationActivity, "Failed to load available chambres", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chambre>>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Charge les détails d'une réservation existante pour les afficher dans le formulaire.
     */
    private fun loadReservationDetails(reservationId: Long) {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getReservationById(reservationId)

        call.enqueue(object : Callback<ReservationInput> {
            override fun onResponse(call: Call<ReservationInput>, response: Response<ReservationInput>) {
                if (response.isSuccessful && response.body() != null) {
                    val reservationInput = response.body()!!

                    // Remplir les champs du formulaire avec les détails de la réservation
                    editTextClientId.setText(reservationInput.client?.id.toString())
                    editTextDateDebut.setText(reservationInput.dateDebut)
                    editTextDateFin.setText(reservationInput.dateFin)
                    editTextPreferences.setText(reservationInput.preferences)

                    // Sélectionner la chambre correspondante dans le Spinner
                    for (i in 0 until spinnerChambres.count) {
                        val chambre = spinnerChambres.getItemAtPosition(i) as Chambre
                        if (chambre.id == reservationInput.chambre?.id) {
                            spinnerChambres.setSelection(i)
                            break
                        }
                    }
                } else {
                    Toast.makeText(this@AddReservationActivity, "Failed to load reservation details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReservationInput>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Sauvegarde une nouvelle réservation en utilisant les données saisies par l'utilisateur.
     */
    private fun saveReservation() {
        // Vérifier que les champs ne sont pas vides
        if (editTextClientId.text.toString().isEmpty() ||
            editTextDateDebut.text.toString().isEmpty() ||
            editTextDateFin.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Valider le format des dates
        if (!isValidDate(editTextDateDebut.text.toString()) || !isValidDate(editTextDateFin.text.toString())) {
            Toast.makeText(this, "Format de date invalide. Utilisez yyyy-MM-dd", Toast.LENGTH_SHORT).show()
            return
        }

        // Récupérer les données saisies
        val clientId = editTextClientId.text.toString().toLong()
        val selectedChambre = spinnerChambres.selectedItem as? Chambre

        // Vérifier qu'une chambre est sélectionnée
        if (selectedChambre == null) {
            Toast.makeText(this, "Veuillez sélectionner une chambre", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer des objets Client et Chambre
        val client = Client().apply { id = clientId }
        val chambre = Chambre().apply { id = selectedChambre.id }

        val dateDebut = editTextDateDebut.text.toString()
        val dateFin = editTextDateFin.text.toString()
        val preferences = editTextPreferences.text.toString()

        // Créer un objet ReservationInput avec les objets complets
        val reservationInput = ReservationInput(client, chambre, dateDebut, dateFin, preferences)

        // Envoyer la réservation à l'API
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.createReservation(reservationInput)
        call.enqueue(object : Callback<ReservationInput> {
            override fun onResponse(call: Call<ReservationInput>, response: Response<ReservationInput>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddReservationActivity, "Reservation saved successfully", Toast.LENGTH_SHORT).show()
                    finish() // Fermer l'activité après la sauvegarde
                } else {
                    // Afficher le message d'erreur retourné par l'API
                    val errorMessage = "Failed to save reservation: " + response.errorBody()?.string()
                    Toast.makeText(this@AddReservationActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ReservationInput>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Met à jour une réservation existante en utilisant les données saisies par l'utilisateur.
     */
    private fun updateReservation(reservationId: Long) {
        // Vérifier que les champs ne sont pas vides
        if (editTextClientId.text.toString().isEmpty() ||
            editTextDateDebut.text.toString().isEmpty() ||
            editTextDateFin.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Valider le format des dates
        if (!isValidDate(editTextDateDebut.text.toString()) || !isValidDate(editTextDateFin.text.toString())) {
            Toast.makeText(this, "Format de date invalide. Utilisez yyyy-MM-dd", Toast.LENGTH_SHORT).show()
            return
        }

        // Récupérer les données saisies
        val clientId = editTextClientId.text.toString().toLong()
        val selectedChambre = spinnerChambres.selectedItem as? Chambre

        // Vérifier qu'une chambre est sélectionnée
        if (selectedChambre == null) {
            Toast.makeText(this, "Veuillez sélectionner une chambre", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer des objets Client et Chambre
        val client = Client().apply { id = clientId }
        val chambre = Chambre().apply { id = selectedChambre.id }

        val dateDebut = editTextDateDebut.text.toString()
        val dateFin = editTextDateFin.text.toString()
        val preferences = editTextPreferences.text.toString()

        // Créer un objet ReservationInput avec les objets complets
        val reservationInput = ReservationInput(client, chambre, dateDebut, dateFin, preferences)

        // Envoyer la réservation à l'API
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.updateReservation(reservationId, reservationInput)

        call.enqueue(object : Callback<ReservationInput> {
            override fun onResponse(call: Call<ReservationInput>, response: Response<ReservationInput>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddReservationActivity, "Reservation saved successfully", Toast.LENGTH_SHORT).show()
                    finish() // Fermer l'activité après la sauvegarde
                } else {
                    // Afficher le message d'erreur retourné par l'API
                    val errorMessage = "Failed to save reservation: " + response.errorBody()?.string()
                    Toast.makeText(this@AddReservationActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ReservationInput>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Valide le format de la date (yyyy-MM-dd).
     */
    private fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }
}