package service;

import model.ClientCategory;
import model.ExtraService;
import model.Pricing;
import model.VehicleCategory;

import java.time.LocalDate;
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
}
