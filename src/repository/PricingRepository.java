package repository;

import model.ClientCategory;
import model.ExtraService;
import model.Pricing;
import model.VehicleCategory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PricingRepository {
    private static final String FILE_PATH = "data/pricings.csv";

    public List<Pricing> loadAll() {
        List<Pricing> pricings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 16) {
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                LocalDate startDate = LocalDate.parse(parts[1]);
                LocalDate endDate = LocalDate.parse(parts[2]);
                double annualSubscription = Double.parseDouble(parts[3]);
                double penaltyPerDay = Double.parseDouble(parts[4]);

                Map<VehicleCategory, Double> categoryPrices = new HashMap<>();
                categoryPrices.put(VehicleCategory.ECONOMY, Double.parseDouble(parts[5]));
                categoryPrices.put(VehicleCategory.STANDARD, Double.parseDouble(parts[6]));
                categoryPrices.put(VehicleCategory.FAMILY, Double.parseDouble(parts[7]));
                categoryPrices.put(VehicleCategory.LUXURY, Double.parseDouble(parts[8]));

                Map<ExtraService, Double> extraServicesPrices = new HashMap<>();
                extraServicesPrices.put(ExtraService.GPS, Double.parseDouble(parts[9]));
                extraServicesPrices.put(ExtraService.CHILD_SEAT, Double.parseDouble(parts[10]));
                extraServicesPrices.put(ExtraService.ADDITIONAL_INSURANCE, Double.parseDouble(parts[11]));
                extraServicesPrices.put(ExtraService.EXTENDED_USE, Double.parseDouble(parts[12]));

                Map<ClientCategory, Double> clientCategoryDiscounts = new HashMap<>();
                clientCategoryDiscounts.put(ClientCategory.STUDENT, Double.parseDouble(parts[13]));
                clientCategoryDiscounts.put(ClientCategory.PENSIONER, Double.parseDouble(parts[14]));
                clientCategoryDiscounts.put(ClientCategory.COMPANY, Double.parseDouble(parts[15]));

                Pricing pricing = new Pricing(
                        id,
                        startDate,
                        endDate,
                        annualSubscription,
                        penaltyPerDay,
                        categoryPrices,
                        extraServicesPrices,
                        clientCategoryDiscounts
                );
                pricings.add(pricing);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return pricings;
    }

    public void saveAll(List<Pricing> pricings) {
        if (pricings == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;startDate;endDate;annualSubscription;penaltyPerDay;economyDailyPrice;standardDailyPrice;familyDailyPrice;luxuryDailyPrice;gpsPrice;childSeatPrice;additionalInsurancePrice;extendedUsePricePerDay;studentDiscount;pensionerDiscount;companyDiscount");
            writer.newLine();

            for (Pricing pricing : pricings) {
                String line = String.join(";",
                        String.valueOf(pricing.getId()),
                        pricing.getStartDate().toString(),
                        pricing.getEndDate().toString(),
                        String.valueOf(pricing.getAnnualSubscription()),
                        String.valueOf(pricing.getPenaltyPerDay()),
                        String.valueOf(getVehicleCategoryPrice(pricing, VehicleCategory.ECONOMY)),
                        String.valueOf(getVehicleCategoryPrice(pricing, VehicleCategory.STANDARD)),
                        String.valueOf(getVehicleCategoryPrice(pricing, VehicleCategory.FAMILY)),
                        String.valueOf(getVehicleCategoryPrice(pricing, VehicleCategory.LUXURY)),
                        String.valueOf(getExtraServicePrice(pricing, ExtraService.GPS)),
                        String.valueOf(getExtraServicePrice(pricing, ExtraService.CHILD_SEAT)),
                        String.valueOf(getExtraServicePrice(pricing, ExtraService.ADDITIONAL_INSURANCE)),
                        String.valueOf(getExtraServicePrice(pricing, ExtraService.EXTENDED_USE)),
                        String.valueOf(getClientCategoryDiscount(pricing, ClientCategory.STUDENT)),
                        String.valueOf(getClientCategoryDiscount(pricing, ClientCategory.PENSIONER)),
                        String.valueOf(getClientCategoryDiscount(pricing, ClientCategory.COMPANY))
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private double getVehicleCategoryPrice(Pricing pricing, VehicleCategory vehicleCategory) {
        Double price = pricing.getCategoryPrices().get(vehicleCategory);
        if (price == null) {
            return 0;
        }
        return price;
    }

    private double getExtraServicePrice(Pricing pricing, ExtraService extraService) {
        Double price = pricing.getExtraServicesPrices().get(extraService);
        if (price == null) {
            return 0;
        }
        return price;
    }

    private double getClientCategoryDiscount(Pricing pricing, ClientCategory clientCategory) {
        Double discount = pricing.getClientCategoryDiscounts().get(clientCategory);
        if (discount == null) {
            return 0;
        }
        return discount;
    }
}
