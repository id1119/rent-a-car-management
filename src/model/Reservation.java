package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int id;
    private ReservationStatus reservationStatus;
    private Client client;
    private VehicleModel vehicleModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
    private double rentalBasePrice;
    private double reservationExtraServicesPrice;
    private double issueExtraServicesPrice;
    private Vehicle assignedVehicle = null;
    private List<ExtraService> extraServices = new ArrayList<ExtraService>();
    private LocalDateTime cancellationTime;
    private LocalDateTime createdAt;
    private Agent processedByAgent;
    private LocalDateTime processedAt;


    public Reservation(int id, Client client, VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate, double price, List<ExtraService> extraServices) {
        this(id, client, vehicleModel, startDate, endDate, price, price, 0, 0, extraServices);
    }

    public Reservation(int id, Client client, VehicleModel vehicleModel, LocalDate startDate, LocalDate endDate,
                       double price, double rentalBasePrice, double reservationExtraServicesPrice,
                       double issueExtraServicesPrice, List<ExtraService> extraServices) {
        this.id = id;
        this.reservationStatus = ReservationStatus.PENDING;
        this.client = client;
        this.vehicleModel = vehicleModel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.rentalBasePrice = rentalBasePrice;
        this.reservationExtraServicesPrice = reservationExtraServicesPrice;
        this.issueExtraServicesPrice = issueExtraServicesPrice;
        this.createdAt = LocalDateTime.now();
        if (extraServices != null) {
            this.extraServices = extraServices;
        }
    }

    public int getId() {
        return id;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getRentalBasePrice() {
        return rentalBasePrice;
    }

    public void setRentalBasePrice(double rentalBasePrice) {
        this.rentalBasePrice = rentalBasePrice;
    }

    public double getReservationExtraServicesPrice() {
        return reservationExtraServicesPrice;
    }

    public void setReservationExtraServicesPrice(double reservationExtraServicesPrice) {
        this.reservationExtraServicesPrice = reservationExtraServicesPrice;
    }

    public double getIssueExtraServicesPrice() {
        return issueExtraServicesPrice;
    }

    public void setIssueExtraServicesPrice(double issueExtraServicesPrice) {
        this.issueExtraServicesPrice = issueExtraServicesPrice;
    }

    public double getTotalExtraServicesPrice() {
        return reservationExtraServicesPrice + issueExtraServicesPrice;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Agent getProcessedByAgent() {
        return processedByAgent;
    }

    public void setProcessedByAgent(Agent processedByAgent) {
        this.processedByAgent = processedByAgent;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
