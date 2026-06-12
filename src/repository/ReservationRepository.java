package repository;

import model.Client;
import model.ExtraService;
import model.Reservation;
import model.ReservationStatus;
import model.Vehicle;
import model.VehicleModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private static final String FILE_PATH = "data/reservations.csv";

    public List<Reservation> loadAll(List<Client> clients, List<VehicleModel> vehicleModels, List<Vehicle> vehicles) {
        List<Reservation> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 11) {
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                ReservationStatus reservationStatus = ReservationStatus.valueOf(parts[1]);
                int clientId = Integer.parseInt(parts[2]);
                int vehicleModelId = Integer.parseInt(parts[3]);
                LocalDate startDate = LocalDate.parse(parts[4]);
                LocalDate endDate = LocalDate.parse(parts[5]);
                double price = Double.parseDouble(parts[6]);
                Integer assignedVehicleId = parseOptionalInteger(parts[7]);
                List<ExtraService> extraServices = parseExtraServices(parts[8]);
                LocalDateTime cancellationTime = parseOptionalDateTime(parts[9]);
                LocalDateTime createdAt = parseOptionalDateTime(parts[10]);

                Client client = findClientById(clients, clientId);
                VehicleModel vehicleModel = findVehicleModelById(vehicleModels, vehicleModelId);
                Vehicle assignedVehicle = null;
                if (assignedVehicleId != null) {
                    assignedVehicle = findVehicleById(vehicles, assignedVehicleId);
                }

                if (client == null || vehicleModel == null) {
                    continue;
                }

                Reservation reservation = new Reservation(id, client, vehicleModel, startDate, endDate, price, extraServices);
                reservation.setReservationStatus(reservationStatus);
                reservation.setAssignedVehicle(assignedVehicle);
                reservation.setCancellationTime(cancellationTime);
                reservation.setCreatedAt(createdAt);

                reservations.add(reservation);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return reservations;
    }

    public void saveAll(List<Reservation> reservations) {
        if (reservations == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;reservationStatus;clientId;vehicleModelId;startDate;endDate;price;assignedVehicleId;extraServices;cancellationTime;createdAt");
            writer.newLine();

            for (Reservation reservation : reservations) {
                String assignedVehicleId = "";
                if (reservation.getAssignedVehicle() != null) {
                    assignedVehicleId = String.valueOf(reservation.getAssignedVehicle().getId());
                }

                String cancellationTime = "";
                if (reservation.getCancellationTime() != null) {
                    cancellationTime = reservation.getCancellationTime().toString();
                }

                String createdAt = "";
                if (reservation.getCreatedAt() != null) {
                    createdAt = reservation.getCreatedAt().toString();
                }

                String line = String.join(";",
                        String.valueOf(reservation.getId()),
                        reservation.getReservationStatus().name(),
                        String.valueOf(reservation.getClient().getId()),
                        String.valueOf(reservation.getVehicleModel().getId()),
                        reservation.getStartDate().toString(),
                        reservation.getEndDate().toString(),
                        String.valueOf(reservation.getPrice()),
                        assignedVehicleId,
                        formatExtraServices(reservation.getExtraServices()),
                        cancellationTime,
                        createdAt
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private Client findClientById(List<Client> clients, int id) {
        if (clients == null) {
            return null;
        }

        for (Client client : clients) {
            if (client.getId() == id) {
                return client;
            }
        }

        return null;
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

    private Vehicle findVehicleById(List<Vehicle> vehicles, int id) {
        if (vehicles == null) {
            return null;
        }

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == id) {
                return vehicle;
            }
        }

        return null;
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }

    private LocalDateTime parseOptionalDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }

    private List<ExtraService> parseExtraServices(String value) {
        List<ExtraService> extraServices = new ArrayList<>();
        if (value == null || value.isEmpty()) {
            return extraServices;
        }

        String[] parts = value.split(",");
        for (String part : parts) {
            if (!part.isEmpty()) {
                extraServices.add(ExtraService.valueOf(part));
            }
        }

        return extraServices;
    }

    private String formatExtraServices(List<ExtraService> extraServices) {
        if (extraServices == null || extraServices.isEmpty()) {
            return "";
        }

        List<String> serviceNames = new ArrayList<>();
        for (ExtraService extraService : extraServices) {
            serviceNames.add(extraService.name());
        }

        return String.join(",", serviceNames);
    }
}
