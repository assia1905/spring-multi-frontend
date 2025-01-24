package ma.ensa.reservation.ui.chambres;

import ma.ensa.reservation.models.Chambre;

public interface OnChambreChangeListener {
    void onChambreAdded(Chambre chambre);
    void onChambreUpdated(Chambre chambre);
    void onChambreDeleted(long chambreId);
}