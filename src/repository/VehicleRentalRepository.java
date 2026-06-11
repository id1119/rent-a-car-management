package repository;

import model.Reservation;
import model.Vehicle;
import model.VehicleRental;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VehicleRentalRepository {
    private static final String FILE_PATH = "data/vehicle_rentals.csv";

    public List<VehicleRental> loadAll(List<Vehicle> vehicles, List<Reservation> reservations) {
        List<VehicleRental> vehicleRentals = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 10) {
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                LocalDate startRentDate = LocalDate.parse(parts[1]);
                LocalDate endRentDate = LocalDate.parse(parts[2]);
                LocalDate actualEndRentDate = parseOptionalDate(parts[3]);
                int startMileage = Integer.parseInt(parts[4]);
                int endMileage = parseOptionalInt(parts[5]);
                double penaltyForBeingLate = Double.parseDouble(parts[6]);
                int vehicleId = Integer.parseInt(parts[7]);
                int reservationId = Integer.parseInt(parts[8]);

                Vehicle vehicle = findVehicleById(vehicles, vehicleId);
                Reservation reservation = findReservationById(reservations, reservationId);

                if (vehicle == null || reservation == null) {
                    continue;
                }

                VehicleRental vehicleRental = new VehicleRental(
                        id,
                        startRentDate,
                        endRentDate,
                        actualEndRentDate,
                        startMileage,
                        endMileage,
                        penaltyForBeingLate,
                        vehicle,
                        reservation
                );
                vehicleRentals.add(vehicleRental);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return vehicleRentals;
    }

    public void saveAll(List<VehicleRental> vehicleRentals) {
        if (vehicleRentals == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;startRentDate;endRentDate;actualEndRentDate;startMileage;endMileage;penaltyForBeingLate;vehicleId;reservationId;notes");
            writer.newLine();

            for (VehicleRental vehicleRental : vehicleRentals) {
                if (vehicleRental.getVehicle() == null || vehicleRental.getReservation() == null) {
                    continue;
                }

                String actualEndRentDate = "";
                if (vehicleRental.getActualEndRentDate() != null) {
                    actualEndRentDate = vehicleRental.getActualEndRentDate().toString();
                }

                String endMileage = "";
                if (vehicleRental.getEndMileage() > 0) {
                    endMileage = String.valueOf(vehicleRental.getEndMileage());
                }

                String line = String.join(";",
                        String.valueOf(vehicleRental.getId()),
                        vehicleRental.getStartRentDate().toString(),
                        vehicleRental.getEndRentDate().toString(),
                        actualEndRentDate,
                        String.valueOf(vehicleRental.getStartMileage()),
                        endMileage,
                        String.valueOf(vehicleRental.getPenaltyForBeingLate()),
                        String.valueOf(vehicleRental.getVehicle().getId()),
                        String.valueOf(vehicleRental.getReservation().getId()),
                        ""
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
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

    private Reservation findReservationById(List<Reservation> reservations, int id) {
        if (reservations == null) {
            return null;
        }

        for (Reservation reservation : reservations) {
            if (reservation.getId() == id) {
                return reservation;
            }
        }

        return null;
    }

    private LocalDate parseOptionalDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private int parseOptionalInt(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }
}
