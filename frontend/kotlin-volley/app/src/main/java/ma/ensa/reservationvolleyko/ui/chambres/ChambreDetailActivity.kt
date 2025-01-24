package ma.ensa.reservationvolleyko.ui.chambres

import android.app.AlertDialog
import android.content.Intent
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
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import ma.ensa.reservationvolleyko.R
import ma.ensa.reservationvolleyko.api.VolleySingleton
import ma.ensa.reservationvolleyko.models.Chambre
import ma.ensa.reservationvolleyko.models.DispoChambre
import ma.ensa.reservationvolleyko.models.TypeChambre
import org.json.JSONException
import org.json.JSONObject

class ChambreDetailActivity : AppCompatActivity() {

    private lateinit var spinnerTypeChambre: Spinner
    private lateinit var spinnerDispoChambre: Spinner
    private lateinit var editPrix: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private var chambre: Chambre? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chambre_detail)

        // Initialisation des vues
        spinnerTypeChambre = findViewById(R.id.spinnerTypeChambre)
        editPrix = findViewById(R.id.editPrix)
        spinnerDispoChambre = findViewById(R.id.spinnerDispoChambre)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)

        // Configurer les Spinners
        setupSpinners()

        // Récupérer l'ID de la chambre depuis l'intent
        val chambreId = intent.getLongExtra("CHAMBRE_ID", -1)
        if (chambreId == -1L) {
            Toast.makeText(this, "Chambre introuvable", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            loadChambreDetails(chambreId)
        }

        // Bouton de mise à jour
        btnUpdate.setOnClickListener { updateChambre() }

        // Bouton de suppression
        btnDelete.setOnClickListener { deleteChambre() }
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

    private fun getCpuUsagePercentage(cpuTimeMs: Double, elapsedTimeMs: Long): Double {
        return (cpuTimeMs / elapsedTimeMs) * 100.0
    }

    private fun loadChambreDetails(chambreId: Long) {
        val url = "http://192.168.1.21:8083/api/chambres/$chambreId"

        // Mesurer la mémoire avant la requête
        val memoryBefore = getMemoryUsage()
        println("Mémoire utilisée avant la requête: $memoryBefore MB")

        // Mesurer la consommation CPU avant la requête
        val cpuBefore = getCpuUsage()
        println("Consommation CPU avant la requête: $cpuBefore ms")

        // Capturer le temps de départ
        val startTime = System.currentTimeMillis()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                try {
                    // Capturer le temps de fin
                    val endTime = System.currentTimeMillis()
                    val elapsedTimeMs = endTime - startTime

                    // Mesurer la consommation CPU après la requête
                    val cpuAfter = getCpuUsage()
                    val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs)
                    println("Consommation CPU après la requête: $cpuUsagePercentage %")

                    // Mesurer la mémoire après la requête
                    val memoryAfter = getMemoryUsage()
                    println("Mémoire utilisée après la requête: $memoryAfter MB")

                    // Mesurer la taille des données reçues
                    val jsonString = response.toString()
                    val sizeInBytes = jsonString.toByteArray().size // Taille en octets
                    val sizeInKB = sizeInBytes / 1024.0 // Convertir en KB
                    println("Taille des données reçues : $sizeInKB KB")

                    // Mesurer le temps de réponse
                    val responseTime = endTime - startTime
                    println("Temps de réponse: $responseTime ms")

                    // Désérialiser la réponse JSON en un objet Chambre
                    val gson = Gson()
                    chambre = gson.fromJson(response.toString(), Chambre::class.java)

                    // Mettre à jour les vues avec les détails de la chambre
                    spinnerTypeChambre.setSelection(TypeChambre.values().indexOf(chambre?.typeChambre))
                    editPrix.setText(chambre?.prix.toString())
                    spinnerDispoChambre.setSelection(DispoChambre.values().indexOf(chambre?.dispoChambre))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@ChambreDetailActivity, "Erreur lors de la lecture de la réponse JSON", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            Response.ErrorListener { error ->
                // Capturer le temps de fin en cas d'erreur
                val endTime = System.currentTimeMillis()
                val elapsedTimeMs = endTime - startTime

                // Mesurer la consommation CPU en cas d'erreur
                val cpuAfter = getCpuUsage()
                val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs)
                println("Consommation CPU après une erreur: $cpuUsagePercentage %")

                // Mesurer la mémoire en cas d'erreur
                val memoryAfter = getMemoryUsage()
                println("Mémoire utilisée après une erreur: $memoryAfter MB")

                Toast.makeText(this@ChambreDetailActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun updateChambre() {
        val prixStr = editPrix.text.toString()

        if (prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            chambre?.apply {
                typeChambre = spinnerTypeChambre.selectedItem as TypeChambre
                prix = prixStr.toDouble()
                dispoChambre = spinnerDispoChambre.selectedItem as DispoChambre
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir l'objet Chambre en JSON
        val gson = Gson()
        val jsonBody = gson.toJson(chambre)

        try {
            val jsonObject = JSONObject(jsonBody)
            val url = "http://192.168.1.21:8083/api/chambres/${chambre?.id}"

            // Mesurer la mémoire avant la requête
            val memoryBefore = getMemoryUsage()
            println("Mémoire utilisée avant la requête: $memoryBefore MB")

            // Mesurer la consommation CPU avant la requête
            val cpuBefore = getCpuUsage()
            println("Consommation CPU avant la requête: $cpuBefore ms")

            // Capturer le temps de départ
            val startTime = System.currentTimeMillis()

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.PUT, url, jsonObject,
                Response.Listener<JSONObject> { response ->
                    // Capturer le temps de fin
                    val endTime = System.currentTimeMillis()
                    val elapsedTimeMs = endTime - startTime

                    // Mesurer la consommation CPU après la requête
                    val cpuAfter = getCpuUsage()
                    val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs)
                    println("Consommation CPU après la requête: $cpuUsagePercentage %")

                    // Mesurer la mémoire après la requête
                    val memoryAfter = getMemoryUsage()
                    println("Mémoire utilisée après la requête: $memoryAfter MB")

                    // Mesurer la taille des données reçues
                    val jsonString = response.toString()
                    val sizeInBytes = jsonString.toByteArray().size // Taille en octets
                    val sizeInKB = sizeInBytes / 1024.0 // Convertir en KB
                    println("Taille des données reçues : $sizeInKB KB")

                    // Mesurer le temps de réponse
                    val responseTime = endTime - startTime
                    println("Temps de réponse: $responseTime ms")

                    Toast.makeText(this@ChambreDetailActivity, "Chambre mise à jour", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                },
                Response.ErrorListener { error ->
                    // Capturer le temps de fin en cas d'erreur
                    val endTime = System.currentTimeMillis()
                    val elapsedTimeMs = endTime - startTime

                    // Mesurer la consommation CPU en cas d'erreur
                    val cpuAfter = getCpuUsage()
                    val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs)
                    println("Consommation CPU après une erreur: $cpuUsagePercentage %")

                    // Mesurer la mémoire en cas d'erreur
                    val memoryAfter = getMemoryUsage()
                    println("Mémoire utilisée après une erreur: $memoryAfter MB")

                    Toast.makeText(this@ChambreDetailActivity, "Erreur de mise à jour: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors de la création du JSON", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteChambre() {
        AlertDialog.Builder(this)
            .setTitle("Supprimer la chambre")
            .setMessage("Êtes-vous sûr de vouloir supprimer cette chambre ?")
            .setPositiveButton("Oui") { dialog, which ->
                val url = "http://192.168.1.21:8083/api/chambres/${chambre?.id}"

                // Mesurer la mémoire avant la requête
                val memoryBefore = getMemoryUsage()
                println("Mémoire utilisée avant la requête: $memoryBefore MB")

                // Mesurer la consommation CPU avant la requête
                val cpuBefore = getCpuUsage()
                println("Consommation CPU avant la requête: $cpuBefore ms")

                // Capturer le temps de départ
                val startTime = System.currentTimeMillis()

                val stringRequest = StringRequest(
                    Request.Method.DELETE, url,
                    Response.Listener<String> { response ->
                        // Capturer le temps de fin
                        val endTime = System.currentTimeMillis()
                        val elapsedTimeMs = endTime - startTime

                        // Mesurer la consommation CPU après la requête
                        val cpuAfter = getCpuUsage()
                        val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs)
                        println("Consommation CPU après la requête: $cpuUsagePercentage %")

                        // Mesurer la mémoire après la requête
                        val memoryAfter = getMemoryUsage()
                        println("Mémoire utilisée après la requête: $memoryAfter MB")

                        // Mesurer le temps de réponse
                        val responseTime = endTime - startTime
                        println("Temps de réponse: $responseTime ms")

                        // Si la réponse est vide, afficher un message de succès
                        Toast.makeText(this@ChambreDetailActivity, "Chambre supprimée", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    Response.ErrorListener { error ->
                        // Capturer le temps de fin en cas d'erreur
                        val endTime = System.currentTimeMillis()
                        val elapsedTimeMs = endTime - startTime

                        // Mesurer la consommation CPU en cas d'erreur
                        val cpuAfter = getCpuUsage()
                        val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter, elapsedTimeMs)
                        println("Consommation CPU après une erreur: $cpuUsagePercentage %")

                        // Mesurer la mémoire en cas d'erreur
                        val memoryAfter = getMemoryUsage()
                        println("Mémoire utilisée après une erreur: $memoryAfter MB")

                        if (error.networkResponse != null) {
                            val statusCode = error.networkResponse.statusCode
                            when (statusCode) {
                                204 -> {
                                    // Réponse vide (No Content)
                                    Toast.makeText(this@ChambreDetailActivity, "Chambre supprimée", Toast.LENGTH_SHORT).show()
                                    setResult(RESULT_OK)
                                    finish()
                                }
                                400 -> {
                                    // Erreur de contrainte de clé étrangère
                                    Toast.makeText(this@ChambreDetailActivity, "La chambre est réservée et ne peut pas être supprimée.", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    // Autre erreur
                                    Toast.makeText(this@ChambreDetailActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // Erreur réseau
                            Toast.makeText(this@ChambreDetailActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
            }
            .setNegativeButton("Non", null)
            .show()
    }
}