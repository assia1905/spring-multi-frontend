package ma.ensa.reservationvolleyko.ui.chambres

import android.os.Bundle
import android.os.Debug
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import ma.ensa.reservationvolleyko.R
import ma.ensa.reservationvolleyko.api.VolleySingleton
import ma.ensa.reservationvolleyko.models.Chambre
import ma.ensa.reservationvolleyko.models.DispoChambre
import ma.ensa.reservationvolleyko.models.TypeChambre
import org.json.JSONObject

class AddChambreActivity : AppCompatActivity() {

    private lateinit var spinnerTypeChambre: Spinner
    private lateinit var spinnerDispoChambre: Spinner
    private lateinit var editPrix: EditText
    private lateinit var btnSave: Button

    // Liste pour stocker les temps de réponse
    private val responseTimes = mutableListOf<Long>()

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

    private fun getMemoryUsage(): Double {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)

        // Mémoire utilisée en Mo
        val usedMemory = memoryInfo.totalPss / 1024.0
        return usedMemory
    }

    private fun getCpuUsage(): Double {
        val threadCpuTime = Debug.threadCpuTimeNanos() // Temps CPU en nanosecondes
        val cpuUsage = threadCpuTime / 1_000_000.0 // Convertir en millisecondes
        return cpuUsage
    }

    private fun saveChambre() {
        val prixStr = editPrix.text.toString()

        // Validation des champs
        if (prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Mesurer la mémoire avant la requête
            val memoryBefore = getMemoryUsage()
            println("Mémoire utilisée avant la requête: $memoryBefore MB")

            // Mesurer la consommation CPU avant la requête
            val cpuBefore = getCpuUsage()
            println("Consommation CPU avant la requête: $cpuBefore ms")

            // Créer une nouvelle chambre
            val chambre = Chambre().apply {
                typeChambre = spinnerTypeChambre.selectedItem as TypeChambre
                prix = prixStr.toDouble()
                dispoChambre = spinnerDispoChambre.selectedItem as DispoChambre
            }

            // Convertir l'objet Chambre en JSON
            val gson = Gson()
            val jsonBody = gson.toJson(chambre)

            // Mesurer la taille de la requête
            val requestSize = jsonBody.toByteArray().size
            val requestSizeKB = requestSize / 1024.0
            println("Taille de la requête: $requestSizeKB KB")

            // URL de l'endpoint pour ajouter une chambre
            val url = "http://192.168.1.21:8083/api/chambres"

            // Capturer le temps de départ
            val startTime = System.currentTimeMillis()

            // Créer une requête POST avec Volley
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, JSONObject(jsonBody),
                Response.Listener<JSONObject> { response ->
                    // Capturer le temps de fin
                    val endTime = System.currentTimeMillis()
                    val responseTime = endTime - startTime
                    responseTimes.add(responseTime) // Ajouter le temps de réponse à la liste

                    // Calculer le temps moyen de réponse
                    val averageResponseTime = responseTimes.average()
                    println("Temps de réponse actuel: $responseTime ms")
                    println("Temps moyen de réponse: $averageResponseTime ms")

                    // Mesurer la mémoire après la requête
                    val memoryAfter = getMemoryUsage()
                    println("Mémoire utilisée après la requête: $memoryAfter MB")

                    // Mesurer la consommation CPU après la requête
                    val cpuAfter = getCpuUsage()
                    println("Consommation CPU après la requête: $cpuAfter ms")

                    // Mesurer la taille de la réponse
                    val responseJson = response.toString()
                    val responseSize = responseJson.toByteArray().size
                    val responseSizeKB = responseSize / 1024.0
                    println("Taille de la réponse: $responseSizeKB KB")

                    // Gérer la réponse JSON
                    Toast.makeText(this@AddChambreActivity, "Chambre ajoutée", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                },
                Response.ErrorListener { error ->
                    // Capturer le temps de fin en cas d'erreur
                    val endTime = System.currentTimeMillis()
                    val responseTime = endTime - startTime
                    responseTimes.add(responseTime) // Ajouter le temps de réponse en cas d'erreur

                    // Calculer le temps moyen de réponse
                    val averageResponseTime = responseTimes.average()
                    println("Temps de réponse actuel (erreur): $responseTime ms")
                    println("Temps moyen de réponse: $averageResponseTime ms")

                    // Mesurer la mémoire en cas d'erreur
                    val memoryAfter = getMemoryUsage()
                    println("Mémoire utilisée après une erreur: $memoryAfter MB")

                    // Mesurer la consommation CPU en cas d'erreur
                    val cpuAfter = getCpuUsage()
                    println("Consommation CPU après une erreur: $cpuAfter ms")

                    // Gérer l'erreur
                    Toast.makeText(
                        this@AddChambreActivity,
                        "Erreur d'ajout: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Ajouter la requête à la file d'attente
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors de la création de la chambre", Toast.LENGTH_SHORT).show()
        }
    }
}