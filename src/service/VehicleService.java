package service;

import model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    private List<Vehicle> vehicles = new ArrayList<>();

    public VehicleService() {
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            vehicles.add(vehicle);
        }
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }
}
