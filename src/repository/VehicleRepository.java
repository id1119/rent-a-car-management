package repository;

import model.Vehicle;
import model.VehicleModel;
import model.VehicleStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    private static final String FILE_PATH = "data/vehicles.csv";

    public List<Vehicle> loadAll(List<VehicleModel> vehicleModels) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 5) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0]);
                    String registration = parts[1];
                    VehicleStatus vehicleStatus = VehicleStatus.valueOf(parts[2]);
                    int mileage = Integer.parseInt(parts[3]);
                    int vehicleModelId = Integer.parseInt(parts[4]);
                    VehicleModel vehicleModel = findVehicleModelById(vehicleModels, vehicleModelId);

                    if (vehicleModel == null) {
                        continue;
                    }

                    Vehicle vehicle = new Vehicle(id, registration, vehicleStatus, mileage, vehicleModel);
                    vehicles.add(vehicle);
                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid vehicle row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return vehicles;
    }

    public void saveAll(List<Vehicle> vehicles) {
        if (vehicles == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;registration;vehicleStatus;mileage;vehicleModelId");
            writer.newLine();

            for (Vehicle vehicle : vehicles) {
                if (vehicle.getVehicleModel() == null) {
                    continue;
                }

                String line = String.join(";",
                        String.valueOf(vehicle.getId()),
                        vehicle.getRegistration(),
                        vehicle.getVehicleStatus().name(),
                        String.valueOf(vehicle.getMileage()),
                        String.valueOf(vehicle.getVehicleModel().getId())
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private VehicleModel findVehicleModelById(List<VehicleModel> vehicleModels, int id) {
        if (vehicleModels == null) {
            return null;
        }

        for (VehicleModel vehicleModel : vehicleModels) {
            if (vehicleModel.getId() == id) {
                return vehicleModel;
            }
        }

        return null;
    }
}
