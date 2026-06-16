package service;

import model.Agent;
import model.Reservation;
import model.ReservationStatus;
import model.Vehicle;
import model.VehicleModel;
import model.VehicleRental;
import model.VehicleStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class VehicleRentalService {
    private List<VehicleRental> vehicleRentals = new ArrayList<>();

    public VehicleRentalService() {
    }

    public List<VehicleRental> getVehicleRentals() {
        return vehicleRentals;
    }

    public void addRental(VehicleRental vehicleRental) {
        if (vehicleRental != null) {
            vehicleRentals.add(vehicleRental);
        }
    }

    public void removeRental(VehicleRental vehicleRental) {
        vehicleRentals.remove(vehicleRental);
    }

    public boolean canIssueVehicle(Vehicle vehicle, Reservation reservation) {
        return getIssueVehicleError(vehicle, reservation) == null;
    }

    public String getIssueVehicleError(Vehicle vehicle, Reservation reservation) {
        if (vehicle == null || reservation == null) {
            return "Vehicle and reservation are required.";
        }

        if (!vehicle.isAvailable()) {
            return "Vehicle is not available.";
        }

        if (reservation.getAssignedVehicle() != null) {
            return "Reservation already has an assigned vehicle.";
        }

        if (reservation.getReservationStatus() != ReservationStatus.APPROVED) {
            return "Only approved reservations can be issued.";
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(reservation.getStartDate()) || today.isAfter(reservation.getEndDate())) {
            return "Vehicle can be issued only during the reservation period.";
        }

        if (!hasSameModelId(vehicle.getVehicleModel(), reservation.getVehicleModel())) {
            return "Vehicle model does not match reservation model.";
        }

        return null;
    }

    public VehicleRental issueVehicle(int id, Vehicle vehicle, Reservation reservation, int startMileage){
        return issueVehicle(id, vehicle, reservation, startMileage, null);
    }

    public VehicleRental issueVehicle(int id, Vehicle vehicle, Reservation reservation, int startMileage, Agent agent){
        if (!canIssueVehicle(vehicle, reservation)){
            return null;
        }
        reservation.setAssignedVehicle(vehicle);
        vehicle.setMileage(startMileage);
        vehicle.setVehicleStatus(VehicleStatus.RENTED);
        LocalDate endRentDate = reservation.getEndDate();
        VehicleRental rentedVehicle = new VehicleRental(id, LocalDate.now(), endRentDate, startMileage, vehicle, reservation, agent);
        addRental(rentedVehicle);
        return rentedVehicle;
    }

    public List<VehicleRental> findActiveRentals() {
        List<VehicleRental> activeRentals = new ArrayList<>();
        for (VehicleRental vehicleRental : vehicleRentals) {
            if (vehicleRental.getActualEndRentDate() == null) {
                activeRentals.add(vehicleRental);
            }
        }
        return activeRentals;
    }

    public boolean returnVehicle(VehicleRental vehicleRental, int endMileage, LocalDate actualEndRentDate, double penaltyPerDay) {
        String error = getReturnVehicleError(vehicleRental, endMileage, actualEndRentDate);
        if (error != null) {
            return false;
        }

        vehicleRental.setActualEndRentDate(actualEndRentDate);
        vehicleRental.setEndMileage(endMileage);

        double penalty = calculateLatePenalty(vehicleRental, actualEndRentDate, penaltyPerDay);
        vehicleRental.setPenaltyForBeingLate(penalty);

        Vehicle vehicle = vehicleRental.getVehicle();
        vehicle.setMileage(endMileage);
        vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);

        if (penalty > 0 && vehicleRental.getReservation() != null && vehicleRental.getReservation().getClient() != null) {
            int lateReturnsCount = vehicleRental.getReservation().getClient().getLateReturnsCount();
            vehicleRental.getReservation().getClient().setLateReturnsCount(lateReturnsCount + 1);
        }

        return true;
    }

    public String getReturnVehicleError(VehicleRental vehicleRental, int endMileage, LocalDate actualEndRentDate) {
        if (vehicleRental == null || vehicleRental.getVehicle() == null || actualEndRentDate == null) {
            return "Rental, vehicle and return date are required.";
        }

        if (vehicleRental.getActualEndRentDate() != null) {
            return "Vehicle has already been returned.";
        }

        if (endMileage < vehicleRental.getStartMileage()) {
            return "End mileage cannot be lower than start mileage.";
        }

        return null;
    }

    private double calculateLatePenalty(VehicleRental vehicleRental, LocalDate actualEndRentDate, double penaltyPerDay) {
        if (!actualEndRentDate.isAfter(vehicleRental.getEndRentDate())) {
            return 0;
        }

        long lateDays = ChronoUnit.DAYS.between(vehicleRental.getEndRentDate(), actualEndRentDate);
        return lateDays * penaltyPerDay;
    }

    private boolean hasSameModelId(VehicleModel first, VehicleModel second) {
        if (first == null || second == null) {
            return false;
        }
        return first.getId() == second.getId();
    }
}
