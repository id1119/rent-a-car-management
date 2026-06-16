package app;

import model.Administrator;
import model.Agent;
import model.Client;
import model.Pricing;
import model.Reservation;
import model.Subscription;
import model.Vehicle;
import model.VehicleModel;
import model.VehicleRental;
import repository.AdministratorRepository;
import repository.AgentRepository;
import repository.ClientRepository;
import repository.PricingRepository;
import repository.ReservationRepository;
import repository.SettingsRepository;
import repository.VehicleModelRepository;
import repository.VehicleRentalRepository;
import repository.VehicleRepository;
import service.AuthService;
import service.ClientService;
import service.PricingService;
import service.ReportService;
import service.ReservationService;
import service.SubscriptionService;
import service.VehicleRentalService;
import service.VehicleService;

import java.util.List;

public class App {
    private VehicleModelRepository vehicleModelRepository = new VehicleModelRepository();
    private VehicleRepository vehicleRepository = new VehicleRepository();
    private ClientRepository clientRepository = new ClientRepository();
    private PricingRepository pricingRepository = new PricingRepository();
    private AgentRepository agentRepository = new AgentRepository();
    private AdministratorRepository administratorRepository = new AdministratorRepository();
    private ReservationRepository reservationRepository = new ReservationRepository();
    private VehicleRentalRepository vehicleRentalRepository = new VehicleRentalRepository();
    private SettingsRepository settingsRepository = new SettingsRepository();

    private List<Client> clients;
    private List<Agent> agents;
    private List<Administrator> administrators;
    private List<VehicleModel> vehicleModels;
    private List<Vehicle> vehicles;
    private List<Pricing> pricings;
    private List<Reservation> reservations;
    private List<VehicleRental> vehicleRentals;

    private AuthService authService;
    private ClientService clientService;
    private VehicleService vehicleService;
    private PricingService pricingService;
    private ReservationService reservationService;
    private VehicleRentalService vehicleRentalService;
    private SubscriptionService subscriptionService;
    private ReportService reportService;
    private int defaultRentalDurationDays = 3;

    public void loadAll() {
        defaultRentalDurationDays = settingsRepository.loadDefaultRentalDurationDays();
        vehicleModels = vehicleModelRepository.loadAll();
        vehicles = vehicleRepository.loadAll(vehicleModels);
        clients = clientRepository.loadAll();
        pricings = pricingRepository.loadAll();
        agents = agentRepository.loadAll();
        administrators = administratorRepository.loadAll();
        reservations = reservationRepository.loadAll(clients, vehicleModels, vehicles, agents);
        vehicleRentals = vehicleRentalRepository.loadAll(vehicles, reservations, agents);

        clientService = new ClientService();
        clientService.getClients().addAll(clients);

        vehicleService = new VehicleService();
        vehicleService.getVehicles().addAll(vehicles);

        pricingService = new PricingService();
        pricingService.getPricings().addAll(pricings);
        pricingService.setDefaultRentalDurationDays(defaultRentalDurationDays);

        reservationService = new ReservationService(vehicleService);
        reservationService.getReservations().addAll(reservations);
        reservationService.rejectExpiredPendingReservations();

        vehicleRentalService = new VehicleRentalService();
        vehicleRentalService.getVehicleRentals().addAll(vehicleRentals);

        subscriptionService = new SubscriptionService();
        for (Client client : clients) {
            Subscription subscription = client.getSubscription();
            if (subscription != null) {
                subscriptionService.addSubscription(subscription);
            }
        }

        authService = new AuthService(clientService, agents, administrators);
        reportService = new ReportService(clientService.getClients(), agents, administrators, vehicleModels, reservationService, vehicleRentalService, pricingService);
    }

    public void saveAll() {
        clientRepository.saveAll(clientService.getClients());
        vehicleModelRepository.saveAll(vehicleModels);
        vehicleRepository.saveAll(vehicleService.getVehicles());
        pricingRepository.saveAll(pricingService.getPricings());
        reservationRepository.saveAll(reservationService.getReservations());
        vehicleRentalRepository.saveAll(vehicleRentalService.getVehicleRentals());
        agentRepository.saveAll(agents);
        administratorRepository.saveAll(administrators);
        settingsRepository.saveDefaultRentalDurationDays(defaultRentalDurationDays);
    }

    public int getNextClientId() {
        int maxId = 0;
        for (Client client : clientService.getClients()) {
            if (client.getId() > maxId) {
                maxId = client.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextSubscriptionId() {
        int maxId = 0;
        for (Subscription subscription : subscriptionService.getSubscriptions()) {
            if (subscription.getId() > maxId) {
                maxId = subscription.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextReservationId() {
        int maxId = 0;
        for (Reservation reservation : reservationService.getReservations()) {
            if (reservation.getId() > maxId) {
                maxId = reservation.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextVehicleRentalId() {
        int maxId = 0;
        for (VehicleRental vehicleRental : vehicleRentalService.getVehicleRentals()) {
            if (vehicleRental.getId() > maxId) {
                maxId = vehicleRental.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextVehicleId() {
        int maxId = 0;
        for (Vehicle vehicle : vehicleService.getVehicles()) {
            if (vehicle.getId() > maxId) {
                maxId = vehicle.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextVehicleModelId() {
        int maxId = 0;
        for (VehicleModel vehicleModel : vehicleModels) {
            if (vehicleModel.getId() > maxId) {
                maxId = vehicleModel.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextPricingId() {
        int maxId = 0;
        for (Pricing pricing : pricingService.getPricings()) {
            if (pricing.getId() > maxId) {
                maxId = pricing.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextAgentId() {
        int maxId = 0;
        for (Agent agent : agents) {
            if (agent.getId() > maxId) {
                maxId = agent.getId();
            }
        }
        return maxId + 1;
    }

    public int getNextAdministratorId() {
        int maxId = 0;
        for (Administrator administrator : administrators) {
            if (administrator.getId() > maxId) {
                maxId = administrator.getId();
            }
        }
        return maxId + 1;
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Administrator> getAdministrators() {
        return administrators;
    }

    public List<VehicleModel> getVehicleModels() {
        return vehicleModels;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Pricing> getPricings() {
        return pricings;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<VehicleRental> getVehicleRentals() {
        return vehicleRentals;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public VehicleService getVehicleService() {
        return vehicleService;
    }

    public PricingService getPricingService() {
        return pricingService;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    public VehicleRentalService getVehicleRentalService() {
        return vehicleRentalService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public int getDefaultRentalDurationDays() {
        return defaultRentalDurationDays;
    }

    public void setDefaultRentalDurationDays(int defaultRentalDurationDays) {
        if (defaultRentalDurationDays > 0) {
            this.defaultRentalDurationDays = defaultRentalDurationDays;
            if (pricingService != null) {
                pricingService.setDefaultRentalDurationDays(defaultRentalDurationDays);
            }
        }
    }
}
