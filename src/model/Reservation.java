package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private ReservationStatus reservationStatus;
    private Client client;
    private VehicleModel vehicleModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
    private Vehicle assignedVehicle = null;
    private List<ExtraService> extraServices = new ArrayList<ExtraService>();
    private LocalDateTime cancellationTime;


    public Reservation(Client client, VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate, double price, List<ExtraService> extraServices) {
        this.reservationStatus = ReservationStatus.PENDING;
        this.client = client;
        this.vehicleModel = vehicleModel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        if (extraServices != null) {
            this.extraServices = extraServices;
        }
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(Vehicle assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    public List<ExtraService> getExtraServices() {
        return extraServices;
    }

    public void setExtraServices(List<ExtraService> extraServices) {
        if (extraServices == null) {
            extraServices = new ArrayList<>();
        }
        this.extraServices = extraServices;
    }

    public boolean addExtraService(ExtraService extraService) {
        if (extraServices == null) {
            extraServices = new ArrayList<>();
        }
        if (extraService == null) {
            return false;
        }
        if (extraServices.contains(extraService)) {
            return false;
        } else {
            extraServices.add(extraService);
            return true;
        }
    }

    public LocalDateTime getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(LocalDateTime cancellationTime) {
        this.cancellationTime = cancellationTime;
    }
}
