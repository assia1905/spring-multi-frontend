package ma.ensa.reservationretrofitko.models

class Chambre {
    var id: Long? = null
        get() = field
        set(value) {
            field = value
        }

    var typeChambre: TypeChambre? = null
        get() = field
        set(value) {
            field = value
        }

    var prix: Double = 0.0
        get() = field
        set(value) {
            field = value
        }

    var dispoChambre: DispoChambre? = null
        get() = field
        set(value) {
            field = value
        }

    // Constructeur par défaut
    constructor()

    // Constructeur avec tous les champs
    constructor(id: Long?, typeChambre: TypeChambre?, prix: Double, dispoChambre: DispoChambre?) {
        this.id = id
        this.typeChambre = typeChambre
        this.prix = prix
        this.dispoChambre = dispoChambre
    }

    // Méthode toString pour afficher le type et le prix
    override fun toString(): String {
        return "${typeChambre} - ${prix}€" // Affiche le type et le prix dans le Spinner
    }
}