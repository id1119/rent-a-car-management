package service;

import model.ClientCategory;
import model.ExtraService;
import model.Pricing;
import model.VehicleCategory;
import model.VehicleModel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PricingService {
    private List<Pricing> pricings = new ArrayList<>();
    private int defaultRentalDurationDays = 3;

    public PricingService() {
    }

    public List<Pricing> getPricings() {
        return pricings;
    }

    public int getDefaultRentalDurationDays() {
        return defaultRentalDurationDays;
    }

    public void setDefaultRentalDurationDays(int defaultRentalDurationDays) {
        if (defaultRentalDurationDays > 0) {
            this.defaultRentalDurationDays = defaultRentalDurationDays;
        }
    }

    public void addPricing(Pricing pricing) {
        if (pricing != null) {
            pricings.add(pricing);
        }
    }

    public void removePricing(Pricing pricing) {
        pricings.remove(pricing);
    }

    public Pricing findValidPricing(LocalDate date) {
        if (date == null) {
            return null;
        }
        for (Pricing pricing : pricings) {
            if (pricing.isValidOn(date)) {
                return pricing;
            }
        }
        return null;
    }

    public double getDailyPrice(VehicleCategory category, LocalDate date) {
        if (category == null) {
            return 0;
        }

        Pricing pricing = findValidPricing(date);
        if (pricing == null) {
            return 0;
        }

        Double price = pricing.getCategoryPrices().get(category);
        if (price == null) {
            return 0;
        }
        return price;
    }

    public double getExtraServicePrice(ExtraService extraService, LocalDate date){
        if (extraService == null) {
            return 0;
        }

        Pricing pricing = findValidPricing(date);
        if (pricing == null) {
            return 0;
        }

        Double price = pricing.getExtraServicesPrices().get(extraService);
        if (price == null) {
            return 0;
        }
        return price;
    }

    public double getClientDiscount(ClientCategory clientCategory, LocalDate date){
        if (clientCategory == null){
            return 0;
        }

        Pricing pricing = findValidPricing(date);
        if (pricing == null) {
            return 0;
        }

        Double discount = pricing.getClientCategoryDiscounts().get(clientCategory);
        if (discount == null) {
            return 0;
        }
        return discount;
    }

    public double getPenaltyPerDay(LocalDate date) {
        Pricing pricing = findValidPricing(date);
        if (pricing == null) {
            return 0;
        }
        return pricing.getPenaltyPerDay();
    }

    public double calculateReservationPrice(VehicleModel vehicleModel, ClientCategory clientCategory, LocalDate startDate, LocalDate endDate, List<ExtraService> extraServices) {
        if (vehicleModel == null || startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return 0;
        }

        return calculateRentalBasePrice(vehicleModel, clientCategory, startDate, endDate) +
                calculateExtraServicesPrice(extraServices, startDate, endDate, startDate);
    }

    public double calculateRentalBasePrice(VehicleModel vehicleModel, ClientCategory clientCategory, LocalDate startDate, LocalDate endDate) {
        if (vehicleModel == null || startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return 0;
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double dailyPrice = getDailyPrice(vehicleModel.getVehicleCategory(), startDate);
        double rentalPrice = dailyPrice * numberOfDays;

        double discount = getClientDiscount(clientCategory, startDate);
        return rentalPrice * (1 - discount);
    }

    public double calculateExtraServicesPrice(List<ExtraService> extraServices, LocalDate startDate, LocalDate endDate, LocalDate pricingDate) {
        double extraServicesPrice = 0;
        if (extraServices != null) {
            for (ExtraService extraService : extraServices) {
                extraServicesPrice += calculateExtraServiceCharge(extraService, startDate, endDate, pricingDate);
            }
        }
        return extraServicesPrice;
    }

    public double calculateExtraServiceCharge(ExtraService extraService, LocalDate startDate, LocalDate endDate, LocalDate pricingDate) {
        if (extraService == null || startDate == null || endDate == null || pricingDate == null || endDate.isBefore(startDate)) {
            return 0;
        }

        double price = getExtraServicePrice(extraService, pricingDate);
        if (extraService != ExtraService.EXTENDED_USE) {
            return price;
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long extendedDays = numberOfDays - defaultRentalDurationDays;
        if (extendedDays <= 0) {
            return 0;
        }
        return price * extendedDays;
    }
}
