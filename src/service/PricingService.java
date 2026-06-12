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

    public PricingService() {
    }

    public List<Pricing> getPricings() {
        return pricings;
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

    public double calculateReservationPrice(VehicleModel vehicleModel, ClientCategory clientCategory, LocalDate startDate, LocalDate endDate, List<ExtraService> extraServices) {
        if (vehicleModel == null || startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return 0;
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double dailyPrice = getDailyPrice(vehicleModel.getVehicleCategory(), startDate);
        double rentalPrice = dailyPrice * numberOfDays;

        double discount = getClientDiscount(clientCategory, startDate);
        rentalPrice = rentalPrice * (1 - discount);

        double extraServicesPrice = 0;
        if (extraServices != null) {
            for (ExtraService extraService : extraServices) {
                if (extraService == ExtraService.EXTENDED_USE) {
                    long extendedDays = numberOfDays - 3;
                    if (extendedDays > 0) {
                        extraServicesPrice += getExtraServicePrice(extraService, startDate) * extendedDays;
                    }
                } else {
                    extraServicesPrice += getExtraServicePrice(extraService, startDate);
                }
            }
        }

        return rentalPrice + extraServicesPrice;
    }
}
