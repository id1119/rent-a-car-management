package service;

import model.VehicleRental;

import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private List<VehicleRental> vehicleRentals = new ArrayList<>();

    public RentalService() {
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
}
