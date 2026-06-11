package service;

import model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private List<Reservation> reservations = new ArrayList<>();

    public ReservationService() {
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        if (reservation != null) {
            reservations.add(reservation);
        }
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

}