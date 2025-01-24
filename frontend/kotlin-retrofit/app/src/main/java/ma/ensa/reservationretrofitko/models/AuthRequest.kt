package ma.ensa.reservationretrofitko.models

class AuthRequest {
    var nom: String? = null
        get() = field
        set(value) {
            field = value
        }

    var prenom: String? = null
        get() = field
        set(value) {
            field = value
        }

    var email: String? = null
        get() = field
        set(value) {
            field = value
        }

    var telephone: String? = null
        get() = field
        set(value) {
            field = value
        }

    var password: String? = null
        get() = field
        set(value) {
            field = value
        }

    // Constructeur par défaut
    constructor()

    // Constructeur avec email et password
    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }
}