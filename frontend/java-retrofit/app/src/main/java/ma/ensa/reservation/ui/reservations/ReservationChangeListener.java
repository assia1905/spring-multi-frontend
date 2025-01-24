package ma.ensa.reservation.ui.reservations;
public interface ReservationChangeListener {
    void onReservationAdded();
    void onReservationUpdated();
    void onReservationDeleted();
}