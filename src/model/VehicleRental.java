package model;

import java.time.LocalDate;

public class VehicleRental {
    private int id;
    private LocalDate startRentDate;
    private LocalDate endRentDate;
    private LocalDate actualEndRentDate;
    private int startMileage;
    private int endMileage;
    private double penaltyForBeingLate;
    private Vehicle vehicle;
    private Reservation reservation;

    public VehicleRental(int id, LocalDate startRentDate, LocalDate endRentDate, LocalDate actualEndRentDate,
                         int startMileage, int endMileage, double penaltyForBeingLate,
                         Vehicle vehicle, Reservation reservation) {
        this.id = id;
        this.startRentDate = startRentDate;
        this.endRentDate = endRentDate;
        this.actualEndRentDate = actualEndRentDate;
        this.startMileage = startMileage;
        this.endMileage = endMileage;
        this.penaltyForBeingLate = penaltyForBeingLate;
        this.vehicle = vehicle;
        this.reservation = reservation;
    }

    public VehicleRental(int id, LocalDate startRentDate, LocalDate endRentDate, int startMileage, Vehicle vehicle, Reservation reservation) {
        this.id = id;
        this.startRentDate = startRentDate;
        this.endRentDate = endRentDate;
        this.startMileage = startMileage;
        this.vehicle = vehicle;
        this.reservation = reservation;
    }

    public int getId() {
        return id;
    }

    public LocalDate getStartRentDate() {
        return startRentDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartRentDate(LocalDate startRentDate) {
        this.startRentDate = startRentDate;
    }

    public LocalDate getEndRentDate() {
        return endRentDate;
    }

    public void setEndRentDate(LocalDate endRentDate) {
        this.endRentDate = endRentDate;
    }

    public LocalDate getActualEndRentDate() {
        return actualEndRentDate;
    }

    public void setActualEndRentDate(LocalDate actualEndRentDate) {
        this.actualEndRentDate = actualEndRentDate;
    }

    public int getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(int startMileage) {
        this.startMileage = startMileage;
    }

    public int getEndMileage() {
        return endMileage;
    }

    public void setEndMileage(int endMileage) {
        this.endMileage = endMileage;
    }

    public double getPenaltyForBeingLate() {
        return penaltyForBeingLate;
    }

    public void setPenaltyForBeingLate(double penaltyForBeingLate) {
        this.penaltyForBeingLate = penaltyForBeingLate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public boolean isLate() {
        if (actualEndRentDate != null) {
            return actualEndRentDate.isAfter(endRentDate);
        }
        return LocalDate.now().isAfter(endRentDate);
    }
}
