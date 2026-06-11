package model;

public class Vehicle {
    private String registration;
    private VehicleStatus vehicleStatus;
    private int mileage;
    private VehicleModel vehicleModel;

    public Vehicle(String registration, VehicleStatus vehicleStatus, int mileage, VehicleModel vehicleModel) {
        this.registration = registration;
        this.vehicleStatus = vehicleStatus;
        this.mileage = mileage;
        this.vehicleModel = vehicleModel;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public boolean isAvailable() {
        return this.vehicleStatus == VehicleStatus.AVAILABLE;

    }

}
