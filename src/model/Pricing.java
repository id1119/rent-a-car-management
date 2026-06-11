package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Pricing {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double annualSubscription;
    private double penaltyPerDay;
    private Map<VehicleCategory, Double> categoryPrices = new HashMap<>();
    private Map<ExtraService, Double> extraServicesPrices = new HashMap<>();
    private Map<ClientCategory, Double> clientCategoryDiscounts = new HashMap<>();

    public Pricing(int id, LocalDate startDate, LocalDate endDate, double annualSubscription, double penaltyPerDay, Map<VehicleCategory, Double> categoryPrices, Map<ExtraService, Double> extraServicesPrices, Map<ClientCategory, Double> clientCategoryDiscounts) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.annualSubscription = annualSubscription;
        this.penaltyPerDay = penaltyPerDay;
        if (categoryPrices != null) {
            this.categoryPrices = categoryPrices;
        }
        if (extraServicesPrices != null) {
            this.extraServicesPrices = extraServicesPrices;
        }
        if (clientCategoryDiscounts != null) {
            this.clientCategoryDiscounts = clientCategoryDiscounts;
        }
    }

    public int getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getAnnualSubscription() {
        return annualSubscription;
    }

    public void setAnnualSubscription(double annualSubscription) {
        this.annualSubscription = annualSubscription;
    }

    public double getPenaltyPerDay() {
        return penaltyPerDay;
    }

    public void setPenaltyPerDay(double penaltyPerDay) {
        this.penaltyPerDay = penaltyPerDay;
    }

    public Map<VehicleCategory, Double> getCategoryPrices() {
        return categoryPrices;
    }

    public void setCategoryPrices(Map<VehicleCategory, Double> categoryPrices) {
        this.categoryPrices = categoryPrices;
    }

    public Map<ExtraService, Double> getExtraServicesPrices() {
        return extraServicesPrices;
    }

    public void setExtraServicesPrices(Map<ExtraService, Double> extraServicesPrices) {
        this.extraServicesPrices = extraServicesPrices;
    }

    public Map<ClientCategory, Double> getClientCategoryDiscounts() {
        return clientCategoryDiscounts;
    }

    public void setClientCategoryDiscounts(Map<ClientCategory, Double> clientCategoryDiscounts) {
        this.clientCategoryDiscounts = clientCategoryDiscounts;
    }

    public boolean isValidOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean isValidNow() {
        return isValidOn(LocalDate.now());
    }

}
