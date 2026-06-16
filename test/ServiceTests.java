package test;

import app.App;
import model.Agent;
import model.Administrator;
import model.Client;
import model.ClientCategory;
import model.ExtraService;
import model.LevelOfEducation;
import model.LoginResult;
import model.Pricing;
import model.Reservation;
import model.ReservationStatus;
import model.Subscription;
import model.SubscriptionStatus;
import model.Vehicle;
import model.VehicleCategory;
import model.VehicleModel;
import model.VehicleRental;
import model.VehicleStatus;
import model.UserRole;
import service.AuthService;
import service.ClientService;
import service.PricingService;
import service.ReportService;
import service.ReservationService;
import service.SubscriptionService;
import service.VehicleRentalService;
import service.VehicleService;
import repository.SettingsRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceTests {
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        runTest("PricingService calculates discounts and extras", ServiceTests::testPricingServiceCalculatesReservationPrice);
        runTest("PricingService uses configurable default rental duration", ServiceTests::testPricingServiceUsesDefaultRentalDuration);
        runTest("PricingService calculates extra service charge by pricing date", ServiceTests::testPricingServiceCalculatesExtraServiceChargeByPricingDate);
        runTest("SettingsRepository saves default rental duration", ServiceTests::testSettingsRepositorySavesDefaultRentalDuration);
        runTest("App loads CSV demo data", ServiceTests::testAppLoadsCsvDemoData);
        runTest("App demo login credentials are valid", ServiceTests::testAppDemoLoginCredentialsAreValid);
        runTest("App save and reload preserves CSV relationships", ServiceTests::testAppSaveAndReloadPreservesCsvRelationships);
        runTest("Demo data contains required operational scenarios", ServiceTests::testDemoDataContainsRequiredOperationalScenarios);
        runTest("AuthService authenticates all user roles", ServiceTests::testAuthServiceAuthenticatesAllUserRoles);
        runTest("AuthService rejects invalid and blank credentials", ServiceTests::testAuthServiceRejectsInvalidAndBlankCredentials);
        runTest("ReservationService counts real availability by date overlap", ServiceTests::testReservationServiceFindsAvailableVehiclesByDate);
        runTest("ReservationService cancels and blocks client", ServiceTests::testCancelReservationBlocksClient);
        runTest("ReservationService staff cancel does not block client", ServiceTests::testStaffCancelReservationDoesNotBlockClient);
        runTest("ReservationService marks no-show and blocks client", ServiceTests::testNoShowBlocksClient);
        runTest("ReservationService rejects cancel for issued reservation", ServiceTests::testCancelReservationWithAssignedVehicleFails);
        runTest("ReservationService returns clear creation and cancellation errors", ServiceTests::testReservationServiceErrorMessages);
        runTest("VehicleRentalService issues and returns late rental", ServiceTests::testVehicleRentalIssueAndLateReturn);
        runTest("Vehicle services compare vehicle models by id", ServiceTests::testVehicleServicesCompareModelsById);
        runTest("VehicleRentalService rejects issuing before reservation start", ServiceTests::testVehicleRentalCannotBeIssuedBeforeReservationStart);
        runTest("VehicleRentalService returns clear issue and return errors", ServiceTests::testVehicleRentalErrorMessages);
        runTest("SubscriptionService renews only eligible clients", ServiceTests::testSubscriptionRenewalRules);
        runTest("SubscriptionService rejects early duplicate renewal request", ServiceTests::testSubscriptionRejectsEarlyRenewalRequest);
        runTest("SubscriptionService returns clear renewal errors", ServiceTests::testSubscriptionRenewalErrorMessages);
        runTest("ReportService aggregates revenue by client category", ServiceTests::testReportRevenueByClientCategory);
        runTest("ReportService chart revenue includes uncategorized and total", ServiceTests::testReportRevenueChartIncludesUncategorizedAndTotal);
        runTest("ReportService counts processed reservations by agent", ServiceTests::testReportCountsProcessedReservationsByAgent);
        runTest("ReportService calculates financial report", ServiceTests::testReportFinancialReport);
        runTest("Client can rent only with license older than two years", ServiceTests::testClientCanRentRequiresOldEnoughLicense);

        System.out.println();
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);

        if (failedTests > 0) {
            System.exit(1);
        }
    }

    private static void testPricingServiceCalculatesReservationPrice() {
        PricingService pricingService = new PricingService();
        pricingService.addPricing(createPricing());

        VehicleModel vehicleModel = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        double price = pricingService.calculateReservationPrice(
                vehicleModel,
                ClientCategory.STUDENT,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 4),
                Arrays.asList(ExtraService.GPS, ExtraService.EXTENDED_USE)
        );

        assertDoubleEquals(385.0, price);
    }

    private static void testPricingServiceUsesDefaultRentalDuration() {
        PricingService pricingService = new PricingService();
        pricingService.addPricing(createPricing());
        pricingService.setDefaultRentalDurationDays(5);

        VehicleModel vehicleModel = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        double price = pricingService.calculateReservationPrice(
                vehicleModel,
                ClientCategory.STUDENT,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 4),
                Arrays.asList(ExtraService.EXTENDED_USE)
        );

        assertDoubleEquals(360.0, price);
    }

    private static void testPricingServiceCalculatesExtraServiceChargeByPricingDate() {
        PricingService pricingService = new PricingService();
        pricingService.addPricing(createPricing());

        double gpsCharge = pricingService.calculateExtraServiceCharge(
                ExtraService.GPS,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 3),
                LocalDate.of(2026, 7, 2)
        );

        double extendedUseCharge = pricingService.calculateExtraServiceCharge(
                ExtraService.EXTENDED_USE,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 2)
        );

        assertDoubleEquals(20.0, gpsCharge);
        assertDoubleEquals(10.0, extendedUseCharge);
    }

    private static void testSettingsRepositorySavesDefaultRentalDuration() {
        SettingsRepository settingsRepository = new SettingsRepository();
        int originalValue = settingsRepository.loadDefaultRentalDurationDays();

        try {
            settingsRepository.saveDefaultRentalDurationDays(6);
            assertEquals(6, settingsRepository.loadDefaultRentalDurationDays());
        } finally {
            settingsRepository.saveDefaultRentalDurationDays(originalValue);
        }
    }

    private static void testAppLoadsCsvDemoData() {
        App app = new App();
        app.loadAll();

        assertTrue(!app.getClientService().getClients().isEmpty());
        assertTrue(!app.getAgents().isEmpty());
        assertTrue(!app.getAdministrators().isEmpty());
        assertTrue(!app.getVehicleModels().isEmpty());
        assertTrue(!app.getVehicleService().getVehicles().isEmpty());
        assertTrue(!app.getPricingService().getPricings().isEmpty());
        assertTrue(!app.getReservationService().getReservations().isEmpty());
        assertTrue(!app.getVehicleRentalService().getVehicleRentals().isEmpty());
        assertTrue(app.getDefaultRentalDurationDays() > 0);
    }

    private static void testAppDemoLoginCredentialsAreValid() {
        App app = new App();
        app.loadAll();

        assertEquals(UserRole.ADMINISTRATOR, app.getAuthService().login("admin", "admin").getRole());
        assertEquals(UserRole.AGENT, app.getAuthService().login("agent", "agent").getRole());
        assertEquals(UserRole.CLIENT, app.getAuthService().login("marko.markovic@example.com", "client").getRole());
    }

    private static void testAuthServiceAuthenticatesAllUserRoles() {
        ClientService clientService = new ClientService();
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        clientService.addClient(client);

        Agent agent = new Agent(2, "Agent", "One", "M", LocalDate.of(1990, 1, 1), "060", "Address", "agent", "agent-pass", LevelOfEducation.HIGH, 5);
        Administrator administrator = new Administrator(3, "Admin", "One", "M", LocalDate.of(1985, 1, 1), "061", "Address", "admin", "admin-pass", LevelOfEducation.COLLEGE, 8);

        AuthService authService = new AuthService(clientService, Arrays.asList(agent), Arrays.asList(administrator));

        LoginResult clientLogin = authService.login(" " + client.getUserName() + " ", "pass");
        LoginResult agentLogin = authService.login("agent", "agent-pass");
        LoginResult administratorLogin = authService.login("admin", "admin-pass");

        assertEquals(UserRole.CLIENT, clientLogin.getRole());
        assertEquals(client, clientLogin.getUser());
        assertEquals(UserRole.AGENT, agentLogin.getRole());
        assertEquals(agent, agentLogin.getUser());
        assertEquals(UserRole.ADMINISTRATOR, administratorLogin.getRole());
        assertEquals(administrator, administratorLogin.getUser());
    }

    private static void testAuthServiceRejectsInvalidAndBlankCredentials() {
        AuthService authService = new AuthService(null, null, null);

        assertEquals(null, authService.login(null, "pass"));
        assertEquals(null, authService.login("user", null));
        assertEquals(null, authService.login("   ", "pass"));
        assertEquals(null, authService.login("user", ""));
        assertEquals(null, authService.login("missing", "missing"));
    }

    private static void testAppSaveAndReloadPreservesCsvRelationships() throws IOException {
        List<Path> csvFiles = Arrays.asList(
                Path.of("data/clients.csv"),
                Path.of("data/agents.csv"),
                Path.of("data/administrators.csv"),
                Path.of("data/vehicle_models.csv"),
                Path.of("data/vehicles.csv"),
                Path.of("data/pricings.csv"),
                Path.of("data/reservations.csv"),
                Path.of("data/vehicle_rentals.csv"),
                Path.of("data/settings.csv")
        );

        Map<Path, String> originalContents = new LinkedHashMap<>();
        for (Path csvFile : csvFiles) {
            originalContents.put(csvFile, Files.readString(csvFile));
        }

        try {
            App app = new App();
            app.loadAll();

            int clientCount = app.getClientService().getClients().size();
            int vehicleCount = app.getVehicleService().getVehicles().size();
            int reservationCount = app.getReservationService().getReservations().size();
            int rentalCount = app.getVehicleRentalService().getVehicleRentals().size();

            app.saveAll();

            App reloadedApp = new App();
            reloadedApp.loadAll();

            assertEquals(clientCount, reloadedApp.getClientService().getClients().size());
            assertEquals(vehicleCount, reloadedApp.getVehicleService().getVehicles().size());
            assertEquals(reservationCount, reloadedApp.getReservationService().getReservations().size());
            assertEquals(rentalCount, reloadedApp.getVehicleRentalService().getVehicleRentals().size());
            assertTrue(allReservationsHaveClientAndModel(reloadedApp));
            assertTrue(allRentalsHaveVehicleAndReservation(reloadedApp));
        } finally {
            for (Map.Entry<Path, String> entry : originalContents.entrySet()) {
                Files.writeString(entry.getKey(), entry.getValue());
            }
        }
    }

    private static boolean allReservationsHaveClientAndModel(App app) {
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getClient() == null || reservation.getVehicleModel() == null) {
                return false;
            }
        }
        return true;
    }

    private static boolean allRentalsHaveVehicleAndReservation(App app) {
        for (VehicleRental vehicleRental : app.getVehicleRentalService().getVehicleRentals()) {
            if (vehicleRental.getVehicle() == null || vehicleRental.getReservation() == null) {
                return false;
            }
        }
        return true;
    }

    private static void testDemoDataContainsRequiredOperationalScenarios() {
        App app = new App();
        app.loadAll();

        boolean hasPendingReservation = false;
        boolean hasApprovedReservation = false;
        boolean hasCanceledReservation = false;
        boolean hasRejectedReservation = false;
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getReservationStatus() == ReservationStatus.PENDING) {
                hasPendingReservation = true;
            } else if (reservation.getReservationStatus() == ReservationStatus.APPROVED) {
                hasApprovedReservation = true;
            } else if (reservation.getReservationStatus() == ReservationStatus.CANCELED) {
                hasCanceledReservation = true;
            } else if (reservation.getReservationStatus() == ReservationStatus.REJECTED) {
                hasRejectedReservation = true;
            }
        }

        boolean hasActiveRental = false;
        boolean hasReturnedRental = false;
        boolean hasLatePenalty = false;
        boolean hasAgentRental = false;
        for (VehicleRental vehicleRental : app.getVehicleRentalService().getVehicleRentals()) {
            if (vehicleRental.getActualEndRentDate() == null) {
                hasActiveRental = true;
            } else {
                hasReturnedRental = true;
            }
            if (vehicleRental.getPenaltyForBeingLate() > 0) {
                hasLatePenalty = true;
            }
            if (vehicleRental.getAgent() != null) {
                hasAgentRental = true;
            }
        }

        boolean hasPendingSubscriptionRenewal = false;
        for (Client client : app.getClientService().getClients()) {
            if (client.getSubscription() != null
                    && client.getSubscription().getSubscriptionStatus() == SubscriptionStatus.PENDING_APPROVAL) {
                hasPendingSubscriptionRenewal = true;
            }
        }

        assertTrue(hasPendingReservation);
        assertTrue(hasApprovedReservation);
        assertTrue(hasCanceledReservation);
        assertTrue(hasRejectedReservation);
        assertTrue(hasActiveRental);
        assertTrue(hasReturnedRental);
        assertTrue(hasLatePenalty);
        assertTrue(hasAgentRental);
        assertTrue(hasPendingSubscriptionRenewal);
    }

    private static void testReservationServiceFindsAvailableVehiclesByDate() {
        VehicleModel model = new VehicleModel(1, "Toyota", "Yaris", VehicleCategory.ECONOMY);
        Vehicle firstVehicle = new Vehicle(1, "NS-001-AA", VehicleStatus.AVAILABLE, 10000, model);
        Vehicle secondVehicle = new Vehicle(2, "NS-002-AA", VehicleStatus.AVAILABLE, 20000, model);

        VehicleService vehicleService = new VehicleService();
        vehicleService.addVehicle(firstVehicle);
        vehicleService.addVehicle(secondVehicle);

        ReservationService reservationService = new ReservationService(vehicleService);
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        Reservation unassignedReservation = new Reservation(1, client, model, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), 100, new ArrayList<>());
        unassignedReservation.setReservationStatus(ReservationStatus.PENDING);
        reservationService.addReservation(unassignedReservation);

        assertTrue(reservationService.hasAvailableVehicle(model, LocalDate.now().plusDays(4), LocalDate.now().plusDays(6)));

        Reservation assignedReservation = new Reservation(2, client, model, LocalDate.now().plusDays(4), LocalDate.now().plusDays(6), 100, new ArrayList<>());
        assignedReservation.setReservationStatus(ReservationStatus.APPROVED);
        assignedReservation.setAssignedVehicle(secondVehicle);
        reservationService.addReservation(assignedReservation);

        assertFalse(reservationService.hasAvailableVehicle(model, LocalDate.now().plusDays(4), LocalDate.now().plusDays(6)));

        assignedReservation.setReservationStatus(ReservationStatus.CANCELED);
        assertTrue(reservationService.hasAvailableVehicle(model, LocalDate.now().plusDays(4), LocalDate.now().plusDays(6)));
    }

    private static void testCancelReservationBlocksClient() {
        Client client = createClient(1, ClientCategory.COMPANY, 0);
        VehicleModel model = new VehicleModel(1, "Skoda", "Octavia", VehicleCategory.STANDARD);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), 300, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        ReservationService reservationService = new ReservationService(new VehicleService());

        assertTrue(reservationService.cancelReservationByClient(reservation));
        assertEquals(ReservationStatus.CANCELED, reservation.getReservationStatus());
        assertTrue(reservation.getCancellationTime() != null);
        assertFalse(client.canMakeReservationNow());
    }

    private static void testStaffCancelReservationDoesNotBlockClient() {
        Client client = createClient(1, ClientCategory.COMPANY, 0);
        VehicleModel model = new VehicleModel(1, "Skoda", "Octavia", VehicleCategory.STANDARD);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), 300, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        ReservationService reservationService = new ReservationService(new VehicleService());

        assertTrue(reservationService.cancelReservationByStaff(reservation));
        assertEquals(ReservationStatus.CANCELED, reservation.getReservationStatus());
        assertTrue(reservation.getCancellationTime() != null);
        assertTrue(client.canMakeReservationNow());
    }

    private static void testNoShowBlocksClient() {
        Client client = createClient(1, ClientCategory.COMPANY, 0);
        VehicleModel model = new VehicleModel(1, "Skoda", "Octavia", VehicleCategory.STANDARD);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 300, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        ReservationService reservationService = new ReservationService(new VehicleService());

        assertTrue(reservationService.markNoShow(reservation));
        assertEquals(ReservationStatus.CANCELED, reservation.getReservationStatus());
        assertTrue(reservation.getCancellationTime() != null);
        assertFalse(client.canMakeReservationNow());
    }

    private static void testCancelReservationWithAssignedVehicleFails() {
        Client client = createClient(1, ClientCategory.COMPANY, 0);
        VehicleModel model = new VehicleModel(1, "Skoda", "Octavia", VehicleCategory.STANDARD);
        Vehicle vehicle = new Vehicle(1, "NS-456-AA", VehicleStatus.RENTED, 40000, model);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now(), LocalDate.now().plusDays(2), 300, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);
        reservation.setAssignedVehicle(vehicle);

        ReservationService reservationService = new ReservationService(new VehicleService());

        assertFalse(reservationService.cancelReservationByClient(reservation));
        assertEquals(ReservationStatus.APPROVED, reservation.getReservationStatus());
        assertEquals(null, reservation.getCancellationTime());
        assertEquals(null, client.getReservationBlockedUntil());
    }

    private static void testReservationServiceErrorMessages() {
        VehicleModel model = new VehicleModel(1, "Skoda", "Octavia", VehicleCategory.STANDARD);
        Vehicle vehicle = new Vehicle(1, "NS-222-AA", VehicleStatus.AVAILABLE, 10000, model);
        VehicleService vehicleService = new VehicleService();
        vehicleService.addVehicle(vehicle);
        ReservationService reservationService = new ReservationService(vehicleService);
        Client client = createClient(1, ClientCategory.STUDENT, 0);

        assertEquals("Reservation must start at least tomorrow.", reservationService.getReservationCreationError(client, model, LocalDate.now(), LocalDate.now().plusDays(2)));

        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.EXPIRED);
        assertEquals("Client must have an active subscription.", reservationService.getReservationCreationError(client, model, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4)));

        Reservation issuedReservation = new Reservation(1, client, model, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), 300, new ArrayList<>());
        issuedReservation.setReservationStatus(ReservationStatus.APPROVED);
        issuedReservation.setAssignedVehicle(vehicle);

        assertEquals("Reservation cannot be canceled because the vehicle has already been issued.", reservationService.getReservationCancellationError(issuedReservation));
    }

    private static void testVehicleRentalIssueAndLateReturn() {
        VehicleModel model = new VehicleModel(1, "BMW", "Series 5", VehicleCategory.LUXURY);
        Vehicle vehicle = new Vehicle(1, "BG-001-AA", VehicleStatus.AVAILABLE, 50000, model);
        Client client = createClient(1, ClientCategory.COMPANY, 0);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now(), LocalDate.now().plusDays(2), 900, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);
        Agent agent = new Agent(1, "Agent", "One", "M", LocalDate.of(1990, 1, 1), "060", "Address", "agent", "pass", LevelOfEducation.HIGH, 5);

        VehicleRentalService rentalService = new VehicleRentalService();
        VehicleRental rental = rentalService.issueVehicle(1, vehicle, reservation, 50100, agent);

        assertTrue(rental != null);
        assertEquals(VehicleStatus.RENTED, vehicle.getVehicleStatus());
        assertEquals(vehicle, reservation.getAssignedVehicle());
        assertEquals(agent, rental.getAgent());

        boolean returned = rentalService.returnVehicle(rental, 50750, reservation.getEndDate().plusDays(2), 1500);

        assertTrue(returned);
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getVehicleStatus());
        assertEquals(50750, vehicle.getMileage());
        assertDoubleEquals(3000.0, rental.getPenaltyForBeingLate());
        assertEquals(1, client.getLateReturnsCount());
    }

    private static void testVehicleServicesCompareModelsById() {
        VehicleModel vehicleModelFromVehicle = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        VehicleModel sameVehicleModelFromReservation = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        Vehicle vehicle = new Vehicle(1, "NS-321-AA", VehicleStatus.AVAILABLE, 10000, vehicleModelFromVehicle);

        VehicleService vehicleService = new VehicleService();
        vehicleService.addVehicle(vehicle);

        assertEquals(1, vehicleService.findVehiclesByModel(sameVehicleModelFromReservation).size());

        Client client = createClient(1, ClientCategory.STUDENT, 0);
        Reservation reservation = new Reservation(1, client, sameVehicleModelFromReservation, LocalDate.now(), LocalDate.now().plusDays(1), 200, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        VehicleRentalService vehicleRentalService = new VehicleRentalService();
        assertEquals(null, vehicleRentalService.getIssueVehicleError(vehicle, reservation));
    }

    private static void testVehicleRentalCannotBeIssuedBeforeReservationStart() {
        VehicleModel model = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        Vehicle vehicle = new Vehicle(1, "NS-123-AA", VehicleStatus.AVAILABLE, 25000, model);
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 300, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        VehicleRentalService rentalService = new VehicleRentalService();

        assertFalse(rentalService.canIssueVehicle(vehicle, reservation));
        assertEquals(null, rentalService.issueVehicle(1, vehicle, reservation, 25100));
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getVehicleStatus());
        assertEquals(null, reservation.getAssignedVehicle());
    }

    private static void testVehicleRentalErrorMessages() {
        VehicleModel model = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        Vehicle vehicle = new Vehicle(1, "NS-123-AA", VehicleStatus.AVAILABLE, 25000, model);
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        Reservation reservation = new Reservation(1, client, model, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 300, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        VehicleRentalService rentalService = new VehicleRentalService();

        assertEquals("Vehicle can be issued only during the reservation period.", rentalService.getIssueVehicleError(vehicle, reservation));

        Reservation currentReservation = new Reservation(2, client, model, LocalDate.now(), LocalDate.now().plusDays(2), 300, new ArrayList<>());
        currentReservation.setReservationStatus(ReservationStatus.APPROVED);
        VehicleRental rental = rentalService.issueVehicle(1, vehicle, currentReservation, 25050);

        assertEquals("End mileage cannot be lower than start mileage.", rentalService.getReturnVehicleError(rental, 24000, LocalDate.now()));

        assertTrue(rentalService.returnVehicle(rental, 25100, LocalDate.now(), 1000));
        assertEquals("Vehicle has already been returned.", rentalService.getReturnVehicleError(rental, 25200, LocalDate.now()));
    }

    private static void testSubscriptionRenewalRules() {
        SubscriptionService subscriptionService = new SubscriptionService();

        Client eligibleClient = createClient(1, ClientCategory.STUDENT, 5);
        eligibleClient.getSubscription().setSubscriptionStatus(SubscriptionStatus.PENDING_APPROVAL);

        assertTrue(subscriptionService.approveRenewal(eligibleClient));
        assertEquals(SubscriptionStatus.ACTIVE, eligibleClient.getSubscription().getSubscriptionStatus());
        assertEquals(LocalDate.now().plusYears(1), eligibleClient.getSubscription().getExpirationDate());

        Client blockedClient = createClient(2, ClientCategory.PENSIONER, 6);
        blockedClient.getSubscription().setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        blockedClient.getSubscription().setExpirationDate(LocalDate.now().plusDays(10));

        assertFalse(subscriptionService.requestRenewal(blockedClient));
        assertEquals(SubscriptionStatus.EXPIRED, blockedClient.getSubscription().getSubscriptionStatus());
    }

    private static void testSubscriptionRejectsEarlyRenewalRequest() {
        SubscriptionService subscriptionService = new SubscriptionService();
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        client.getSubscription().setExpirationDate(LocalDate.now().plusDays(90));

        assertFalse(subscriptionService.requestRenewal(client));
        assertEquals(SubscriptionStatus.ACTIVE, client.getSubscription().getSubscriptionStatus());

        client.getSubscription().setExpirationDate(LocalDate.now().plusDays(20));
        assertTrue(subscriptionService.requestRenewal(client));
        assertEquals(SubscriptionStatus.PENDING_APPROVAL, client.getSubscription().getSubscriptionStatus());

        assertFalse(subscriptionService.requestRenewal(client));
        assertEquals(SubscriptionStatus.PENDING_APPROVAL, client.getSubscription().getSubscriptionStatus());
    }

    private static void testSubscriptionRenewalErrorMessages() {
        SubscriptionService subscriptionService = new SubscriptionService();
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        client.getSubscription().setExpirationDate(LocalDate.now().plusDays(90));

        assertEquals("Subscription can be renewed only in the last 30 days before expiration.", subscriptionService.getRenewalRequestError(client));

        client.getSubscription().setExpirationDate(LocalDate.now().plusDays(20));
        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.PENDING_APPROVAL);
        assertEquals("Renewal request is already pending.", subscriptionService.getRenewalRequestError(client));

        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        client.setLateReturnsCount(6);
        assertEquals("Renewal request rejected because of too many late returns.", subscriptionService.getRenewalRequestError(client));
    }

    private static void testReportRevenueByClientCategory() {
        PricingService pricingService = new PricingService();
        pricingService.addPricing(createPricing());

        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService(vehicleService);
        VehicleRentalService vehicleRentalService = new VehicleRentalService();

        Client student = createClient(1, ClientCategory.STUDENT, 0);
        Client company = createClient(2, ClientCategory.COMPANY, 0);
        VehicleModel model = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);

        Reservation approvedStudentReservation = new Reservation(1, student, model, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 500, new ArrayList<>());
        approvedStudentReservation.setReservationStatus(ReservationStatus.APPROVED);
        reservationService.addReservation(approvedStudentReservation);

        Reservation pendingCompanyReservation = new Reservation(2, company, model, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 700, new ArrayList<>());
        pendingCompanyReservation.setReservationStatus(ReservationStatus.PENDING);
        reservationService.addReservation(pendingCompanyReservation);

        List<Client> clients = Arrays.asList(student, company);
        ReportService reportService = new ReportService(clients, new ArrayList<>(), new ArrayList<>(), Arrays.asList(model), reservationService, vehicleRentalService, pricingService);

        Map<ClientCategory, Double> revenue = reportService.getRevenueByClientCategory(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertDoubleEquals(1500.0, revenue.get(ClientCategory.STUDENT));
        assertDoubleEquals(1000.0, revenue.get(ClientCategory.COMPANY));
        assertDoubleEquals(0.0, revenue.get(ClientCategory.PENSIONER));
    }

    private static void testReportRevenueChartIncludesUncategorizedAndTotal() {
        PricingService pricingService = new PricingService();
        pricingService.addPricing(createPricing());

        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService(vehicleService);
        VehicleRentalService vehicleRentalService = new VehicleRentalService();

        Client student = createClient(1, ClientCategory.STUDENT, 0);
        Client regular = createClient(2, null, 0);
        VehicleModel model = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);

        Reservation studentReservation = new Reservation(1, student, model, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 500, 450, 50, 0, new ArrayList<>());
        studentReservation.setReservationStatus(ReservationStatus.APPROVED);
        reservationService.addReservation(studentReservation);

        Reservation regularReservation = new Reservation(2, regular, model, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 700, 650, 50, 0, new ArrayList<>());
        regularReservation.setReservationStatus(ReservationStatus.APPROVED);
        reservationService.addReservation(regularReservation);

        ReportService reportService = new ReportService(Arrays.asList(student, regular), new ArrayList<>(), new ArrayList<>(), Arrays.asList(model), reservationService, vehicleRentalService, pricingService);
        Map<String, Double> revenue = reportService.getRevenueByClientCategoryChartData(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertDoubleEquals(1500.0, revenue.get("STUDENT"));
        assertDoubleEquals(1700.0, revenue.get("NO_CATEGORY"));
        assertDoubleEquals(3200.0, revenue.get("TOTAL"));
    }

    private static void testReportCountsProcessedReservationsByAgent() {
        PricingService pricingService = new PricingService();
        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService(vehicleService);
        VehicleRentalService vehicleRentalService = new VehicleRentalService();

        Agent agent = new Agent(1, "Agent", "One", "M", LocalDate.of(1990, 1, 1), "060", "Address", "agent", "pass", LevelOfEducation.HIGH, 3);
        Client client = createClient(1, ClientCategory.STUDENT, 0);
        VehicleModel model = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);

        Reservation reservation = new Reservation(1, client, model, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 500, new ArrayList<>());
        reservation.setReservationStatus(ReservationStatus.APPROVED);
        reservation.setProcessedByAgent(agent);
        reservation.setProcessedAt(LocalDateTime.now());
        reservationService.addReservation(reservation);

        ReportService reportService = new ReportService(Arrays.asList(client), Arrays.asList(agent), new ArrayList<>(), Arrays.asList(model), reservationService, vehicleRentalService, pricingService);
        Map<String, Integer> processed = reportService.countProcessedReservationsByAgent(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertEquals(1, processed.get("1 - Agent One"));
    }

    private static void testReportFinancialReport() {
        PricingService pricingService = new PricingService();
        pricingService.addPricing(createPricing());

        VehicleService vehicleService = new VehicleService();
        ReservationService reservationService = new ReservationService(vehicleService);
        VehicleRentalService vehicleRentalService = new VehicleRentalService();

        Client client = createClient(1, ClientCategory.STUDENT, 0);
        client.getSubscription().setExpirationDate(LocalDate.of(2027, 7, 1));

        VehicleModel model = new VehicleModel(1, "Toyota", "Corolla", VehicleCategory.STANDARD);
        Reservation approvedReservation = new Reservation(1, client, model, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 500, 420, 80, 40, new ArrayList<>());
        approvedReservation.setReservationStatus(ReservationStatus.APPROVED);
        reservationService.addReservation(approvedReservation);

        Vehicle vehicle = new Vehicle(1, "NS-123-AA", VehicleStatus.AVAILABLE, 10000, model);
        VehicleRental returnedRental = new VehicleRental(1, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 14), 10000, 10500, 3000, vehicle, approvedReservation);
        vehicleRentalService.addRental(returnedRental);

        Agent agent = new Agent(1, "Agent", "One", "M", LocalDate.of(1990, 1, 1), "060", "Address", "agent", "pass", LevelOfEducation.MIDDLE, 0);
        model.Administrator administrator = new model.Administrator(1, "Admin", "One", "M", LocalDate.of(1985, 1, 1), "061", "Address", "admin", "pass", LevelOfEducation.MIDDLE, 0);

        ReportService reportService = new ReportService(
                Arrays.asList(client),
                Arrays.asList(agent),
                Arrays.asList(administrator),
                Arrays.asList(model),
                reservationService,
                vehicleRentalService,
                pricingService
        );

        ReportService.FinancialReport report = reportService.getFinancialReport(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertDoubleEquals(1000.0, report.getSubscriptionRevenue());
        assertDoubleEquals(420.0, report.getRentalRevenue());
        assertDoubleEquals(120.0, report.getExtraServiceRevenue());
        assertDoubleEquals(3000.0, report.getPenaltyRevenue());
        assertDoubleEquals(4540.0, report.getTotalRevenue());
        assertDoubleEquals(100000.0, report.getSalaryExpenses());
        assertDoubleEquals(-95460.0, report.getProfit());
    }

    private static void testClientCanRentRequiresOldEnoughLicense() {
        Client validClient = createClient(1, ClientCategory.STUDENT, 0);
        validClient.setDriversLicenseIssueDate(LocalDate.now().minusYears(2));

        Client invalidClient = createClient(2, ClientCategory.STUDENT, 0);
        invalidClient.setDriversLicenseIssueDate(LocalDate.now().minusYears(2).plusDays(1));

        assertTrue(validClient.canRent());
        assertFalse(invalidClient.canRent());
    }

    private static Pricing createPricing() {
        Map<VehicleCategory, Double> categoryPrices = new HashMap<>();
        categoryPrices.put(VehicleCategory.ECONOMY, 80.0);
        categoryPrices.put(VehicleCategory.STANDARD, 100.0);
        categoryPrices.put(VehicleCategory.FAMILY, 130.0);
        categoryPrices.put(VehicleCategory.LUXURY, 200.0);

        Map<ExtraService, Double> extraServicePrices = new HashMap<>();
        extraServicePrices.put(ExtraService.GPS, 20.0);
        extraServicePrices.put(ExtraService.CHILD_SEAT, 15.0);
        extraServicePrices.put(ExtraService.ADDITIONAL_INSURANCE, 40.0);
        extraServicePrices.put(ExtraService.EXTENDED_USE, 5.0);

        Map<ClientCategory, Double> discounts = new HashMap<>();
        discounts.put(ClientCategory.STUDENT, 0.10);
        discounts.put(ClientCategory.PENSIONER, 0.20);
        discounts.put(ClientCategory.COMPANY, 0.15);

        return new Pricing(1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 1000.0, 1500.0, categoryPrices, extraServicePrices, discounts);
    }

    private static Client createClient(int id, ClientCategory clientCategory, int lateReturnsCount) {
        Subscription subscription = new Subscription(id, SubscriptionStatus.ACTIVE, LocalDate.of(2027, 1, 1));
        Client client = new Client(
                id,
                "Client" + id,
                "Test",
                "M",
                LocalDate.of(1995, 1, 1),
                "060",
                "Address",
                "client" + id,
                "pass",
                LocalDate.now().minusYears(5),
                clientCategory,
                subscription
        );
        client.setLateReturnsCount(lateReturnsCount);
        return client;
    }

    private static void runTest(String name, TestCase testCase) {
        try {
            testCase.run();
            passedTests++;
            System.out.println("[PASS] " + name);
        } catch (AssertionError error) {
            failedTests++;
            System.out.println("[FAIL] " + name + " - " + error.getMessage());
        } catch (Exception exception) {
            failedTests++;
            System.out.println("[FAIL] " + name + " - unexpected exception: " + exception.getMessage());
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true, got false");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false, got true");
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        throw new AssertionError("Expected " + expected + ", got " + actual);
    }

    private static void assertDoubleEquals(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }

    private interface TestCase {
        void run() throws Exception;
    }
}
