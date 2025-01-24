package ma.ensa.reservationretrofitko.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.reservationretrofitko.R
import ma.ensa.reservationretrofitko.models.Chambre
import ma.ensa.reservationretrofitko.models.Client
import ma.ensa.reservationretrofitko.models.ReservationInput
import ma.ensa.reservationretrofitko.ui.reservations.ReservationDetailActivity

class ReservationAdapter(
    private val reservationList: List<ReservationInput>,
    private val context: Context
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservationInput = reservationList[position]

        // Afficher les détails du client
        val client = reservationInput.client
        holder.textViewClientId.text = if (client != null) {
            "Client: ${client.nom} ${client.prenom}"
        } else {
            "Client: Non disponible"
        }

        // Afficher les détails de la chambre
        val chambre = reservationInput.chambre
        holder.textViewChambreId.text = if (chambre != null) {
            "Chambre: ${chambre.typeChambre} - ${chambre.prix}€"
        } else {
            "Chambre: Non disponible"
        }

        // Afficher les autres détails de la réservation
        holder.textViewDateDebut.text = "Date Début: ${reservationInput.dateDebut}"
        holder.textViewDateFin.text = "Date Fin: ${reservationInput.dateFin}"
        holder.textViewPreferences.text = "Préférences: ${reservationInput.preferences}"

        // Ajouter un écouteur de clics sur l'élément
        holder.itemView.setOnClickListener {
            // Log pour vérifier que l'écouteur de clics est déclenché
            Log.d("ReservationAdapter", "Item clicked, reservation ID: ${reservationInput.id}")

            // Naviguer vers ReservationDetailActivity avec l'ID de la réservation
            val intent = Intent(context, ReservationDetailActivity::class.java)
            intent.putExtra("reservationId", reservationInput.id) // Passer l'ID de la réservation
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return reservationList.size
    }

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewClientId: TextView = itemView.findViewById(R.id.textViewClientId)
        val textViewChambreId: TextView = itemView.findViewById(R.id.textViewChambreId)
        val textViewDateDebut: TextView = itemView.findViewById(R.id.textViewDateDebut)
        val textViewDateFin: TextView = itemView.findViewById(R.id.textViewDateFin)
        val textViewPreferences: TextView = itemView.findViewById(R.id.textViewPreferences)
    }
}