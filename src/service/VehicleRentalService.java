package service;

import model.Reservation;
import model.Vehicle;
import model.VehicleRental;
import model.VehicleStatus;

import java.time.LocalDate;
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
        if (vehicle == null || reservation == null) {
            return false;
        }

        if (!vehicle.isAvailable()) {
            return false;
        }

        if (reservation.getAssignedVehicle() != null) {
            return false;
        }

        return vehicle.getVehicleModel().equals(reservation.getVehicleModel());
    }

    public VehicleRental issueVehicle(Vehicle vehicle, Reservation reservation, int startMileage){
        if (!canIssueVehicle(vehicle, reservation)){
            return null;
        }
        reservation.setAssignedVehicle(vehicle);
        vehicle.setMileage(startMileage);
        vehicle.setVehicleStatus(VehicleStatus.RENTED);
        LocalDate endRentDate = reservation.getEndDate();
        VehicleRental rentedVehicle = new VehicleRental(0, LocalDate.now(), endRentDate, startMileage, vehicle, reservation);
        addRental(rentedVehicle);
        return rentedVehicle;
    }
}
