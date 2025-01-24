package ma.ensa.reservationretrofitko.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

class DataSizeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Mesurer la taille de la requête
        val requestSize = request.body?.contentLength() ?: 0
        val requestSizeKB = requestSize / 1024.0

        // Capturer le temps de départ
        val startTime = System.currentTimeMillis()

        // Exécuter la requête
        val response = chain.proceed(request)

        // Capturer le temps de fin
        val endTime = System.currentTimeMillis()
        val responseTime = endTime - startTime

        // Mesurer la taille de la réponse
        val responseBody = response.body
        val responseSize = responseBody?.contentLength() ?: 0
        val responseSizeKB = responseSize / 1024.0

        // Afficher les résultats
        println("Méthode: ${request.method}")
        println("URL: ${request.url}")
        println("Taille de la requête: $requestSizeKB KB")
        println("Taille de la réponse: $responseSizeKB KB")
        println("Temps de réponse: $responseTime ms")

        return response
        }
}