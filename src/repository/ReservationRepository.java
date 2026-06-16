package repository;

import model.Client;
import model.ExtraService;
import model.Agent;
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
        return loadAll(clients, vehicleModels, vehicles, new ArrayList<>());
    }

    public List<Reservation> loadAll(List<Client> clients, List<VehicleModel> vehicleModels, List<Vehicle> vehicles, List<Agent> agents) {
        List<Reservation> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 11 && parts.length != 15 && parts.length != 16) {
                    continue;
                }

                try {
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
                    double rentalBasePrice = price;
                    double reservationExtraServicesPrice = 0;
                    double issueExtraServicesPrice = 0;
                    Agent processedByAgent = null;
                    LocalDateTime processedAt = null;
                    if (parts.length >= 15) {
                        rentalBasePrice = parseOptionalDouble(parts[11], price);
                        reservationExtraServicesPrice = parseOptionalDouble(parts[12], 0);
                        issueExtraServicesPrice = parseOptionalDouble(parts[13], 0);
                        int processedByAgentIndex = parts.length == 16 ? 15 : 14;
                        if (parts.length == 16) {
                            processedAt = parseOptionalDateTime(parts[14]);
                        }
                        Integer processedByAgentId = parseOptionalInteger(parts[processedByAgentIndex]);
                        if (processedByAgentId != null) {
                            processedByAgent = findAgentById(agents, processedByAgentId);
                        }
                        if (parts.length == 15 && processedByAgent != null) {
                            processedAt = createdAt;
                        }
                    }

                    Client client = findClientById(clients, clientId);
                    VehicleModel vehicleModel = findVehicleModelById(vehicleModels, vehicleModelId);
                    Vehicle assignedVehicle = null;
                    if (assignedVehicleId != null) {
                        assignedVehicle = findVehicleById(vehicles, assignedVehicleId);
                    }

                    if (client == null || vehicleModel == null) {
                        continue;
                    }

                    Reservation reservation = new Reservation(id, client, vehicleModel, startDate, endDate, price, rentalBasePrice, reservationExtraServicesPrice, issueExtraServicesPrice, extraServices);
                    reservation.setReservationStatus(reservationStatus);
                    reservation.setAssignedVehicle(assignedVehicle);
                    reservation.setCancellationTime(cancellationTime);
                    reservation.setCreatedAt(createdAt);
                    reservation.setProcessedByAgent(processedByAgent);
                    reservation.setProcessedAt(processedAt);

                    reservations.add(reservation);
                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid reservation row: " + line);
                }
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
            writer.write("id;reservationStatus;clientId;vehicleModelId;startDate;endDate;price;assignedVehicleId;extraServices;cancellationTime;createdAt;rentalBasePrice;reservationExtraServicesPrice;issueExtraServicesPrice;processedAt;processedByAgentId");
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

                String processedAt = "";
                if (reservation.getProcessedAt() != null) {
                    processedAt = reservation.getProcessedAt().toString();
                }

                String processedByAgentId = "";
                if (reservation.getProcessedByAgent() != null) {
                    processedByAgentId = String.valueOf(reservation.getProcessedByAgent().getId());
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
                        createdAt,
                        String.valueOf(reservation.getRentalBasePrice()),
                        String.valueOf(reservation.getReservationExtraServicesPrice()),
                        String.valueOf(reservation.getIssueExtraServicesPrice()),
                        processedAt,
                        processedByAgentId
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

    private Agent findAgentById(List<Agent> agents, int id) {
        if (agents == null) {
            return null;
        }

        for (Agent agent : agents) {
            if (agent.getId() == id) {
                return agent;
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

    private double parseOptionalDouble(String value, double defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(value);
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
