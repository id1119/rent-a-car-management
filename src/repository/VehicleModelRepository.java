package repository;

import model.VehicleCategory;
import model.VehicleModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VehicleModelRepository {
    private static final String FILE_PATH = "data/vehicle_models.csv";

    public List<VehicleModel> loadAll() {
        List<VehicleModel> vehicleModels = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 4) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0]);
                    String manufacturer = parts[1];
                    String model = parts[2];
                    VehicleCategory vehicleCategory = VehicleCategory.valueOf(parts[3]);

                    VehicleModel vehicleModel = new VehicleModel(id, manufacturer, model, vehicleCategory);
                    vehicleModels.add(vehicleModel);
                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid vehicle model row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return vehicleModels;
    }

    public void saveAll(List<VehicleModel> vehicleModels) {
        if (vehicleModels == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;manufacturer;model;vehicleCategory");
            writer.newLine();

            for (VehicleModel vehicleModel : vehicleModels) {
                String line = String.join(";",
                        String.valueOf(vehicleModel.getId()),
                        vehicleModel.getManufacturer(),
                        vehicleModel.getModel(),
                        vehicleModel.getVehicleCategory().name()
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
