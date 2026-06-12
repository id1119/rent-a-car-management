package service;

import model.Client;
import model.ExtraService;
import model.Reservation;
import model.ReservationStatus;
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

        return hasAvailableVehicle(vehicleModel, startDate, endDate);
    }

    public boolean hasAvailableVehicle(VehicleModel vehicleModel) {
        if (vehicleModel == null || vehicleService == null) {
            return false;
        }
        List<Vehicle> availableVehicles = vehicleService.findAvailableVehiclesByModel(vehicleModel);
        return !availableVehicles.isEmpty();
    }

    public boolean hasAvailableVehicle(VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate) {
        return !findAvailableVehicles(vehicleModel, startDate, endDate).isEmpty();
    }

    public List<Vehicle> findAvailableVehicles(VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate) {
        return findAvailableVehicles(vehicleModel, startDate, endDate, 0);
    }

    private List<Vehicle> findAvailableVehicles(VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate, int ignoredReservationId) {
        List<Vehicle> availableVehicles = new ArrayList<>();
        if (vehicleModel == null || startDate == null || endDate == null || vehicleService == null || endDate.isBefore(startDate)) {
            return availableVehicles;
        }

        List<Integer> blockedVehicleIds = new ArrayList<>();
        int unassignedBlockingReservations = 0;

        for (Reservation reservation : reservations) {
            if (reservation.getId() == ignoredReservationId) {
                continue;
            }

            if (!isBlockingReservation(reservation)) {
                continue;
            }

            if (!isSameVehicleModel(reservation.getVehicleModel(), vehicleModel)) {
                continue;
            }

            if (!datesOverlap(startDate, endDate, reservation.getStartDate(), reservation.getEndDate())) {
                continue;
            }

            Vehicle assignedVehicle = reservation.getAssignedVehicle();
            if (assignedVehicle == null) {
                unassignedBlockingReservations++;
            } else {
                blockedVehicleIds.add(assignedVehicle.getId());
            }
        }

        List<Vehicle> vehiclesByModel = vehicleService.findAvailableVehiclesByModel(vehicleModel);
        for (Vehicle vehicle : vehiclesByModel) {
            if (!blockedVehicleIds.contains(vehicle.getId())) {
                availableVehicles.add(vehicle);
            }
        }

        while (unassignedBlockingReservations > 0 && !availableVehicles.isEmpty()) {
            availableVehicles.remove(0);
            unassignedBlockingReservations--;
        }

        return availableVehicles;
    }

    private boolean isBlockingReservation(Reservation reservation) {
        if (reservation == null || reservation.getReservationStatus() == null) {
            return false;
        }

        return reservation.getReservationStatus() == ReservationStatus.PENDING ||
                reservation.getReservationStatus() == ReservationStatus.APPROVED;
    }

    private boolean isSameVehicleModel(VehicleModel first, VehicleModel second) {
        if (first == null || second == null) {
            return false;
        }
        return first.getId() == second.getId();
    }

    private boolean datesOverlap(LocalDate firstStart, LocalDate firstEnd, LocalDate secondStart, LocalDate secondEnd) {
        return !firstEnd.isBefore(secondStart) && !secondEnd.isBefore(firstStart);
    }

    public Reservation createReservationRequest(int id, Client client, VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate, double price, List<ExtraService> extraServices) {
        if (!canCreateReservation(client, vehicleModel, startDate, endDate)) {
            return null;
        }

        Reservation reservation = new Reservation(id, client, vehicleModel, startDate, endDate, price, extraServices);
        addReservation(reservation);
        return reservation;
    }

    public boolean approveReservation(Reservation reservation) {
        if (reservation == null || reservation.getReservationStatus() != ReservationStatus.PENDING) {
            return false;
        }

        if (findAvailableVehicles(reservation.getVehicleModel(), reservation.getStartDate(), reservation.getEndDate(), reservation.getId()).isEmpty()) {
            return false;
        }

        reservation.setReservationStatus(ReservationStatus.APPROVED);
        return true;
    }

    public boolean rejectReservation(Reservation reservation) {
        if (reservation == null || reservation.getReservationStatus() != ReservationStatus.PENDING) {
            return false;
        }

        reservation.setReservationStatus(ReservationStatus.REJECTED);
        return true;
    }

}
