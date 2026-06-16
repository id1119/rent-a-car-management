package service;

import model.Administrator;
import model.Agent;
import model.Client;
import model.ClientCategory;
import model.Pricing;
import model.Reservation;
import model.ReservationStatus;
import model.VehicleModel;
import model.VehicleRental;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private List<Client> clients;
    private List<Agent> agents;
    private List<Administrator> administrators;
    private List<VehicleModel> vehicleModels;
    private ReservationService reservationService;
    private VehicleRentalService vehicleRentalService;
    private PricingService pricingService;

    public ReportService(List<Client> clients, List<Agent> agents, List<Administrator> administrators, List<VehicleModel> vehicleModels, ReservationService reservationService, VehicleRentalService vehicleRentalService, PricingService pricingService) {
        this.clients = clients;
        this.agents = agents;
        this.administrators = administrators;
        this.vehicleModels = vehicleModels;
        this.reservationService = reservationService;
        this.vehicleRentalService = vehicleRentalService;
        this.pricingService = pricingService;
    }

    public Map<String, Integer> countRentalsByAgent(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Agent agent : agents) {
            result.put(formatAgent(agent), 0);
        }

        for (VehicleRental rental : vehicleRentalService.getVehicleRentals()) {
            if (rental.getAgent() == null || !isDateInRange(rental.getStartRentDate(), startDate, endDate)) {
                continue;
            }
            String key = formatAgent(rental.getAgent());
            result.put(key, result.getOrDefault(key, 0) + 1);
        }

        return result;
    }

    public Map<String, Integer> countProcessedReservationsByAgent(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Agent agent : agents) {
            result.put(formatAgent(agent), 0);
        }

        for (Reservation reservation : reservationService.getReservations()) {
            Agent processedByAgent = reservation.getProcessedByAgent();
            LocalDate processedDate = reservation.getCreatedAt() == null ? reservation.getStartDate() : reservation.getCreatedAt().toLocalDate();
            if (processedByAgent == null || !isDateInRange(processedDate, startDate, endDate)) {
                continue;
            }
            String key = formatAgent(processedByAgent);
            result.put(key, result.getOrDefault(key, 0) + 1);
        }

        return result;
    }

    public Map<ReservationStatus, Integer> countReservationsByStatus(LocalDate startDate, LocalDate endDate) {
        Map<ReservationStatus, Integer> result = new LinkedHashMap<>();
        for (ReservationStatus status : ReservationStatus.values()) {
            result.put(status, 0);
        }

        for (Reservation reservation : reservationService.getReservations()) {
            LocalDate createdDate = reservation.getCreatedAt() == null ? reservation.getStartDate() : reservation.getCreatedAt().toLocalDate();
            if (!isDateInRange(createdDate, startDate, endDate)) {
                continue;
            }
            result.put(reservation.getReservationStatus(), result.get(reservation.getReservationStatus()) + 1);
        }

        return result;
    }

    public Map<String, ModelReport> getModelReports(LocalDate startDate, LocalDate endDate) {
        Map<Integer, ModelReport> byModelId = new HashMap<>();
        for (VehicleModel vehicleModel : vehicleModels) {
            byModelId.put(vehicleModel.getId(), new ModelReport(vehicleModel.getManufacturer() + " " + vehicleModel.getModel(), String.valueOf(vehicleModel.getVehicleCategory())));
        }

        for (Reservation reservation : reservationService.getReservations()) {
            if (!isDateInRange(reservation.getStartDate(), startDate, endDate)) {
                continue;
            }
            ModelReport report = byModelId.get(reservation.getVehicleModel().getId());
            if (report != null) {
                report.reservationCount++;
            }
        }

        for (VehicleRental rental : vehicleRentalService.getVehicleRentals()) {
            if (!isDateInRange(rental.getStartRentDate(), startDate, endDate) || rental.getReservation() == null) {
                continue;
            }
            ModelReport report = byModelId.get(rental.getReservation().getVehicleModel().getId());
            if (report != null) {
                report.rentalCount++;
            }
        }

        Map<String, ModelReport> result = new LinkedHashMap<>();
        for (VehicleModel vehicleModel : vehicleModels) {
            ModelReport report = byModelId.get(vehicleModel.getId());
            result.put(report.modelName, report);
        }
        return result;
    }

    public FinancialReport getFinancialReport(LocalDate startDate, LocalDate endDate) {
        FinancialReport report = new FinancialReport();

        for (Client client : clients) {
            if (client.getSubscription() != null) {
                LocalDate subscriptionStartDate = client.getSubscription().getExpirationDate().minusYears(1);
                if (isDateInRange(subscriptionStartDate, startDate, endDate)) {
                    Pricing pricing = pricingService.findValidPricing(subscriptionStartDate);
                    if (pricing != null) {
                        report.subscriptionRevenue += pricing.getAnnualSubscription();
                    }
                }
            }
        }

        for (Reservation reservation : reservationService.getReservations()) {
            if (reservation.getReservationStatus() == ReservationStatus.APPROVED && isDateInRange(reservation.getStartDate(), startDate, endDate)) {
                report.rentalRevenue += reservation.getRentalBasePrice();
                report.extraServiceRevenue += reservation.getTotalExtraServicesPrice();
            }
        }

        for (VehicleRental rental : vehicleRentalService.getVehicleRentals()) {
            if (isDateInRange(rental.getStartRentDate(), startDate, endDate)) {
                report.penaltyRevenue += rental.getPenaltyForBeingLate();
            }
        }

        for (Agent agent : agents) {
            report.salaryExpenses += agent.getSalary();
        }
        for (Administrator administrator : administrators) {
            report.salaryExpenses += administrator.getSalary();
        }

        return report;
    }

    public Map<ClientCategory, Double> getRevenueByClientCategory(LocalDate startDate, LocalDate endDate) {
        Map<ClientCategory, Double> result = new LinkedHashMap<>();
        for (ClientCategory clientCategory : ClientCategory.values()) {
            result.put(clientCategory, 0.0);
        }

        for (Reservation reservation : reservationService.getReservations()) {
            if (reservation.getReservationStatus() != ReservationStatus.APPROVED || !isDateInRange(reservation.getStartDate(), startDate, endDate)) {
                continue;
            }

            ClientCategory clientCategory = reservation.getClient().getClientCategory();
            if (clientCategory != null) {
                result.put(clientCategory, result.get(clientCategory) + reservation.getPrice());
            }
        }

        for (Client client : clients) {
            if (client.getSubscription() == null || client.getClientCategory() == null) {
                continue;
            }

            LocalDate subscriptionStartDate = client.getSubscription().getExpirationDate().minusYears(1);
            if (!isDateInRange(subscriptionStartDate, startDate, endDate)) {
                continue;
            }

            Pricing pricing = pricingService.findValidPricing(subscriptionStartDate);
            if (pricing != null) {
                ClientCategory clientCategory = client.getClientCategory();
                result.put(clientCategory, result.get(clientCategory) + pricing.getAnnualSubscription());
            }
        }

        return result;
    }

    public Map<String, Double> getRevenueByClientCategoryChartData(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (ClientCategory clientCategory : ClientCategory.values()) {
            result.put(clientCategory.name(), 0.0);
        }
        result.put("NO_CATEGORY", 0.0);

        for (Reservation reservation : reservationService.getReservations()) {
            if (reservation.getReservationStatus() != ReservationStatus.APPROVED || !isDateInRange(reservation.getStartDate(), startDate, endDate)) {
                continue;
            }

            String key = getClientCategoryKey(reservation.getClient());
            result.put(key, result.getOrDefault(key, 0.0) + reservation.getPrice());
        }

        for (Client client : clients) {
            if (client.getSubscription() == null) {
                continue;
            }

            LocalDate subscriptionStartDate = client.getSubscription().getExpirationDate().minusYears(1);
            if (!isDateInRange(subscriptionStartDate, startDate, endDate)) {
                continue;
            }

            Pricing pricing = pricingService.findValidPricing(subscriptionStartDate);
            if (pricing != null) {
                String key = getClientCategoryKey(client);
                result.put(key, result.getOrDefault(key, 0.0) + pricing.getAnnualSubscription());
            }
        }

        double total = 0;
        for (Double value : result.values()) {
            if (value != null) {
                total += value;
            }
        }
        result.put("TOTAL", total);
        return result;
    }

    private boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private String formatAgent(Agent agent) {
        return agent.getId() + " - " + agent.getName() + " " + agent.getLastName();
    }

    private String getClientCategoryKey(Client client) {
        if (client == null || client.getClientCategory() == null) {
            return "NO_CATEGORY";
        }
        return client.getClientCategory().name();
    }

    public static class ModelReport {
        private String modelName;
        private String category;
        private int reservationCount;
        private int rentalCount;

        public ModelReport(String modelName, String category) {
            this.modelName = modelName;
            this.category = category;
        }

        public String getModelName() {
            return modelName;
        }

        public String getCategory() {
            return category;
        }

        public int getReservationCount() {
            return reservationCount;
        }

        public int getRentalCount() {
            return rentalCount;
        }
    }

    public static class FinancialReport {
        private double subscriptionRevenue;
        private double rentalRevenue;
        private double extraServiceRevenue;
        private double penaltyRevenue;
        private double salaryExpenses;

        public double getSubscriptionRevenue() {
            return subscriptionRevenue;
        }

        public double getRentalRevenue() {
            return rentalRevenue;
        }

        public double getExtraServiceRevenue() {
            return extraServiceRevenue;
        }

        public double getPenaltyRevenue() {
            return penaltyRevenue;
        }

        public double getSalaryExpenses() {
            return salaryExpenses;
        }

        public double getTotalRevenue() {
            return subscriptionRevenue + rentalRevenue + extraServiceRevenue + penaltyRevenue;
        }

        public double getProfit() {
            return getTotalRevenue() - salaryExpenses;
        }
    }
}
