package ma.ensa.reservationretrofitko.api

class AuthResponse {
    var token: String? = null
        get() = field // Getter personnalisé
        set(value) {  // Setter personnalisé
            field = value
        }

    var message: String? = null
        get() = field // Getter personnalisé
        set(value) {  // Setter personnalisé
            field = value
        }
}