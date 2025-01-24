package ma.ensa.reservationretrofitko.api

import android.os.Debug
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class ApiClient {
    companion object {
        private const val BASE_URL = "http://192.168.1.21:8083/" // Remplacez par l'URL de votre backend
        private var retrofit: Retrofit? = null

        fun getClient(): Retrofit {
            if (retrofit == null) {
                // Créer un interceptor pour mesurer la taille des données et le temps de réponse
                val dataSizeInterceptor = Interceptor { chain ->
                    val request = chain.request()

                    // Mesurer la taille des données envoyées
                    val requestSize = request.body?.contentLength() ?: 0
                    val requestSizeKB = requestSize / 1024.0

                    // Capturer le temps de départ
                    val startTime = System.currentTimeMillis()

                    // Mesurer la mémoire avant la requête
                    val memoryBefore = getMemoryUsage()

                    // Mesurer la consommation CPU avant la requête
                    val cpuBefore = getCpuUsage()

                    // Exécuter la requête
                    val response = chain.proceed(request)

                    // Capturer le temps de fin
                    val endTime = System.currentTimeMillis()
                    val responseTime = endTime - startTime

                    // Mesurer la taille des données reçues
                    val responseSize = response.body?.contentLength() ?: 0
                    val responseSizeKB = responseSize / 1024.0

                    // Mesurer la mémoire après la requête
                    val memoryAfter = getMemoryUsage()

                    // Mesurer la consommation CPU après la requête
                    val cpuAfter = getCpuUsage()

                    // Calculer l'utilisation du CPU en pourcentage
                    val cpuUsagePercentage = getCpuUsagePercentage(cpuAfter - cpuBefore, responseTime)

                    // Afficher les résultats
                    println("Méthode: ${request.method}")
                    println("URL: ${request.url}")
                    println("Taille de la requête: $requestSizeKB KB")
                    println("Taille de la réponse: $responseSizeKB KB")
                    println("Temps de réponse: $responseTime ms")
                    println("Mémoire utilisée avant la requête: $memoryBefore MB")
                    println("Mémoire utilisée après la requête: $memoryAfter MB")
                    println("Consommation CPU: $cpuUsagePercentage %")

                    response
                }

                // Créer un interceptor pour le logging
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY // Niveau BODY pour voir les détails des requêtes
                }

                // Configurer OkHttpClient avec les interceptors
                val httpClient = OkHttpClient.Builder().apply {
                    addInterceptor(dataSizeInterceptor) // Ajouter l'intercepteur personnalisé
                    addInterceptor(loggingInterceptor) // Ajouter le logging interceptor
                }

                // Configurer Retrofit avec OkHttpClient personnalisé
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
            }
            return retrofit!!
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
        }
}