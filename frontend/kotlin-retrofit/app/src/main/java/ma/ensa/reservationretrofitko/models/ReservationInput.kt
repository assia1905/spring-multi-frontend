package ma.ensa.reservationretrofitko.models

class ReservationInput(
    var id: Long = 0, // Valeur par défaut pour id
    var client: Client? = null, // Objet Client (nullable)
    var chambre: Chambre? = null, // Objet Chambre (nullable)
    var dateDebut: String? = null, // Date de début (nullable)
    var dateFin: String? = null, // Date de fin (nullable)
    var preferences: String? = null // Préférences (nullable)
) {
    // Constructeur secondaire pour initialiser les champs sans id
    constructor(client: Client?, chambre: Chambre?, dateDebut: String?, dateFin: String?, preferences: String?) : this(
        0,
        client,
        chambre,
        dateDebut,
        dateFin,
        preferences
    )
}