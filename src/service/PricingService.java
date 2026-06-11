package service;

import model.Pricing;

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
}
