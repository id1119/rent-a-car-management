package service;

import model.Vehicle;
import model.VehicleModel;

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

    public List<Vehicle> findVehiclesByModel(VehicleModel vehicleModel) {
        if (vehicleModel == null) {
            return new ArrayList<>();
        }
        List<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleModel().equals(vehicleModel)) {
                availableVehicles.add(vehicle);
            }
        }
        return availableVehicles;
    }

    public List<Vehicle> findAvailableVehiclesByModel(VehicleModel vehicleModel) {
        List<Vehicle> vehiclesByModel = findVehiclesByModel(vehicleModel);
        List<Vehicle> availableVehicles = new ArrayList<>();

        for (Vehicle vehicle : vehiclesByModel) {
            if (vehicle.isAvailable()) {
                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }
}
