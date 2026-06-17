package gui;

import app.App;
import model.Agent;
import model.Client;
import model.ClientCategory;
import model.ExtraService;
import model.Reservation;
import model.ReservationStatus;
import model.Subscription;
import model.SubscriptionStatus;
import model.User;
import model.Vehicle;
import model.VehicleCategory;
import model.VehicleModel;
import model.VehicleRental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AgentMainFrame extends JFrame {
    private App app;
    private Agent agent;
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    private JComboBox<Reservation> issueReservationComboBox;
    private JComboBox<Vehicle> issueVehicleComboBox;
    private JTextField startMileageField;
    private JCheckBox issueGpsCheckBox;
    private JCheckBox issueChildSeatCheckBox;
    private JCheckBox issueInsuranceCheckBox;
    private JCheckBox issueExtendedUseCheckBox;
    private JComboBox<VehicleRental> returnRentalComboBox;
    private JTextField endMileageField;
    private JTable subscriptionRequestsTable;
    private DefaultTableModel subscriptionRequestsTableModel;
    private DefaultTableModel clientsTableModel;
    private JTextField dailyOverviewDateField;
    private JLabel pendingReservationsCountLabel;
    private JLabel approvedReservationsCountLabel;
    private JLabel activeRentalsCountLabel;
    private JLabel returnsTodayCountLabel;
    private DefaultTableModel dailyReservationsTableModel;
    private DefaultTableModel dailyRentalsTableModel;
    private JTextField vehicleAvailabilityRegistrationField;
    private JTextField vehicleAvailabilityManufacturerField;
    private JTextField vehicleAvailabilityModelField;
    private JComboBox<String> vehicleAvailabilityCategoryComboBox;
    private JTextField vehicleAvailabilityStartDateField;
    private JTextField vehicleAvailabilityEndDateField;
    private DefaultTableModel vehicleAvailabilityTableModel;

    public AgentMainFrame(App app, Agent agent) {
        this.app = app;
        this.agent = agent;

        setTitle("Agent - " + agent.getName() + " " + agent.getLastName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = GuiTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new BorderLayout(10, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = GuiTheme.createTitleLabel("Agent workspace");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel(agent.getName() + " " + agent.getLastName() + " - " + agent.getUserName());
        userLabel.setForeground(GuiTheme.MUTED_TEXT);
        headerPanel.add(userLabel, BorderLayout.SOUTH);

        JButton logoutButton = GuiTheme.createSecondaryButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(GuiTheme.BACKGROUND);
        tabbedPane.addTab("Daily Overview", createDailyOverviewPanel());
        tabbedPane.addTab("Vehicles", createVehiclesPanel());
        tabbedPane.addTab("Reservations", createReservationsPanel());
        tabbedPane.addTab("Issue Vehicle", createIssueVehiclePanel());
        tabbedPane.addTab("Return Vehicle", createReturnVehiclePanel());
        tabbedPane.addTab("Subscriptions", createSubscriptionsPanel());
        tabbedPane.addTab("Clients", createClientsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createDailyOverviewPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel topPanel = new JPanel(new BorderLayout(12, 12));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Daily overview"));

        JLabel hintLabel = new JLabel("Reservations, active rentals and returns for the selected day.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);
        dailyOverviewDateField = createTextField(LocalDate.now().toString());
        JButton refreshButton = GuiTheme.createPrimaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshDailyOverview());
        filterPanel.add(new JLabel("Date:"));
        filterPanel.add(dailyOverviewDateField);
        filterPanel.add(refreshButton);
        topPanel.add(filterPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 6));
        contentPanel.setOpaque(false);
        contentPanel.add(createDailySummaryPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createDailyReservationsPanel(), createDailyRentalsPanel());
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(splitPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        refreshDailyOverview();
        return wrapWithMargin(panel);
    }

    private JPanel createVehiclesPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel topPanel = new JPanel(new BorderLayout(12, 12));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Vehicles and availability"));
        JLabel hintLabel = new JLabel("Filter by registration, manufacturer, model, category and availability date range.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);
        GridBagConstraints filterGbc = new GridBagConstraints();
        filterGbc.insets = new Insets(2, 4, 2, 4);
        filterGbc.fill = GridBagConstraints.HORIZONTAL;

        vehicleAvailabilityRegistrationField = createFilterTextField();
        vehicleAvailabilityManufacturerField = createFilterTextField();
        vehicleAvailabilityModelField = createFilterTextField();
        vehicleAvailabilityCategoryComboBox = new JComboBox<>();
        vehicleAvailabilityCategoryComboBox.setPreferredSize(new Dimension(115, 24));
        vehicleAvailabilityCategoryComboBox.addItem("ANY");
        for (VehicleCategory category : VehicleCategory.values()) {
            vehicleAvailabilityCategoryComboBox.addItem(category.name());
        }
        vehicleAvailabilityStartDateField = createFilterTextField();
        vehicleAvailabilityEndDateField = createFilterTextField();
        JButton refreshButton = GuiTheme.createPrimaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshVehicleAvailabilityTable());

        addFilterComponent(filterPanel, filterGbc, 0, 0, "Registration:", vehicleAvailabilityRegistrationField);
        addFilterComponent(filterPanel, filterGbc, 2, 0, "Manufacturer:", vehicleAvailabilityManufacturerField);
        addFilterComponent(filterPanel, filterGbc, 4, 0, "Model:", vehicleAvailabilityModelField);
        addFilterComponent(filterPanel, filterGbc, 0, 1, "Category:", vehicleAvailabilityCategoryComboBox);
        addFilterComponent(filterPanel, filterGbc, 2, 1, "Available from:", vehicleAvailabilityStartDateField);
        addFilterComponent(filterPanel, filterGbc, 4, 1, "Available to:", vehicleAvailabilityEndDateField);
        filterGbc.gridx = 6;
        filterGbc.gridy = 1;
        filterGbc.weightx = 0;
        filterPanel.add(refreshButton, filterGbc);
        topPanel.add(filterPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        vehicleAvailabilityTableModel = new DefaultTableModel(new String[]{"ID", "Registration", "Manufacturer", "Model", "Category", "Mileage", "Current status", "Available in period"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(vehicleAvailabilityTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshVehicleAvailabilityTable();
        return wrapWithMargin(panel);
    }

    private JPanel createDailySummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        pendingReservationsCountLabel = new JLabel("0", SwingConstants.CENTER);
        approvedReservationsCountLabel = new JLabel("0", SwingConstants.CENTER);
        activeRentalsCountLabel = new JLabel("0", SwingConstants.CENTER);
        returnsTodayCountLabel = new JLabel("0", SwingConstants.CENTER);

        panel.add(createSummaryCard("Pending reservations", pendingReservationsCountLabel));
        panel.add(createSummaryCard("Approved reservations", approvedReservationsCountLabel));
        panel.add(createSummaryCard("Active rentals", activeRentalsCountLabel));
        panel.add(createSummaryCard("Returns today", returnsTodayCountLabel));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(GuiTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(GuiTheme.MUTED_TEXT);

        valueLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        valueLabel.setForeground(GuiTheme.PRIMARY_DARK);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDailyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(GuiTheme.createSectionLabel("Reservations on selected day"), BorderLayout.NORTH);

        dailyReservationsTableModel = new DefaultTableModel(new String[]{"ID", "Client", "Model", "Period", "Status", "Base price", "Extra services", "Total price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(dailyReservationsTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDailyRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(GuiTheme.createSectionLabel("Rentals on selected day"), BorderLayout.NORTH);

        dailyRentalsTableModel = new DefaultTableModel(new String[]{"ID", "Vehicle", "Client", "Agent", "Planned end", "Actual return", "Penalty"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(dailyRentalsTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReservationsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Reservations"), BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton approveButton = GuiTheme.createPrimaryButton("Approve selected");
        approveButton.addActionListener(e -> approveSelectedReservation());
        buttonPanel.add(approveButton);

        JButton rejectButton = GuiTheme.createSecondaryButton("Reject selected");
        rejectButton.addActionListener(e -> rejectSelectedReservation());
        buttonPanel.add(rejectButton);

        JButton noShowButton = GuiTheme.createSecondaryButton("Mark no-show");
        noShowButton.addActionListener(e -> markSelectedReservationNoShow());
        buttonPanel.add(noShowButton);

        titlePanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Client", "Model", "Start", "End", "Status", "Base price", "Extra services", "Total price"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GuiTheme.styleTable(reservationsTable);
        refreshReservationsTable();

        panel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        return wrapWithMargin(panel);
    }

    private JPanel createIssueVehiclePanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Issue vehicle"));

        JLabel hintLabel = new JLabel("Choose an approved reservation, select a concrete available vehicle and enter start mileage.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        issueReservationComboBox = new JComboBox<>();
        issueReservationComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> createReservationComboLabel(value, isSelected, list));
        issueReservationComboBox.addActionListener(e -> refreshIssueReservationDetails());

        issueVehicleComboBox = new JComboBox<>();
        issueVehicleComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> createVehicleComboLabel(value, isSelected, list));

        startMileageField = createTextField("0");
        JPanel extraServicesPanel = createIssueExtraServicesPanel();

        addFormRow(formPanel, gbc, 0, "Reservation", issueReservationComboBox);
        addFormRow(formPanel, gbc, 1, "Vehicle", issueVehicleComboBox);
        addFormRow(formPanel, gbc, 2, "Start mileage", startMileageField);
        addFormRow(formPanel, gbc, 3, "Extra services", extraServicesPanel);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton issueButton = GuiTheme.createPrimaryButton("Issue vehicle");
        issueButton.addActionListener(e -> issueSelectedVehicle());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(issueButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshIssueReservationComboBox();
        return wrapWithMargin(panel);
    }

    private JPanel createReturnVehiclePanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Return vehicle"));

        JLabel hintLabel = new JLabel("Choose an active rental and enter end mileage.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        returnRentalComboBox = new JComboBox<>();
        returnRentalComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> createRentalComboLabel(value, isSelected, list));
        returnRentalComboBox.addActionListener(e -> updateEndMileageSuggestion());

        endMileageField = createTextField("0");

        addFormRow(formPanel, gbc, 0, "Rental", returnRentalComboBox);
        addFormRow(formPanel, gbc, 1, "End mileage", endMileageField);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton returnButton = GuiTheme.createPrimaryButton("Return vehicle");
        returnButton.addActionListener(e -> returnSelectedVehicle());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(returnButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshReturnRentalComboBox();
        return wrapWithMargin(panel);
    }

    private JPanel createSubscriptionsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Subscription renewal requests"), BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton approveButton = GuiTheme.createPrimaryButton("Approve selected");
        approveButton.addActionListener(e -> approveSelectedSubscriptionRenewal());
        buttonPanel.add(approveButton);

        JButton rejectButton = GuiTheme.createSecondaryButton("Reject selected");
        rejectButton.addActionListener(e -> rejectSelectedSubscriptionRenewal());
        buttonPanel.add(rejectButton);

        titlePanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"Client ID", "Client", "Username", "Late returns", "Expiration date", "Status"};
        subscriptionRequestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        subscriptionRequestsTable = new JTable(subscriptionRequestsTableModel);
        subscriptionRequestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GuiTheme.styleTable(subscriptionRequestsTable);
        refreshSubscriptionRequestsTable();

        panel.add(new JScrollPane(subscriptionRequestsTable), BorderLayout.CENTER);
        return wrapWithMargin(panel);
    }

    private JPanel createClientsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Clients"), BorderLayout.WEST);

        JButton addButton = GuiTheme.createPrimaryButton("Register client");
        addButton.addActionListener(e -> showRegisterClientDialog());
        titlePanel.add(addButton, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Client", "Username", "License issued", "Category", "Subscription", "Expires"};
        clientsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(clientsTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshClientsTable();
        return wrapWithMargin(panel);
    }

    private void refreshReservationsTable() {
        reservationsTableModel.setRowCount(0);

        for (Reservation reservation : app.getReservationService().getReservations()) {
            VehicleModel vehicleModel = reservation.getVehicleModel();
            reservationsTableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getClient().getName() + " " + reservation.getClient().getLastName(),
                    vehicleModel.getManufacturer() + " " + vehicleModel.getModel(),
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getReservationStatus(),
                    reservation.getRentalBasePrice(),
                    reservation.getTotalExtraServicesPrice(),
                    reservation.getPrice()
            });
        }
    }

    private void refreshDailyOverview() {
        if (dailyOverviewDateField == null || dailyReservationsTableModel == null || dailyRentalsTableModel == null) {
            return;
        }

        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dailyOverviewDateField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Date must use yyyy-mm-dd format.");
            return;
        }

        dailyReservationsTableModel.setRowCount(0);
        dailyRentalsTableModel.setRowCount(0);

        int pendingReservations = 0;
        int approvedReservations = 0;
        int activeRentals = 0;
        int returnsToday = 0;

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (!isDateInsidePeriod(selectedDate, reservation.getStartDate(), reservation.getEndDate())) {
                continue;
            }

            if (reservation.getReservationStatus() == ReservationStatus.PENDING) {
                pendingReservations++;
            }
            if (reservation.getReservationStatus() == ReservationStatus.APPROVED) {
                approvedReservations++;
            }

            VehicleModel vehicleModel = reservation.getVehicleModel();
            dailyReservationsTableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getClient().getName() + " " + reservation.getClient().getLastName(),
                    vehicleModel.getManufacturer() + " " + vehicleModel.getModel(),
                    reservation.getStartDate() + " - " + reservation.getEndDate(),
                    reservation.getReservationStatus(),
                    reservation.getRentalBasePrice(),
                    reservation.getTotalExtraServicesPrice(),
                    reservation.getPrice()
            });
        }

        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            boolean activeOnSelectedDate = isRentalActiveOnDate(rental, selectedDate);
            boolean returnedOnSelectedDate = selectedDate.equals(rental.getActualEndRentDate());

            if (!activeOnSelectedDate && !returnedOnSelectedDate) {
                continue;
            }

            if (activeOnSelectedDate) {
                activeRentals++;
            }
            if (returnedOnSelectedDate) {
                returnsToday++;
            }

            Reservation reservation = rental.getReservation();
            Vehicle vehicle = rental.getVehicle();
            Agent rentalAgent = rental.getAgent();
            dailyRentalsTableModel.addRow(new Object[]{
                    rental.getId(),
                    vehicle == null ? "" : vehicle.getRegistration(),
                    reservation == null ? "" : reservation.getClient().getName() + " " + reservation.getClient().getLastName(),
                    rentalAgent == null ? "" : rentalAgent.getName() + " " + rentalAgent.getLastName(),
                    rental.getEndRentDate(),
                    rental.getActualEndRentDate() == null ? "" : rental.getActualEndRentDate(),
                    rental.getPenaltyForBeingLate()
            });
        }

        pendingReservationsCountLabel.setText(String.valueOf(pendingReservations));
        approvedReservationsCountLabel.setText(String.valueOf(approvedReservations));
        activeRentalsCountLabel.setText(String.valueOf(activeRentals));
        returnsTodayCountLabel.setText(String.valueOf(returnsToday));
    }

    private void refreshIssueReservationComboBox() {
        issueReservationComboBox.removeAllItems();
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getReservationStatus() == ReservationStatus.APPROVED &&
                    reservation.getAssignedVehicle() == null &&
                    isDateInsidePeriod(LocalDate.now(), reservation.getStartDate(), reservation.getEndDate())) {
                issueReservationComboBox.addItem(reservation);
            }
        }
        refreshIssueReservationDetails();
    }

    private void refreshIssueReservationDetails() {
        refreshIssueVehicleComboBox();
        refreshIssueExtraServiceSelection();
    }

    private void refreshIssueVehicleComboBox() {
        issueVehicleComboBox.removeAllItems();
        Reservation reservation = (Reservation) issueReservationComboBox.getSelectedItem();
        if (reservation == null) {
            return;
        }

        List<Vehicle> availableVehicles = app.getReservationService().findAvailableVehicles(
                reservation.getVehicleModel(),
                reservation.getStartDate(),
                reservation.getEndDate()
        );

        for (Vehicle vehicle : availableVehicles) {
            issueVehicleComboBox.addItem(vehicle);
        }
    }

    private void refreshIssueExtraServiceSelection() {
        Reservation reservation = (Reservation) issueReservationComboBox.getSelectedItem();
        if (reservation == null) {
            clearIssueExtraServiceSelection();
            return;
        }

        List<ExtraService> extraServices = reservation.getExtraServices();
        issueGpsCheckBox.setSelected(extraServices.contains(ExtraService.GPS));
        issueChildSeatCheckBox.setSelected(extraServices.contains(ExtraService.CHILD_SEAT));
        issueInsuranceCheckBox.setSelected(extraServices.contains(ExtraService.ADDITIONAL_INSURANCE));
        issueExtendedUseCheckBox.setSelected(extraServices.contains(ExtraService.EXTENDED_USE));
    }

    private void refreshVehicleAvailabilityTable() {
        if (vehicleAvailabilityTableModel == null) {
            return;
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        boolean usePeriodFilter = !vehicleAvailabilityStartDateField.getText().trim().isEmpty()
                || !vehicleAvailabilityEndDateField.getText().trim().isEmpty();

        if (usePeriodFilter) {
            try {
                startDate = LocalDate.parse(vehicleAvailabilityStartDateField.getText().trim());
                endDate = LocalDate.parse(vehicleAvailabilityEndDateField.getText().trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Availability dates must use yyyy-mm-dd format.");
                return;
            }

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "Availability end date cannot be before start date.");
                return;
            }
        }

        String registrationFilter = vehicleAvailabilityRegistrationField.getText().trim().toLowerCase();
        String manufacturerFilter = vehicleAvailabilityManufacturerField.getText().trim().toLowerCase();
        String modelFilter = vehicleAvailabilityModelField.getText().trim().toLowerCase();
        String categoryFilter = (String) vehicleAvailabilityCategoryComboBox.getSelectedItem();
        vehicleAvailabilityTableModel.setRowCount(0);

        for (Vehicle vehicle : app.getVehicleService().getVehicles()) {
            VehicleModel vehicleModel = vehicle.getVehicleModel();
            if (vehicleModel == null) {
                continue;
            }

            if (!registrationFilter.isEmpty() && !vehicle.getRegistration().toLowerCase().contains(registrationFilter)) {
                continue;
            }

            if (!manufacturerFilter.isEmpty() && !vehicleModel.getManufacturer().toLowerCase().contains(manufacturerFilter)) {
                continue;
            }

            if (!modelFilter.isEmpty() && !vehicleModel.getModel().toLowerCase().contains(modelFilter)) {
                continue;
            }

            if (categoryFilter != null && !categoryFilter.equals("ANY") && !vehicleModel.getVehicleCategory().name().equals(categoryFilter)) {
                continue;
            }

            boolean availableInPeriod = !usePeriodFilter || containsVehicleById(
                    app.getReservationService().findAvailableVehicles(vehicleModel, startDate, endDate),
                    vehicle
            );

            vehicleAvailabilityTableModel.addRow(new Object[]{
                    vehicle.getId(),
                    vehicle.getRegistration(),
                    vehicleModel.getManufacturer(),
                    vehicleModel.getModel(),
                    vehicleModel.getVehicleCategory(),
                    vehicle.getMileage(),
                    vehicle.getVehicleStatus(),
                    usePeriodFilter ? (availableInPeriod ? "YES" : "NO") : ""
            });
        }
    }

    private void approveSelectedReservation() {
        Reservation reservation = getSelectedReservation();
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending reservations can be approved.");
            return;
        }

        boolean approved = app.getReservationService().approveReservation(reservation, agent);
        if (!approved) {
            JOptionPane.showMessageDialog(this, "Reservation cannot be approved because no vehicle is available in that period.");
            return;
        }

        app.saveAll();
        refreshReservationsTable();
        refreshIssueReservationComboBox();
        refreshVehicleAvailabilityTable();
        refreshDailyOverview();
        JOptionPane.showMessageDialog(this, "Reservation approved.");
    }

    private void rejectSelectedReservation() {
        Reservation reservation = getSelectedReservation();
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending reservations can be rejected.");
            return;
        }

        boolean rejected = app.getReservationService().rejectReservation(reservation, agent);
        if (!rejected) {
            JOptionPane.showMessageDialog(this, "Reservation was not rejected.");
            return;
        }

        app.saveAll();
        refreshReservationsTable();
        refreshIssueReservationComboBox();
        refreshVehicleAvailabilityTable();
        refreshDailyOverview();
        JOptionPane.showMessageDialog(this, "Reservation rejected.");
    }

    private void markSelectedReservationNoShow() {
        Reservation reservation = getSelectedReservation();
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        String error = app.getReservationService().getNoShowError(reservation);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Mark this reservation as no-show and block the client for 24 hours?",
                "Confirm no-show",
                JOptionPane.YES_NO_OPTION
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        boolean marked = app.getReservationService().markNoShow(reservation, agent);
        if (!marked) {
            JOptionPane.showMessageDialog(this, app.getReservationService().getNoShowError(reservation));
            return;
        }

        app.saveAll();
        refreshReservationsTable();
        refreshIssueReservationComboBox();
        refreshVehicleAvailabilityTable();
        refreshDailyOverview();
        JOptionPane.showMessageDialog(this, "Reservation marked as no-show. Client is blocked for 24 hours.");
    }

    private void issueSelectedVehicle() {
        Reservation reservation = (Reservation) issueReservationComboBox.getSelectedItem();
        Vehicle vehicle = (Vehicle) issueVehicleComboBox.getSelectedItem();

        if (reservation == null || vehicle == null) {
            JOptionPane.showMessageDialog(this, "Select reservation and vehicle first.");
            return;
        }

        int startMileage;
        try {
            startMileage = Integer.parseInt(startMileageField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Start mileage must be a number.");
            return;
        }

        if (startMileage < vehicle.getMileage()) {
            JOptionPane.showMessageDialog(this, "Start mileage cannot be lower than current vehicle mileage.");
            return;
        }

        VehicleRental vehicleRental = app.getVehicleRentalService().issueVehicle(
                app.getNextVehicleRentalId(),
                vehicle,
                reservation,
                startMileage,
                agent
        );

        if (vehicleRental == null) {
            JOptionPane.showMessageDialog(this, app.getVehicleRentalService().getIssueVehicleError(vehicle, reservation));
            return;
        }

        double addedExtraServicesPrice = applyIssueExtraServices(reservation);
        app.saveAll();
        refreshReservationsTable();
        refreshIssueReservationComboBox();
        refreshReturnRentalComboBox();
        refreshVehicleAvailabilityTable();
        refreshDailyOverview();
        clearIssueExtraServiceSelection();
        JOptionPane.showMessageDialog(this, "Vehicle issued. Rental ID: " + vehicleRental.getId() + ". Added extra services: " + addedExtraServicesPrice);
    }

    private void refreshReturnRentalComboBox() {
        if (returnRentalComboBox == null) {
            return;
        }

        returnRentalComboBox.removeAllItems();
        for (VehicleRental vehicleRental : app.getVehicleRentalService().findActiveRentals()) {
            returnRentalComboBox.addItem(vehicleRental);
        }
        updateEndMileageSuggestion();
    }

    private void refreshSubscriptionRequestsTable() {
        if (subscriptionRequestsTableModel == null) {
            return;
        }

        subscriptionRequestsTableModel.setRowCount(0);
        for (Client client : app.getClientService().getClients()) {
            if (client.getSubscription() != null &&
                    client.getSubscription().getSubscriptionStatus() == SubscriptionStatus.PENDING_APPROVAL) {
                subscriptionRequestsTableModel.addRow(new Object[]{
                        client.getId(),
                        client.getName() + " " + client.getLastName(),
                        client.getUserName(),
                        client.getLateReturnsCount(),
                        client.getSubscription().getExpirationDate(),
                        client.getSubscription().getSubscriptionStatus()
                });
            }
        }
    }

    private void refreshClientsTable() {
        if (clientsTableModel == null) {
            return;
        }

        clientsTableModel.setRowCount(0);
        for (Client client : app.getClientService().getClients()) {
            clientsTableModel.addRow(new Object[]{
                    client.getId(),
                    client.getName() + " " + client.getLastName(),
                    client.getUserName(),
                    client.getDriversLicenseIssueDate(),
                    client.getClientCategory(),
                    client.getSubscription() == null ? "" : client.getSubscription().getSubscriptionStatus(),
                    client.getSubscription() == null ? "" : client.getSubscription().getExpirationDate()
            });
        }
    }

    private void showRegisterClientDialog() {
        JTextField nameField = createTextField();
        JTextField lastNameField = createTextField();
        JTextField genderField = createTextField();
        JTextField birthdayField = createTextField("1998-01-01");
        JTextField phoneField = createTextField();
        JTextField addressField = createTextField();
        JTextField usernameField = createTextField();
        JPasswordField passwordField = createPasswordField();
        JTextField licenseIssueDateField = createTextField("2020-01-01");
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("NONE");
        for (ClientCategory clientCategory : ClientCategory.values()) {
            categoryComboBox.addItem(clientCategory.name());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Last name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderField);
        panel.add(new JLabel("Birthday (yyyy-mm-dd):"));
        panel.add(birthdayField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Username/email:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Driver license issue date:"));
        panel.add(licenseIssueDateField);
        panel.add(new JLabel("Client category:"));
        panel.add(categoryComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String gender = genderField.getText().trim();
            LocalDate birthday = LocalDate.parse(birthdayField.getText().trim());
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            LocalDate licenseIssueDate = LocalDate.parse(licenseIssueDateField.getText().trim());
            ClientCategory clientCategory = parseClientCategory((String) categoryComboBox.getSelectedItem());

            if (name.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, last name, username and password are required.");
                return;
            }

            if (!username.contains("@")) {
                JOptionPane.showMessageDialog(this, "Client username must be an email address.");
                return;
            }

            if (isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }

            if (licenseIssueDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Driver license issue date cannot be in the future.");
                return;
            }

            if (licenseIssueDate.isAfter(LocalDate.now().minusYears(2))) {
                JOptionPane.showMessageDialog(this, "Client must have a driver license for at least 2 years.");
                return;
            }

            Subscription subscription = new Subscription(
                    app.getNextSubscriptionId(),
                    SubscriptionStatus.ACTIVE,
                    LocalDate.now().plusYears(1)
            );

            Client client = new Client(
                    app.getNextClientId(),
                    name,
                    lastName,
                    gender,
                    birthday,
                    phone,
                    address,
                    username,
                    password,
                    licenseIssueDate,
                    clientCategory,
                    subscription
            );

            app.getClientService().addClient(client);
            app.getSubscriptionService().addSubscription(subscription);
            app.saveAll();
            refreshClientsTable();
            JOptionPane.showMessageDialog(this, "Client registered.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must use yyyy-mm-dd format.");
        }
    }

    private void approveSelectedSubscriptionRenewal() {
        Client client = getSelectedSubscriptionClient();
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Select a subscription request first.");
            return;
        }

        boolean approved = app.getSubscriptionService().approveRenewal(client);
        if (!approved) {
            JOptionPane.showMessageDialog(this, "Subscription renewal could not be approved.");
            return;
        }

        app.saveAll();
        refreshSubscriptionRequestsTable();
        refreshClientsTable();
        JOptionPane.showMessageDialog(this, "Subscription renewal approved.");
    }

    private void rejectSelectedSubscriptionRenewal() {
        Client client = getSelectedSubscriptionClient();
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Select a subscription request first.");
            return;
        }

        boolean rejected = app.getSubscriptionService().rejectRenewal(client);
        if (!rejected) {
            JOptionPane.showMessageDialog(this, "Subscription renewal could not be rejected.");
            return;
        }

        app.saveAll();
        refreshSubscriptionRequestsTable();
        refreshClientsTable();
        JOptionPane.showMessageDialog(this, "Subscription renewal rejected.");
    }

    private Client getSelectedSubscriptionClient() {
        int selectedRow = subscriptionRequestsTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int modelRow = subscriptionRequestsTable.convertRowIndexToModel(selectedRow);
        int clientId = (int) subscriptionRequestsTableModel.getValueAt(modelRow, 0);

        for (Client client : app.getClientService().getClients()) {
            if (client.getId() == clientId) {
                return client;
            }
        }

        return null;
    }

    private void updateEndMileageSuggestion() {
        if (endMileageField == null || returnRentalComboBox == null) {
            return;
        }

        VehicleRental vehicleRental = (VehicleRental) returnRentalComboBox.getSelectedItem();
        if (vehicleRental != null && vehicleRental.getVehicle() != null) {
            endMileageField.setText(String.valueOf(vehicleRental.getVehicle().getMileage()));
        }
    }

    private void returnSelectedVehicle() {
        VehicleRental vehicleRental = (VehicleRental) returnRentalComboBox.getSelectedItem();
        if (vehicleRental == null) {
            JOptionPane.showMessageDialog(this, "Select an active rental first.");
            return;
        }

        int endMileage;
        try {
            endMileage = Integer.parseInt(endMileageField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "End mileage must be a number.");
            return;
        }

        LocalDate returnDate = LocalDate.now();
        double penaltyPerDay = app.getPricingService().getPenaltyPerDay(returnDate);

        boolean returned = app.getVehicleRentalService().returnVehicle(vehicleRental, endMileage, returnDate, penaltyPerDay);
        if (!returned) {
            JOptionPane.showMessageDialog(this, app.getVehicleRentalService().getReturnVehicleError(vehicleRental, endMileage, returnDate));
            return;
        }

        app.saveAll();
        refreshReservationsTable();
        refreshIssueReservationComboBox();
        refreshReturnRentalComboBox();
        refreshVehicleAvailabilityTable();
        refreshDailyOverview();
        JOptionPane.showMessageDialog(this, "Vehicle returned. Penalty: " + vehicleRental.getPenaltyForBeingLate());
    }

    private Reservation getSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int modelRow = reservationsTable.convertRowIndexToModel(selectedRow);
        int reservationId = (int) reservationsTableModel.getValueAt(modelRow, 0);

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getId() == reservationId) {
                return reservation;
            }
        }

        return null;
    }

    private JLabel createReservationComboLabel(Reservation reservation, boolean isSelected, JList<? extends Reservation> list) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        if (reservation != null) {
            VehicleModel model = reservation.getVehicleModel();
            label.setText("#" + reservation.getId() + " - " + reservation.getClient().getName() + " " + reservation.getClient().getLastName() + " - " + model.getManufacturer() + " " + model.getModel());
        }
        applyComboSelectionStyle(label, isSelected, list);
        return label;
    }

    private JLabel createVehicleComboLabel(Vehicle vehicle, boolean isSelected, JList<? extends Vehicle> list) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        if (vehicle != null) {
            label.setText(vehicle.getRegistration() + " - mileage " + vehicle.getMileage());
        }
        applyComboSelectionStyle(label, isSelected, list);
        return label;
    }

    private JLabel createRentalComboLabel(VehicleRental vehicleRental, boolean isSelected, JList<? extends VehicleRental> list) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        if (vehicleRental != null && vehicleRental.getVehicle() != null && vehicleRental.getReservation() != null) {
            Reservation reservation = vehicleRental.getReservation();
            label.setText("#" + vehicleRental.getId() + " - " +
                    vehicleRental.getVehicle().getRegistration() + " - " +
                    reservation.getClient().getName() + " " + reservation.getClient().getLastName());
        }
        applyComboSelectionStyle(label, isSelected, list);
        return label;
    }

    private JPanel createIssueExtraServicesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.setOpaque(false);

        issueGpsCheckBox = new JCheckBox("GPS navigation");
        issueChildSeatCheckBox = new JCheckBox("Child seat");
        issueInsuranceCheckBox = new JCheckBox("Additional insurance");
        issueExtendedUseCheckBox = new JCheckBox("Extended use");

        JCheckBox[] checkBoxes = {
                issueGpsCheckBox,
                issueChildSeatCheckBox,
                issueInsuranceCheckBox,
                issueExtendedUseCheckBox
        };

        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setOpaque(false);
            checkBox.setForeground(GuiTheme.TEXT);
            panel.add(checkBox);
        }

        return panel;
    }

    private double applyIssueExtraServices(Reservation reservation) {
        if (reservation == null) {
            return 0;
        }

        List<ExtraService> selectedExtraServices = new ArrayList<>();
        if (issueGpsCheckBox.isSelected()) {
            selectedExtraServices.add(ExtraService.GPS);
        }
        if (issueChildSeatCheckBox.isSelected()) {
            selectedExtraServices.add(ExtraService.CHILD_SEAT);
        }
        if (issueInsuranceCheckBox.isSelected()) {
            selectedExtraServices.add(ExtraService.ADDITIONAL_INSURANCE);
        }
        if (issueExtendedUseCheckBox.isSelected()) {
            selectedExtraServices.add(ExtraService.EXTENDED_USE);
        }

        double addedPrice = 0;
        for (ExtraService extraService : selectedExtraServices) {
            if (reservation.addExtraService(extraService)) {
                addedPrice += app.getPricingService().calculateExtraServiceCharge(
                        extraService,
                        reservation.getStartDate(),
                        reservation.getEndDate(),
                        LocalDate.now()
                );
            }
        }

        reservation.setIssueExtraServicesPrice(reservation.getIssueExtraServicesPrice() + addedPrice);
        reservation.setPrice(reservation.getPrice() + addedPrice);
        return addedPrice;
    }

    private void clearIssueExtraServiceSelection() {
        issueGpsCheckBox.setSelected(false);
        issueChildSeatCheckBox.setSelected(false);
        issueInsuranceCheckBox.setSelected(false);
        issueExtendedUseCheckBox.setSelected(false);
    }

    private void applyComboSelectionStyle(JLabel label, boolean isSelected, JList<?> list) {
        if (isSelected) {
            label.setOpaque(true);
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        }
    }

    private JTextField createTextField() {
        return createTextField("");
    }

    private JTextField createTextField(String value) {
        JTextField textField = new JTextField(value);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return textField;
    }

    private JTextField createFilterTextField() {
        JTextField textField = createTextField();
        textField.setColumns(9);
        textField.setPreferredSize(new Dimension(105, 24));
        return textField;
    }

    private void addFilterComponent(JPanel panel, GridBagConstraints gbc, int x, int y, String label, JComponent component) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return passwordField;
    }

    private ClientCategory parseClientCategory(String value) {
        if (value == null || value.equals("NONE")) {
            return null;
        }
        return ClientCategory.valueOf(value);
    }

    private boolean isUsernameTaken(String username) {
        if (app.getClientService().usernameExists(username)) {
            return true;
        }

        for (Agent agent : app.getAgents()) {
            if (username.equals(agent.getUserName())) {
                return true;
            }
        }

        for (User administrator : app.getAdministrators()) {
            if (username.equals(administrator.getUserName())) {
                return true;
            }
        }

        return false;
    }

    private boolean isDateInsidePeriod(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private boolean isRentalActiveOnDate(VehicleRental rental, LocalDate date) {
        if (rental == null || date == null || rental.getStartRentDate() == null || rental.getEndRentDate() == null) {
            return false;
        }

        LocalDate effectiveEndDate = rental.getActualEndRentDate() == null ? rental.getEndRentDate() : rental.getActualEndRentDate();
        return isDateInsidePeriod(date, rental.getStartRentDate(), effectiveEndDate);
    }

    private boolean containsVehicleById(List<Vehicle> vehicles, Vehicle selectedVehicle) {
        if (vehicles == null || selectedVehicle == null) {
            return false;
        }

        for (Vehicle vehicle : vehicles) {
            if (vehicle != null && vehicle.getId() == selectedVehicle.getId()) {
                return true;
            }
        }

        return false;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private JPanel wrapWithMargin(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(GuiTheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private void logout() {
        app.saveAll();
        new LoginFrame(app).setVisible(true);
        dispose();
    }
}
