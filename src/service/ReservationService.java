package service;

import model.Client;
import model.Reservation;
import model.Vehicle;
import model.VehicleModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private List<Reservation> reservations = new ArrayList<>();
    private VehicleService vehicleService;

    public ReservationService(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public VehicleService getVehicleService() {
        return vehicleService;
    }

    public void addReservation(Reservation reservation) {
        if (reservation != null) {
            reservations.add(reservation);
        }
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    public boolean canCreateReservation(Client client, VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate) {
        if (client == null || vehicleModel == null || startDate == null || endDate == null) {
            return false;
        }

        if (endDate.isBefore(startDate)) {
            return false;
        }

        if (startDate.isBefore(LocalDate.now().plusDays(1))) {
            return false;
        }

        if (!client.canRent()) {
            return false;
        }

        if (client.getSubscription() == null || !client.getSubscription().isActive()) {
            return false;
        }

        return hasAvailableVehicle(vehicleModel);
    }

    public boolean hasAvailableVehicle(VehicleModel vehicleModel) {
        if (vehicleModel == null || vehicleService == null) {
            return false;
        }
        List<Vehicle> availableVehicles = vehicleService.findAvailableVehiclesByModel(vehicleModel);
        return !availableVehicles.isEmpty();
    }

}