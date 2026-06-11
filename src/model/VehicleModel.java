package model;

public class VehicleModel {
    private int id;
    private String manufacturer;
    private String model;
    private VehicleCategory vehicleCategory;

    public VehicleModel(int id, String manufacturer, String model, VehicleCategory vehicleCategory) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.vehicleCategory = vehicleCategory;
    }

    public int getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleCategory getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(VehicleCategory vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }
}
