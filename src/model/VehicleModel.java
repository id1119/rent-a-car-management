package model;

public class VehicleModel {
    private String manufacturer;
    private String model;
    private VehicleCategory vehicleCategory;

    public VehicleModel(String manufacturer, String model, VehicleCategory vehicleCategory) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.vehicleCategory = vehicleCategory;
    }

    public String getManufacturer() {
        return manufacturer;
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
