package gui;

import app.App;
import model.Administrator;
import model.Agent;
import model.Client;
import model.ClientCategory;
import model.Employee;
import model.ExtraService;
import model.LevelOfEducation;
import model.Pricing;
import model.Reservation;
import model.Subscription;
import model.SubscriptionStatus;
import model.Vehicle;
import model.VehicleCategory;
import model.VehicleModel;
import model.VehicleRental;
import model.VehicleStatus;
import model.ReservationStatus;
import service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminMainFrame extends JFrame {
    private App app;
    private Administrator administrator;
    private DefaultTableModel agentsTableModel;
    private DefaultTableModel administratorsTableModel;
    private DefaultTableModel clientsTableModel;
    private DefaultTableModel pricingTableModel;
    private DefaultTableModel vehicleModelsTableModel;
    private DefaultTableModel vehiclesTableModel;
    private DefaultTableModel rentalsByAgentTableModel;
    private DefaultTableModel reservationStatusesTableModel;
    private DefaultTableModel modelReportsTableModel;
    private DefaultTableModel financialReportTableModel;
    private DefaultTableModel operationsReservationsTableModel;
    private DefaultTableModel operationsRentalsTableModel;
    private JTable agentsTable;
    private JTable administratorsTable;
    private JTable clientsTable;
    private JTable pricingTable;
    private JTable vehicleModelsTable;
    private JTable vehiclesTable;
    private JTable operationsReservationsTable;
    private JComboBox<String> operationsReservationStatusFilter;
    private JTextField operationsReservationStartDateFilter;
    private JTextField operationsReservationEndDateFilter;
    private JTextField operationsReservationSearchFilter;
    private JComboBox<String> operationsRentalStatusFilter;
    private JTextField operationsRentalStartDateFilter;
    private JTextField operationsRentalEndDateFilter;
    private JTextField operationsRentalSearchFilter;
    private JLabel operationsPendingCountLabel;
    private JLabel operationsApprovedCountLabel;
    private JLabel operationsActiveRentalsCountLabel;
    private JLabel operationsReturnedRentalsCountLabel;
    private XChartDashboardPanel chartDashboardPanel;

    public AdminMainFrame(App app, Administrator administrator) {
        this.app = app;
        this.administrator = administrator;

        setTitle("Administrator - " + administrator.getName() + " " + administrator.getLastName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(980, 660));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = GuiTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new BorderLayout(10, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = GuiTheme.createTitleLabel("Administrator workspace");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel(administrator.getName() + " " + administrator.getLastName() + " - " + administrator.getUserName());
        userLabel.setForeground(GuiTheme.MUTED_TEXT);
        headerPanel.add(userLabel, BorderLayout.SOUTH);

        JButton logoutButton = GuiTheme.createSecondaryButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(GuiTheme.BACKGROUND);
        tabbedPane.addTab("Employees", createEmployeesPanel());
        tabbedPane.addTab("Clients", createClientsPanel());
        tabbedPane.addTab("Pricing", createPricingPanel());
        tabbedPane.addTab("Vehicles", createVehiclesPanel());
        tabbedPane.addTab("Operations", createOperationsPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 16));
        panel.setBackground(GuiTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        panel.add(createAgentsPanel());
        panel.add(createAdministratorsPanel());

        return panel;
    }

    private JPanel createAgentsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Agents"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton addButton = GuiTheme.createPrimaryButton("Add agent");
        addButton.addActionListener(e -> showAddAgentDialog());
        JButton editButton = GuiTheme.createSecondaryButton("Edit");
        editButton.addActionListener(e -> showEditAgentDialog());
        JButton deleteButton = GuiTheme.createSecondaryButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedAgent());
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Username", "Education", "Experience", "Salary"};
        agentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        agentsTable = new JTable(agentsTableModel);
        GuiTheme.styleTable(agentsTable);
        panel.add(new JScrollPane(agentsTable), BorderLayout.CENTER);

        refreshAgentsTable();
        return panel;
    }

    private JPanel createAdministratorsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Administrators"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton addButton = GuiTheme.createPrimaryButton("Add administrator");
        addButton.addActionListener(e -> showAddAdministratorDialog());
        JButton editButton = GuiTheme.createSecondaryButton("Edit");
        editButton.addActionListener(e -> showEditAdministratorDialog());
        JButton deleteButton = GuiTheme.createSecondaryButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedAdministrator());
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Username", "Education", "Experience", "Salary"};
        administratorsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        administratorsTable = new JTable(administratorsTableModel);
        GuiTheme.styleTable(administratorsTable);
        panel.add(new JScrollPane(administratorsTable), BorderLayout.CENTER);

        refreshAdministratorsTable();
        return panel;
    }

    private JPanel createClientsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Clients"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton addButton = GuiTheme.createPrimaryButton("Add client");
        addButton.addActionListener(e -> showAddClientDialog());
        JButton editButton = GuiTheme.createSecondaryButton("Edit");
        editButton.addActionListener(e -> showEditClientDialog());
        JButton deleteButton = GuiTheme.createSecondaryButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedClient());
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        clientsTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Username", "License", "Category", "Late", "Subscription", "Expires"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientsTable = new JTable(clientsTableModel);
        GuiTheme.styleTable(clientsTable);
        panel.add(new JScrollPane(clientsTable), BorderLayout.CENTER);

        refreshClientsTable();
        return wrapWithTopMargin(panel);
    }

    private JPanel createPricingPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Pricing"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton addButton = GuiTheme.createPrimaryButton("Add pricing");
        addButton.addActionListener(e -> showAddPricingDialog());
        JButton editButton = GuiTheme.createSecondaryButton("Edit");
        editButton.addActionListener(e -> showEditPricingDialog());
        JButton deleteButton = GuiTheme.createSecondaryButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedPricing());
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {
                "ID",
                "Start",
                "End",
                "Annual subscription",
                "Penalty/day",
                "Economy",
                "Standard",
                "Family",
                "Luxury"
        };

        pricingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pricingTable = new JTable(pricingTableModel);
        GuiTheme.styleTable(pricingTable);
        panel.add(new JScrollPane(pricingTable), BorderLayout.CENTER);

        refreshPricingTable();
        return wrapWithTopMargin(panel);
    }

    private void refreshPricingTable() {
        pricingTableModel.setRowCount(0);

        for (Pricing pricing : app.getPricingService().getPricings()) {
            pricingTableModel.addRow(new Object[]{
                    pricing.getId(),
                    pricing.getStartDate(),
                    pricing.getEndDate(),
                    pricing.getAnnualSubscription(),
                    pricing.getPenaltyPerDay(),
                    pricing.getCategoryPrices().get(VehicleCategory.ECONOMY),
                    pricing.getCategoryPrices().get(VehicleCategory.STANDARD),
                    pricing.getCategoryPrices().get(VehicleCategory.FAMILY),
                    pricing.getCategoryPrices().get(VehicleCategory.LUXURY)
            });
        }
    }

    private JPanel createVehiclesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 16));
        panel.setBackground(GuiTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        panel.add(createVehicleModelsPanel());
        panel.add(createPhysicalVehiclesPanel());

        return panel;
    }

    private JPanel createVehicleModelsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Vehicle models"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton addButton = GuiTheme.createPrimaryButton("Add model");
        addButton.addActionListener(e -> showAddVehicleModelDialog());
        JButton editButton = GuiTheme.createSecondaryButton("Edit");
        editButton.addActionListener(e -> showEditVehicleModelDialog());
        JButton deleteButton = GuiTheme.createSecondaryButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedVehicleModel());
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Manufacturer", "Model", "Category"};
        vehicleModelsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleModelsTable = new JTable(vehicleModelsTableModel);
        GuiTheme.styleTable(vehicleModelsTable);
        panel.add(new JScrollPane(vehicleModelsTable), BorderLayout.CENTER);

        refreshVehicleModelsTable();
        return panel;
    }

    private JPanel createPhysicalVehiclesPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Vehicles"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton addButton = GuiTheme.createPrimaryButton("Add vehicle");
        addButton.addActionListener(e -> showAddVehicleDialog());
        JButton editButton = GuiTheme.createSecondaryButton("Edit");
        editButton.addActionListener(e -> showEditVehicleDialog());
        JButton deleteButton = GuiTheme.createSecondaryButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedVehicle());
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Registration", "Status", "Mileage", "Model"};
        vehiclesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehiclesTable = new JTable(vehiclesTableModel);
        GuiTheme.styleTable(vehiclesTable);
        panel.add(new JScrollPane(vehiclesTable), BorderLayout.CENTER);

        refreshVehiclesTable();
        return panel;
    }

    private void refreshVehicleModelsTable() {
        vehicleModelsTableModel.setRowCount(0);
        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            vehicleModelsTableModel.addRow(new Object[]{
                    vehicleModel.getId(),
                    vehicleModel.getManufacturer(),
                    vehicleModel.getModel(),
                    vehicleModel.getVehicleCategory()
            });
        }
    }

    private void refreshVehiclesTable() {
        vehiclesTableModel.setRowCount(0);
        for (Vehicle vehicle : app.getVehicleService().getVehicles()) {
            VehicleModel vehicleModel = vehicle.getVehicleModel();
            vehiclesTableModel.addRow(new Object[]{
                    vehicle.getId(),
                    vehicle.getRegistration(),
                    vehicle.getVehicleStatus(),
                    vehicle.getMileage(),
                    vehicleModel.getManufacturer() + " " + vehicleModel.getModel()
            });
        }
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(GuiTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        panel.add(createOperationsSummaryPanel(), BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 16));
        tablesPanel.setBackground(GuiTheme.BACKGROUND);
        tablesPanel.add(createOperationsReservationsPanel());
        tablesPanel.add(createOperationsRentalsPanel());
        panel.add(tablesPanel, BorderLayout.CENTER);

        refreshOperationsSummary();

        return panel;
    }

    private JPanel createOperationsSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBackground(GuiTheme.BACKGROUND);

        operationsPendingCountLabel = new JLabel("0", SwingConstants.CENTER);
        operationsApprovedCountLabel = new JLabel("0", SwingConstants.CENTER);
        operationsActiveRentalsCountLabel = new JLabel("0", SwingConstants.CENTER);
        operationsReturnedRentalsCountLabel = new JLabel("0", SwingConstants.CENTER);

        panel.add(createSmallMetricCard("Pending reservations", operationsPendingCountLabel));
        panel.add(createSmallMetricCard("Approved reservations", operationsApprovedCountLabel));
        panel.add(createSmallMetricCard("Active rentals", operationsActiveRentalsCountLabel));
        panel.add(createSmallMetricCard("Returned rentals", operationsReturnedRentalsCountLabel));

        return panel;
    }

    private JPanel createSmallMetricCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(GuiTheme.SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(GuiTheme.MUTED_TEXT);
        valueLabel.setFont(new Font("Avenir Next", Font.BOLD, 22));
        valueLabel.setForeground(GuiTheme.PRIMARY_DARK);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOperationsReservationsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("All reservations"), BorderLayout.WEST);

        JPanel actionPanel = createActionPanel();
        JButton approveButton = GuiTheme.createPrimaryButton("Approve");
        approveButton.addActionListener(e -> approveSelectedOperationReservation());
        JButton rejectButton = GuiTheme.createSecondaryButton("Reject");
        rejectButton.addActionListener(e -> rejectSelectedOperationReservation());
        JButton cancelButton = GuiTheme.createSecondaryButton("Cancel selected");
        cancelButton.addActionListener(e -> cancelSelectedOperationReservation());
        JButton refreshButton = GuiTheme.createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshOperationsTables());
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);
        actionPanel.add(cancelButton);
        actionPanel.add(refreshButton);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        operationsReservationStatusFilter = new JComboBox<>();
        operationsReservationStatusFilter.addItem("ANY");
        for (ReservationStatus status : ReservationStatus.values()) {
            operationsReservationStatusFilter.addItem(status.name());
        }
        operationsReservationStartDateFilter = createTextField("");
        operationsReservationEndDateFilter = createTextField("");
        operationsReservationSearchFilter = createTextField("");
        operationsReservationStartDateFilter.setColumns(9);
        operationsReservationEndDateFilter.setColumns(9);
        operationsReservationSearchFilter.setColumns(14);
        JButton applyFilterButton = GuiTheme.createSecondaryButton("Apply filters");
        applyFilterButton.addActionListener(e -> refreshOperationsReservationsTable());
        JButton clearFilterButton = GuiTheme.createSecondaryButton("Clear");
        clearFilterButton.addActionListener(e -> clearOperationsReservationFilters());

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(operationsReservationStatusFilter);
        filterPanel.add(new JLabel("Start:"));
        filterPanel.add(operationsReservationStartDateFilter);
        filterPanel.add(new JLabel("End:"));
        filterPanel.add(operationsReservationEndDateFilter);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(operationsReservationSearchFilter);
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);
        headerPanel.add(filterPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        operationsReservationsTableModel = new DefaultTableModel(new String[]{"ID", "Client", "Model", "Start", "End", "Status", "Vehicle", "Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        operationsReservationsTable = new JTable(operationsReservationsTableModel);
        GuiTheme.styleTable(operationsReservationsTable);
        panel.add(new JScrollPane(operationsReservationsTable), BorderLayout.CENTER);

        refreshOperationsReservationsTable();
        return panel;
    }

    private JPanel createOperationsRentalsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("All rentals"), BorderLayout.WEST);

        JButton refreshButton = GuiTheme.createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshOperationsTables());
        titlePanel.add(refreshButton, BorderLayout.EAST);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        operationsRentalStatusFilter = new JComboBox<>(new String[]{"ANY", "ACTIVE", "RETURNED"});
        operationsRentalStartDateFilter = createTextField("");
        operationsRentalEndDateFilter = createTextField("");
        operationsRentalSearchFilter = createTextField("");
        operationsRentalStartDateFilter.setColumns(9);
        operationsRentalEndDateFilter.setColumns(9);
        operationsRentalSearchFilter.setColumns(14);
        JButton applyFilterButton = GuiTheme.createSecondaryButton("Apply filters");
        applyFilterButton.addActionListener(e -> refreshOperationsRentalsTable());
        JButton clearFilterButton = GuiTheme.createSecondaryButton("Clear");
        clearFilterButton.addActionListener(e -> clearOperationsRentalFilters());

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(operationsRentalStatusFilter);
        filterPanel.add(new JLabel("Start:"));
        filterPanel.add(operationsRentalStartDateFilter);
        filterPanel.add(new JLabel("End:"));
        filterPanel.add(operationsRentalEndDateFilter);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(operationsRentalSearchFilter);
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);
        headerPanel.add(filterPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        operationsRentalsTableModel = new DefaultTableModel(new String[]{"ID", "Vehicle", "Client", "Agent", "Start", "Planned end", "Actual end", "Penalty"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(operationsRentalsTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshOperationsRentalsTable();
        return panel;
    }

    private void refreshOperationsTables() {
        refreshOperationsSummary();
        refreshOperationsReservationsTable();
        refreshOperationsRentalsTable();
    }

    private void refreshDerivedViews() {
        refreshOperationsTables();
        refreshCharts();
    }

    private void refreshOperationsSummary() {
        if (operationsPendingCountLabel == null) {
            return;
        }

        int pendingReservations = 0;
        int approvedReservations = 0;
        int activeRentals = 0;
        int returnedRentals = 0;

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getReservationStatus() == ReservationStatus.PENDING) {
                pendingReservations++;
            }
            if (reservation.getReservationStatus() == ReservationStatus.APPROVED) {
                approvedReservations++;
            }
        }

        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            if (rental.getActualEndRentDate() == null) {
                activeRentals++;
            } else {
                returnedRentals++;
            }
        }

        operationsPendingCountLabel.setText(String.valueOf(pendingReservations));
        operationsApprovedCountLabel.setText(String.valueOf(approvedReservations));
        operationsActiveRentalsCountLabel.setText(String.valueOf(activeRentals));
        operationsReturnedRentalsCountLabel.setText(String.valueOf(returnedRentals));
    }

    private void refreshOperationsReservationsTable() {
        if (operationsReservationsTableModel == null) {
            return;
        }

        operationsReservationsTableModel.setRowCount(0);
        ReservationStatus statusFilter = parseOperationsReservationStatusFilter();
        LocalDate startDateFilter;
        LocalDate endDateFilter;
        try {
            startDateFilter = parseOptionalFilterDate(operationsReservationStartDateFilter);
            endDateFilter = parseOptionalFilterDate(operationsReservationEndDateFilter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Operation filters must use yyyy-mm-dd date format.");
            return;
        }
        String searchFilter = operationsReservationSearchFilter == null ? "" : operationsReservationSearchFilter.getText().trim().toLowerCase();

        if (startDateFilter != null && endDateFilter != null && endDateFilter.isBefore(startDateFilter)) {
            JOptionPane.showMessageDialog(this, "Operation reservation end filter cannot be before start filter.");
            return;
        }

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (statusFilter != null && reservation.getReservationStatus() != statusFilter) {
                continue;
            }

            if (startDateFilter != null && reservation.getEndDate().isBefore(startDateFilter)) {
                continue;
            }

            if (endDateFilter != null && reservation.getStartDate().isAfter(endDateFilter)) {
                continue;
            }

            VehicleModel vehicleModel = reservation.getVehicleModel();
            String searchableText = (reservation.getClient().getName() + " " +
                    reservation.getClient().getLastName() + " " +
                    reservation.getClient().getUserName() + " " +
                    vehicleModel.getManufacturer() + " " +
                    vehicleModel.getModel()).toLowerCase();
            if (!searchFilter.isEmpty() && !searchableText.contains(searchFilter)) {
                continue;
            }

            Vehicle assignedVehicle = reservation.getAssignedVehicle();
            operationsReservationsTableModel.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getClient().getName() + " " + reservation.getClient().getLastName(),
                    vehicleModel.getManufacturer() + " " + vehicleModel.getModel(),
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getReservationStatus(),
                    assignedVehicle == null ? "" : assignedVehicle.getRegistration(),
                    reservation.getPrice()
            });
        }
    }

    private ReservationStatus parseOperationsReservationStatusFilter() {
        if (operationsReservationStatusFilter == null || operationsReservationStatusFilter.getSelectedItem() == null) {
            return null;
        }

        String value = String.valueOf(operationsReservationStatusFilter.getSelectedItem());
        if (value.equals("ANY")) {
            return null;
        }
        return ReservationStatus.valueOf(value);
    }

    private LocalDate parseOptionalFilterDate(JTextField textField) {
        if (textField == null) {
            return null;
        }

        String value = textField.getText().trim();
        if (value.isEmpty()) {
            return null;
        }

        return LocalDate.parse(value);
    }

    private void clearOperationsReservationFilters() {
        operationsReservationStatusFilter.setSelectedItem("ANY");
        operationsReservationStartDateFilter.setText("");
        operationsReservationEndDateFilter.setText("");
        operationsReservationSearchFilter.setText("");
        refreshOperationsReservationsTable();
    }

    private void refreshOperationsRentalsTable() {
        if (operationsRentalsTableModel == null) {
            return;
        }

        operationsRentalsTableModel.setRowCount(0);
        String statusFilter = operationsRentalStatusFilter == null ? "ANY" : String.valueOf(operationsRentalStatusFilter.getSelectedItem());
        LocalDate startDateFilter;
        LocalDate endDateFilter;
        try {
            startDateFilter = parseOptionalFilterDate(operationsRentalStartDateFilter);
            endDateFilter = parseOptionalFilterDate(operationsRentalEndDateFilter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Operation filters must use yyyy-mm-dd date format.");
            return;
        }

        if (startDateFilter != null && endDateFilter != null && endDateFilter.isBefore(startDateFilter)) {
            JOptionPane.showMessageDialog(this, "Operation rental end filter cannot be before start filter.");
            return;
        }

        String searchFilter = operationsRentalSearchFilter == null ? "" : operationsRentalSearchFilter.getText().trim().toLowerCase();

        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            boolean active = rental.getActualEndRentDate() == null;
            if ("ACTIVE".equals(statusFilter) && !active) {
                continue;
            }
            if ("RETURNED".equals(statusFilter) && active) {
                continue;
            }

            LocalDate rentalEffectiveEndDate = rental.getActualEndRentDate() == null ? rental.getEndRentDate() : rental.getActualEndRentDate();
            if (startDateFilter != null && rentalEffectiveEndDate.isBefore(startDateFilter)) {
                continue;
            }
            if (endDateFilter != null && rental.getStartRentDate().isAfter(endDateFilter)) {
                continue;
            }

            Reservation reservation = rental.getReservation();
            Agent rentalAgent = rental.getAgent();
            String searchableText = ((rental.getVehicle() == null ? "" : rental.getVehicle().getRegistration()) + " " +
                    (reservation == null ? "" : reservation.getClient().getName() + " " + reservation.getClient().getLastName() + " " + reservation.getClient().getUserName()) + " " +
                    (rentalAgent == null ? "" : rentalAgent.getName() + " " + rentalAgent.getLastName() + " " + rentalAgent.getUserName())).toLowerCase();
            if (!searchFilter.isEmpty() && !searchableText.contains(searchFilter)) {
                continue;
            }

            operationsRentalsTableModel.addRow(new Object[]{
                    rental.getId(),
                    rental.getVehicle() == null ? "" : rental.getVehicle().getRegistration(),
                    reservation == null ? "" : reservation.getClient().getName() + " " + reservation.getClient().getLastName(),
                    rentalAgent == null ? "" : rentalAgent.getName() + " " + rentalAgent.getLastName(),
                    rental.getStartRentDate(),
                    rental.getEndRentDate(),
                    rental.getActualEndRentDate() == null ? "" : rental.getActualEndRentDate(),
                    rental.getPenaltyForBeingLate()
            });
        }
    }

    private void clearOperationsRentalFilters() {
        operationsRentalStatusFilter.setSelectedItem("ANY");
        operationsRentalStartDateFilter.setText("");
        operationsRentalEndDateFilter.setText("");
        operationsRentalSearchFilter.setText("");
        refreshOperationsRentalsTable();
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

    private void cancelSelectedOperationReservation() {
        Reservation reservation = findReservationById(getSelectedId(operationsReservationsTable));
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        boolean canceled = app.getReservationService().cancelReservationByStaff(reservation);
        if (!canceled) {
            JOptionPane.showMessageDialog(this, app.getReservationService().getReservationCancellationError(reservation));
            return;
        }

        app.saveAll();
        refreshOperationsTables();
        refreshCharts();
        JOptionPane.showMessageDialog(this, "Reservation canceled.");
    }

    private void approveSelectedOperationReservation() {
        Reservation reservation = findReservationById(getSelectedId(operationsReservationsTable));
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        boolean approved = app.getReservationService().approveReservation(reservation);
        if (!approved) {
            JOptionPane.showMessageDialog(this, "Reservation cannot be approved. It must be pending and have available vehicles.");
            return;
        }

        app.saveAll();
        refreshOperationsTables();
        refreshCharts();
        JOptionPane.showMessageDialog(this, "Reservation approved.");
    }

    private void rejectSelectedOperationReservation() {
        Reservation reservation = findReservationById(getSelectedId(operationsReservationsTable));
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        boolean rejected = app.getReservationService().rejectReservation(reservation);
        if (!rejected) {
            JOptionPane.showMessageDialog(this, "Only pending reservations can be rejected.");
            return;
        }

        app.saveAll();
        refreshOperationsTables();
        refreshCharts();
        JOptionPane.showMessageDialog(this, "Reservation rejected.");
    }

    private JPanel createReportsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        JTextField startDateField = createTextField("2026-01-01");
        JTextField endDateField = createTextField("2026-12-31");
        JButton refreshButton = GuiTheme.createPrimaryButton("Refresh reports");

        filterPanel.add(new JLabel("Start date:"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("End date:"));
        filterPanel.add(endDateField);
        filterPanel.add(refreshButton);
        panel.add(filterPanel, BorderLayout.NORTH);

        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.addTab("Rentals by agent", createRentalsByAgentReportPanel());
        reportTabs.addTab("Reservation statuses", createReservationStatusesReportPanel());
        reportTabs.addTab("Model stats", createModelReportsPanel());
        reportTabs.addTab("Financial", createFinancialReportPanel());
        reportTabs.addTab("Charts", createChartsPanel());
        panel.add(reportTabs, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refreshReports(startDateField, endDateField));
        refreshReports(startDateField, endDateField);

        return wrapWithTopMargin(panel);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("System settings"));

        JLabel hintLabel = new JLabel("Default rental duration affects extended-use extra service pricing.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField defaultDurationField = createTextField(String.valueOf(app.getDefaultRentalDurationDays()));
        addFormRow(formPanel, gbc, 0, "Default rental duration days", defaultDurationField);
        panel.add(formPanel, BorderLayout.CENTER);

        JButton saveButton = GuiTheme.createPrimaryButton("Save settings");
        saveButton.addActionListener(e -> saveSettings(defaultDurationField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return wrapWithTopMargin(panel);
    }

    private void saveSettings(JTextField defaultDurationField) {
        try {
            int defaultDuration = Integer.parseInt(defaultDurationField.getText().trim());
            if (defaultDuration <= 0) {
                JOptionPane.showMessageDialog(this, "Default rental duration must be greater than zero.");
                return;
            }

            app.setDefaultRentalDurationDays(defaultDuration);
            app.saveAll();
            JOptionPane.showMessageDialog(this, "Settings saved.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Default rental duration must be a number.");
        }
    }

    private JPanel createRentalsByAgentReportPanel() {
        rentalsByAgentTableModel = new DefaultTableModel(new String[]{"Agent", "Issued vehicles"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return createReportTablePanel(rentalsByAgentTableModel);
    }

    private JPanel createReservationStatusesReportPanel() {
        reservationStatusesTableModel = new DefaultTableModel(new String[]{"Status", "Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return createReportTablePanel(reservationStatusesTableModel);
    }

    private JPanel createModelReportsPanel() {
        modelReportsTableModel = new DefaultTableModel(new String[]{"Model", "Category", "Reservations", "Rentals"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return createReportTablePanel(modelReportsTableModel);
    }

    private JPanel createFinancialReportPanel() {
        financialReportTableModel = new DefaultTableModel(new String[]{"Metric", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return createReportTablePanel(financialReportTableModel);
    }

    private JPanel createChartsPanel() {
        chartDashboardPanel = new XChartDashboardPanel();

        JScrollPane scrollPane = new JScrollPane(chartDashboardPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GuiTheme.BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportTablePanel(DefaultTableModel tableModel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GuiTheme.SURFACE);
        JTable table = new JTable(tableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshReports(JTextField startDateField, JTextField endDateField) {
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateField.getText().trim());
            endDate = LocalDate.parse(endDateField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must use yyyy-mm-dd format.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
            return;
        }

        refreshRentalsByAgentReport(startDate, endDate);
        refreshReservationStatusesReport(startDate, endDate);
        refreshModelReports(startDate, endDate);
        refreshFinancialReport(startDate, endDate);
        refreshCharts();
    }

    private void refreshRentalsByAgentReport(LocalDate startDate, LocalDate endDate) {
        rentalsByAgentTableModel.setRowCount(0);
        for (Map.Entry<String, Integer> entry : app.getReportService().countRentalsByAgent(startDate, endDate).entrySet()) {
            rentalsByAgentTableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void refreshReservationStatusesReport(LocalDate startDate, LocalDate endDate) {
        reservationStatusesTableModel.setRowCount(0);
        for (Map.Entry<ReservationStatus, Integer> entry : app.getReportService().countReservationsByStatus(startDate, endDate).entrySet()) {
            reservationStatusesTableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void refreshModelReports(LocalDate startDate, LocalDate endDate) {
        modelReportsTableModel.setRowCount(0);
        for (ReportService.ModelReport report : app.getReportService().getModelReports(startDate, endDate).values()) {
            modelReportsTableModel.addRow(new Object[]{
                    report.getModelName(),
                    report.getCategory(),
                    report.getReservationCount(),
                    report.getRentalCount()
            });
        }
    }

    private void refreshFinancialReport(LocalDate startDate, LocalDate endDate) {
        financialReportTableModel.setRowCount(0);
        ReportService.FinancialReport report = app.getReportService().getFinancialReport(startDate, endDate);
        financialReportTableModel.addRow(new Object[]{"Subscription revenue", report.getSubscriptionRevenue()});
        financialReportTableModel.addRow(new Object[]{"Rental revenue", report.getRentalRevenue()});
        financialReportTableModel.addRow(new Object[]{"Extra service revenue", report.getExtraServiceRevenue()});
        financialReportTableModel.addRow(new Object[]{"Penalty revenue", report.getPenaltyRevenue()});
        financialReportTableModel.addRow(new Object[]{"Total revenue", report.getTotalRevenue()});
        financialReportTableModel.addRow(new Object[]{"Salary expenses", report.getSalaryExpenses()});
        financialReportTableModel.addRow(new Object[]{"Profit", report.getProfit()});
    }

    private void refreshCharts() {
        if (chartDashboardPanel == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(29);

        chartDashboardPanel.updateCharts(
                app.getReportService().getMonthlyRevenueByClientCategory(today),
                app.getReportService().countProcessedReservationsByAgent(thirtyDaysAgo, today),
                toStringIntegerMap(app.getReportService().countReservationsByStatus(thirtyDaysAgo, today))
        );
    }

    private Map<String, Double> toDoubleMap(Map<String, Integer> source) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            result.put(entry.getKey(), entry.getValue().doubleValue());
        }
        return result;
    }

    private Map<String, Double> toStringDoubleMap(Map<?, ? extends Number> source) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<?, ? extends Number> entry : source.entrySet()) {
            Number value = entry.getValue();
            result.put(String.valueOf(entry.getKey()), value == null ? 0 : value.doubleValue());
        }
        return result;
    }

    private Map<String, Integer> toStringIntegerMap(Map<?, Integer> source) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<?, Integer> entry : source.entrySet()) {
            Integer value = entry.getValue();
            result.put(String.valueOf(entry.getKey()), value == null ? 0 : value);
        }
        return result;
    }

    private void refreshAgentsTable() {
        agentsTableModel.setRowCount(0);
        for (Agent agent : app.getAgents()) {
            agentsTableModel.addRow(new Object[]{
                    agent.getId(),
                    agent.getName() + " " + agent.getLastName(),
                    agent.getUserName(),
                    agent.getLevelOfEducation(),
                    agent.getYearsOfExperience(),
                    agent.getSalary()
            });
        }
    }

    private void refreshAdministratorsTable() {
        administratorsTableModel.setRowCount(0);
        for (Administrator administrator : app.getAdministrators()) {
            administratorsTableModel.addRow(new Object[]{
                    administrator.getId(),
                    administrator.getName() + " " + administrator.getLastName(),
                    administrator.getUserName(),
                    administrator.getLevelOfEducation(),
                    administrator.getYearsOfExperience(),
                    administrator.getSalary()
            });
        }
    }

    private void refreshClientsTable() {
        clientsTableModel.setRowCount(0);
        for (Client client : app.getClientService().getClients()) {
            Subscription subscription = client.getSubscription();
            clientsTableModel.addRow(new Object[]{
                    client.getId(),
                    client.getName() + " " + client.getLastName(),
                    client.getUserName(),
                    client.getDriversLicenseIssueDate(),
                    client.getClientCategory(),
                    client.getLateReturnsCount(),
                    subscription == null ? "" : subscription.getSubscriptionStatus(),
                    subscription == null ? "" : subscription.getExpirationDate()
            });
        }
    }

    private void showAddClientDialog() {
        ClientFormData formData = showClientFormDialog("Add client", null);
        if (formData == null) {
            return;
        }

        if (isUsernameTaken(formData.userName, -1)) {
            JOptionPane.showMessageDialog(this, "Username is already used.");
            return;
        }

        Subscription subscription = new Subscription(app.getNextSubscriptionId(), formData.subscriptionStatus, formData.subscriptionExpirationDate);
        Client client = new Client(
                app.getNextClientId(),
                formData.name,
                formData.lastName,
                formData.gender,
                formData.birthday,
                formData.phoneNumber,
                formData.address,
                formData.userName,
                formData.password,
                formData.driversLicenseIssueDate,
                formData.clientCategory,
                subscription
        );
        client.setLateReturnsCount(formData.lateReturnsCount);

        app.getClientService().addClient(client);
        app.getSubscriptionService().addSubscription(subscription);
        app.saveAll();
        refreshClientsTable();
        refreshDerivedViews();
    }

    private void showEditClientDialog() {
        Client client = findClientById(getSelectedId(clientsTable));
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Select a client first.");
            return;
        }

        ClientFormData formData = showClientFormDialog("Edit client", client);
        if (formData == null) {
            return;
        }

        if (isUsernameTaken(formData.userName, client.getId())) {
            JOptionPane.showMessageDialog(this, "Username is already used.");
            return;
        }

        updateClient(client, formData);
        app.saveAll();
        refreshClientsTable();
        refreshDerivedViews();
    }

    private void deleteSelectedClient() {
        Client client = findClientById(getSelectedId(clientsTable));
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Select a client first.");
            return;
        }

        if (isClientUsed(client)) {
            JOptionPane.showMessageDialog(this, "Client cannot be deleted because there are reservations or rentals for this client.");
            return;
        }

        if (!confirmDelete("Delete selected client?")) {
            return;
        }

        if (client.getSubscription() != null) {
            app.getSubscriptionService().removeSubscription(client.getSubscription());
        }
        app.getClientService().removeClient(client);
        app.saveAll();
        refreshClientsTable();
        refreshDerivedViews();
    }

    private void showEditAgentDialog() {
        Agent agent = findAgentById(getSelectedId(agentsTable));
        if (agent == null) {
            JOptionPane.showMessageDialog(this, "Select an agent first.");
            return;
        }

        EmployeeFormData initialData = createEmployeeFormData(agent);
        EmployeeFormData formData = showEmployeeFormDialog("Edit agent", initialData);
        if (formData == null) {
            return;
        }

        if (isUsernameTaken(formData.userName, agent.getId())) {
            JOptionPane.showMessageDialog(this, "Username is already used.");
            return;
        }

        updateEmployee(agent, formData);
        app.saveAll();
        refreshAgentsTable();
        refreshDerivedViews();
    }

    private void deleteSelectedAgent() {
        Agent agent = findAgentById(getSelectedId(agentsTable));
        if (agent == null) {
            JOptionPane.showMessageDialog(this, "Select an agent first.");
            return;
        }

        if (isAgentUsedByRental(agent)) {
            JOptionPane.showMessageDialog(this, "Agent cannot be deleted because there are rentals issued by this agent.");
            return;
        }

        if (!confirmDelete("Delete selected agent?")) {
            return;
        }

        app.getAgents().remove(agent);
        app.saveAll();
        refreshAgentsTable();
        refreshDerivedViews();
    }

    private void showEditAdministratorDialog() {
        Administrator selectedAdministrator = findAdministratorById(getSelectedId(administratorsTable));
        if (selectedAdministrator == null) {
            JOptionPane.showMessageDialog(this, "Select an administrator first.");
            return;
        }

        EmployeeFormData initialData = createEmployeeFormData(selectedAdministrator);
        EmployeeFormData formData = showEmployeeFormDialog("Edit administrator", initialData);
        if (formData == null) {
            return;
        }

        if (isUsernameTaken(formData.userName, selectedAdministrator.getId())) {
            JOptionPane.showMessageDialog(this, "Username is already used.");
            return;
        }

        updateEmployee(selectedAdministrator, formData);
        if (selectedAdministrator.getId() == administrator.getId()) {
            administrator = selectedAdministrator;
            setTitle("Administrator - " + administrator.getName() + " " + administrator.getLastName());
        }
        app.saveAll();
        refreshAdministratorsTable();
        refreshDerivedViews();
    }

    private void deleteSelectedAdministrator() {
        Administrator selectedAdministrator = findAdministratorById(getSelectedId(administratorsTable));
        if (selectedAdministrator == null) {
            JOptionPane.showMessageDialog(this, "Select an administrator first.");
            return;
        }

        if (selectedAdministrator.getId() == administrator.getId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete the currently logged in administrator.");
            return;
        }

        if (app.getAdministrators().size() <= 1) {
            JOptionPane.showMessageDialog(this, "At least one administrator must remain.");
            return;
        }

        if (!confirmDelete("Delete selected administrator?")) {
            return;
        }

        app.getAdministrators().remove(selectedAdministrator);
        app.saveAll();
        refreshAdministratorsTable();
        refreshDerivedViews();
    }

    private void showAddAgentDialog() {
        EmployeeFormData formData = showEmployeeFormDialog("Add agent");
        if (formData == null) {
            return;
        }

        if (isUsernameTaken(formData.userName, -1)) {
            JOptionPane.showMessageDialog(this, "Username is already used.");
            return;
        }

        Agent agent = new Agent(
                app.getNextAgentId(),
                formData.name,
                formData.lastName,
                formData.gender,
                formData.birthday,
                formData.phoneNumber,
                formData.address,
                formData.userName,
                formData.password,
                formData.levelOfEducation,
                formData.yearsOfExperience
        );

        app.getAgents().add(agent);
        app.saveAll();
        refreshAgentsTable();
        refreshDerivedViews();
    }

    private void showAddAdministratorDialog() {
        EmployeeFormData formData = showEmployeeFormDialog("Add administrator");
        if (formData == null) {
            return;
        }

        if (isUsernameTaken(formData.userName, -1)) {
            JOptionPane.showMessageDialog(this, "Username is already used.");
            return;
        }

        Administrator administrator = new Administrator(
                app.getNextAdministratorId(),
                formData.name,
                formData.lastName,
                formData.gender,
                formData.birthday,
                formData.phoneNumber,
                formData.address,
                formData.userName,
                formData.password,
                formData.levelOfEducation,
                formData.yearsOfExperience
        );

        app.getAdministrators().add(administrator);
        app.saveAll();
        refreshAdministratorsTable();
        refreshDerivedViews();
    }

    private void showEditPricingDialog() {
        Pricing pricing = findPricingById(getSelectedId(pricingTable));
        if (pricing == null) {
            JOptionPane.showMessageDialog(this, "Select a pricing row first.");
            return;
        }

        PricingFormData formData = showPricingFormDialog("Edit pricing", pricing);
        if (formData == null) {
            return;
        }

        if (hasOverlappingPricing(formData.startDate, formData.endDate, pricing.getId())) {
            JOptionPane.showMessageDialog(this, "Pricing period overlaps with an existing pricing period.");
            return;
        }

        pricing.setStartDate(formData.startDate);
        pricing.setEndDate(formData.endDate);
        pricing.setAnnualSubscription(formData.annualSubscription);
        pricing.setPenaltyPerDay(formData.penaltyPerDay);
        pricing.setCategoryPrices(formData.categoryPrices);
        pricing.setExtraServicesPrices(formData.extraServicePrices);
        pricing.setClientCategoryDiscounts(formData.clientDiscounts);

        app.saveAll();
        refreshPricingTable();
        refreshDerivedViews();
    }

    private void deleteSelectedPricing() {
        Pricing pricing = findPricingById(getSelectedId(pricingTable));
        if (pricing == null) {
            JOptionPane.showMessageDialog(this, "Select a pricing row first.");
            return;
        }

        if (app.getPricingService().getPricings().size() <= 1) {
            JOptionPane.showMessageDialog(this, "At least one pricing period should remain.");
            return;
        }

        if (!confirmDelete("Delete selected pricing period?")) {
            return;
        }

        app.getPricingService().removePricing(pricing);
        app.saveAll();
        refreshPricingTable();
        refreshDerivedViews();
    }

    private void showAddPricingDialog() {
        PricingFormData formData = showPricingFormDialog("Add pricing", null);
        if (formData == null) {
            return;
        }

        if (hasOverlappingPricing(formData.startDate, formData.endDate, -1)) {
            JOptionPane.showMessageDialog(this, "Pricing period overlaps with an existing pricing period.");
            return;
        }

        Pricing pricing = new Pricing(
                app.getNextPricingId(),
                formData.startDate,
                formData.endDate,
                formData.annualSubscription,
                formData.penaltyPerDay,
                formData.categoryPrices,
                formData.extraServicePrices,
                formData.clientDiscounts
        );

        app.getPricingService().addPricing(pricing);
        app.saveAll();
        refreshPricingTable();
        refreshDerivedViews();
    }

    private void showAddVehicleModelDialog() {
        JTextField manufacturerField = createTextField();
        JTextField modelField = createTextField();
        JComboBox<VehicleCategory> categoryComboBox = new JComboBox<>(VehicleCategory.values());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add vehicle model", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String manufacturer = manufacturerField.getText().trim();
        String model = modelField.getText().trim();
        VehicleCategory category = (VehicleCategory) categoryComboBox.getSelectedItem();

        if (manufacturer.isEmpty() || model.isEmpty() || category == null) {
            JOptionPane.showMessageDialog(this, "Manufacturer, model and category are required.");
            return;
        }

        VehicleModel vehicleModel = new VehicleModel(app.getNextVehicleModelId(), manufacturer, model, category);
        app.getVehicleModels().add(vehicleModel);
        app.saveAll();
        refreshVehicleModelsTable();
        refreshDerivedViews();
    }

    private void showEditVehicleModelDialog() {
        VehicleModel vehicleModel = findVehicleModelById(getSelectedId(vehicleModelsTable));
        if (vehicleModel == null) {
            JOptionPane.showMessageDialog(this, "Select a vehicle model first.");
            return;
        }

        JTextField manufacturerField = createTextField(vehicleModel.getManufacturer());
        JTextField modelField = createTextField(vehicleModel.getModel());
        JComboBox<VehicleCategory> categoryComboBox = new JComboBox<>(VehicleCategory.values());
        categoryComboBox.setSelectedItem(vehicleModel.getVehicleCategory());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit vehicle model", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String manufacturer = manufacturerField.getText().trim();
        String model = modelField.getText().trim();
        VehicleCategory category = (VehicleCategory) categoryComboBox.getSelectedItem();

        if (manufacturer.isEmpty() || model.isEmpty() || category == null) {
            JOptionPane.showMessageDialog(this, "Manufacturer, model and category are required.");
            return;
        }

        vehicleModel.setManufacturer(manufacturer);
        vehicleModel.setModel(model);
        vehicleModel.setVehicleCategory(category);
        app.saveAll();
        refreshVehicleModelsTable();
        refreshVehiclesTable();
        refreshDerivedViews();
    }

    private void deleteSelectedVehicleModel() {
        VehicleModel vehicleModel = findVehicleModelById(getSelectedId(vehicleModelsTable));
        if (vehicleModel == null) {
            JOptionPane.showMessageDialog(this, "Select a vehicle model first.");
            return;
        }

        if (isVehicleModelUsed(vehicleModel)) {
            JOptionPane.showMessageDialog(this, "Vehicle model cannot be deleted because it is used by vehicles or reservations.");
            return;
        }

        if (!confirmDelete("Delete selected vehicle model?")) {
            return;
        }

        app.getVehicleModels().remove(vehicleModel);
        app.saveAll();
        refreshVehicleModelsTable();
        refreshDerivedViews();
    }

    private void showAddVehicleDialog() {
        JTextField registrationField = createTextField();
        JComboBox<VehicleStatus> statusComboBox = new JComboBox<>(VehicleStatus.values());
        JTextField mileageField = createTextField("0");
        JComboBox<VehicleModel> modelComboBox = new JComboBox<>();

        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            modelComboBox.addItem(vehicleModel);
        }
        modelComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> createVehicleModelComboLabel(value, isSelected, list));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Registration:"));
        panel.add(registrationField);
        panel.add(new JLabel("Status:"));
        panel.add(statusComboBox);
        panel.add(new JLabel("Mileage:"));
        panel.add(mileageField);
        panel.add(new JLabel("Vehicle model:"));
        panel.add(modelComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add vehicle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String registration = registrationField.getText().trim();
            VehicleStatus status = (VehicleStatus) statusComboBox.getSelectedItem();
            int mileage = Integer.parseInt(mileageField.getText().trim());
            VehicleModel vehicleModel = (VehicleModel) modelComboBox.getSelectedItem();

            if (registration.isEmpty() || status == null || vehicleModel == null) {
                JOptionPane.showMessageDialog(this, "Registration, status and model are required.");
                return;
            }

            if (mileage < 0) {
                JOptionPane.showMessageDialog(this, "Mileage cannot be negative.");
                return;
            }

            if (isRegistrationTaken(registration, -1)) {
                JOptionPane.showMessageDialog(this, "Registration is already used.");
                return;
            }

            Vehicle vehicle = new Vehicle(app.getNextVehicleId(), registration, status, mileage, vehicleModel);
            app.getVehicleService().addVehicle(vehicle);
            app.saveAll();
            refreshVehiclesTable();
            refreshDerivedViews();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mileage must be a number.");
        }
    }

    private void showEditVehicleDialog() {
        Vehicle vehicle = findVehicleById(getSelectedId(vehiclesTable));
        if (vehicle == null) {
            JOptionPane.showMessageDialog(this, "Select a vehicle first.");
            return;
        }

        JTextField registrationField = createTextField(vehicle.getRegistration());
        JComboBox<VehicleStatus> statusComboBox = new JComboBox<>(VehicleStatus.values());
        statusComboBox.setSelectedItem(vehicle.getVehicleStatus());
        JTextField mileageField = createTextField(String.valueOf(vehicle.getMileage()));
        JComboBox<VehicleModel> modelComboBox = new JComboBox<>();

        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            modelComboBox.addItem(vehicleModel);
        }
        modelComboBox.setSelectedItem(vehicle.getVehicleModel());
        modelComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> createVehicleModelComboLabel(value, isSelected, list));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Registration:"));
        panel.add(registrationField);
        panel.add(new JLabel("Status:"));
        panel.add(statusComboBox);
        panel.add(new JLabel("Mileage:"));
        panel.add(mileageField);
        panel.add(new JLabel("Vehicle model:"));
        panel.add(modelComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit vehicle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String registration = registrationField.getText().trim();
            VehicleStatus status = (VehicleStatus) statusComboBox.getSelectedItem();
            int mileage = Integer.parseInt(mileageField.getText().trim());
            VehicleModel vehicleModel = (VehicleModel) modelComboBox.getSelectedItem();

            if (registration.isEmpty() || status == null || vehicleModel == null) {
                JOptionPane.showMessageDialog(this, "Registration, status and model are required.");
                return;
            }

            if (mileage < 0) {
                JOptionPane.showMessageDialog(this, "Mileage cannot be negative.");
                return;
            }

            if (isRegistrationTaken(registration, vehicle.getId())) {
                JOptionPane.showMessageDialog(this, "Registration is already used.");
                return;
            }

            boolean hasActiveRental = hasActiveRental(vehicle);
            if (hasActiveRental && status != VehicleStatus.RENTED) {
                JOptionPane.showMessageDialog(this, "Vehicle has an active rental and must stay RENTED.");
                return;
            }

            if (!hasActiveRental && status == VehicleStatus.RENTED) {
                JOptionPane.showMessageDialog(this, "Vehicle cannot be set to RENTED without an active rental.");
                return;
            }

            vehicle.setRegistration(registration);
            vehicle.setVehicleStatus(status);
            vehicle.setMileage(mileage);
            vehicle.setVehicleModel(vehicleModel);
            app.saveAll();
            refreshVehiclesTable();
            refreshDerivedViews();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mileage must be a number.");
        }
    }

    private void deleteSelectedVehicle() {
        Vehicle vehicle = findVehicleById(getSelectedId(vehiclesTable));
        if (vehicle == null) {
            JOptionPane.showMessageDialog(this, "Select a vehicle first.");
            return;
        }

        if (isVehicleUsed(vehicle)) {
            JOptionPane.showMessageDialog(this, "Vehicle cannot be deleted because it is used by reservations or rentals.");
            return;
        }

        if (!confirmDelete("Delete selected vehicle?")) {
            return;
        }

        app.getVehicleService().removeVehicle(vehicle);
        app.saveAll();
        refreshVehiclesTable();
        refreshDerivedViews();
    }

    private EmployeeFormData showEmployeeFormDialog(String title) {
        return showEmployeeFormDialog(title, null);
    }

    private EmployeeFormData showEmployeeFormDialog(String title, EmployeeFormData initialData) {
        JTextField nameField = createTextField(initialData == null ? "" : initialData.name);
        JTextField lastNameField = createTextField(initialData == null ? "" : initialData.lastName);
        JTextField genderField = createTextField(initialData == null ? "" : initialData.gender);
        JTextField birthdayField = createTextField(initialData == null ? "1990-01-01" : initialData.birthday.toString());
        JTextField phoneField = createTextField(initialData == null ? "" : initialData.phoneNumber);
        JTextField addressField = createTextField(initialData == null ? "" : initialData.address);
        JTextField usernameField = createTextField(initialData == null ? "" : initialData.userName);
        JPasswordField passwordField = createPasswordField();
        if (initialData != null) {
            passwordField.setText(initialData.password);
        }
        JComboBox<LevelOfEducation> educationComboBox = new JComboBox<>(LevelOfEducation.values());
        if (initialData != null) {
            educationComboBox.setSelectedItem(initialData.levelOfEducation);
        }
        JTextField experienceField = createTextField(initialData == null ? "0" : String.valueOf(initialData.yearsOfExperience));

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
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Education:"));
        panel.add(educationComboBox);
        panel.add(new JLabel("Years of experience:"));
        panel.add(experienceField);

        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            EmployeeFormData formData = new EmployeeFormData();
            formData.name = nameField.getText().trim();
            formData.lastName = lastNameField.getText().trim();
            formData.gender = genderField.getText().trim();
            formData.birthday = LocalDate.parse(birthdayField.getText().trim());
            formData.phoneNumber = phoneField.getText().trim();
            formData.address = addressField.getText().trim();
            formData.userName = usernameField.getText().trim();
            formData.password = new String(passwordField.getPassword());
            formData.levelOfEducation = (LevelOfEducation) educationComboBox.getSelectedItem();
            formData.yearsOfExperience = Integer.parseInt(experienceField.getText().trim());

            if (formData.name.isEmpty() || formData.lastName.isEmpty() || formData.userName.isEmpty() || formData.password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, last name, username and password are required.");
                return null;
            }

            if (formData.yearsOfExperience < 0) {
                JOptionPane.showMessageDialog(this, "Years of experience cannot be negative.");
                return null;
            }

            return formData;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Birthday must use yyyy-mm-dd format.");
            return null;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Years of experience must be a number.");
            return null;
        }
    }

    private PricingFormData showPricingFormDialog(String title, Pricing initialPricing) {
        JTextField startDateField = createTextField(initialPricing == null ? "2027-01-01" : initialPricing.getStartDate().toString());
        JTextField endDateField = createTextField(initialPricing == null ? "2027-12-31" : initialPricing.getEndDate().toString());
        JTextField annualSubscriptionField = createTextField(String.valueOf(initialPricing == null ? 16000 : initialPricing.getAnnualSubscription()));
        JTextField penaltyPerDayField = createTextField(String.valueOf(initialPricing == null ? 4000 : initialPricing.getPenaltyPerDay()));

        JTextField economyField = createTextField(String.valueOf(getPricingMapValue(initialPricing, VehicleCategory.ECONOMY, 3900)));
        JTextField standardField = createTextField(String.valueOf(getPricingMapValue(initialPricing, VehicleCategory.STANDARD, 5600)));
        JTextField familyField = createTextField(String.valueOf(getPricingMapValue(initialPricing, VehicleCategory.FAMILY, 7600)));
        JTextField luxuryField = createTextField(String.valueOf(getPricingMapValue(initialPricing, VehicleCategory.LUXURY, 15000)));

        JTextField gpsField = createTextField(String.valueOf(getExtraServiceMapValue(initialPricing, ExtraService.GPS, 900)));
        JTextField childSeatField = createTextField(String.valueOf(getExtraServiceMapValue(initialPricing, ExtraService.CHILD_SEAT, 1100)));
        JTextField insuranceField = createTextField(String.valueOf(getExtraServiceMapValue(initialPricing, ExtraService.ADDITIONAL_INSURANCE, 2100)));
        JTextField extendedUseField = createTextField(String.valueOf(getExtraServiceMapValue(initialPricing, ExtraService.EXTENDED_USE, 3200)));

        JTextField studentDiscountField = createTextField(String.valueOf(getClientDiscountMapValue(initialPricing, ClientCategory.STUDENT, 0.12)));
        JTextField pensionerDiscountField = createTextField(String.valueOf(getClientDiscountMapValue(initialPricing, ClientCategory.PENSIONER, 0.18)));
        JTextField companyDiscountField = createTextField(String.valueOf(getClientDiscountMapValue(initialPricing, ClientCategory.COMPANY, 0.10)));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Start date (yyyy-mm-dd):"));
        panel.add(startDateField);
        panel.add(new JLabel("End date (yyyy-mm-dd):"));
        panel.add(endDateField);
        panel.add(new JLabel("Annual subscription:"));
        panel.add(annualSubscriptionField);
        panel.add(new JLabel("Penalty per day:"));
        panel.add(penaltyPerDayField);
        panel.add(new JLabel("Economy daily price:"));
        panel.add(economyField);
        panel.add(new JLabel("Standard daily price:"));
        panel.add(standardField);
        panel.add(new JLabel("Family daily price:"));
        panel.add(familyField);
        panel.add(new JLabel("Luxury daily price:"));
        panel.add(luxuryField);
        panel.add(new JLabel("GPS price:"));
        panel.add(gpsField);
        panel.add(new JLabel("Child seat price:"));
        panel.add(childSeatField);
        panel.add(new JLabel("Additional insurance price:"));
        panel.add(insuranceField);
        panel.add(new JLabel("Extended use price/day:"));
        panel.add(extendedUseField);
        panel.add(new JLabel("Student discount:"));
        panel.add(studentDiscountField);
        panel.add(new JLabel("Pensioner discount:"));
        panel.add(pensionerDiscountField);
        panel.add(new JLabel("Company discount:"));
        panel.add(companyDiscountField);

        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            PricingFormData formData = new PricingFormData();
            formData.startDate = LocalDate.parse(startDateField.getText().trim());
            formData.endDate = LocalDate.parse(endDateField.getText().trim());
            if (formData.endDate.isBefore(formData.startDate)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
                return null;
            }

            formData.annualSubscription = parseNonNegativeDouble(annualSubscriptionField);
            formData.penaltyPerDay = parseNonNegativeDouble(penaltyPerDayField);

            formData.categoryPrices = new HashMap<>();
            formData.categoryPrices.put(VehicleCategory.ECONOMY, parseNonNegativeDouble(economyField));
            formData.categoryPrices.put(VehicleCategory.STANDARD, parseNonNegativeDouble(standardField));
            formData.categoryPrices.put(VehicleCategory.FAMILY, parseNonNegativeDouble(familyField));
            formData.categoryPrices.put(VehicleCategory.LUXURY, parseNonNegativeDouble(luxuryField));

            formData.extraServicePrices = new HashMap<>();
            formData.extraServicePrices.put(ExtraService.GPS, parseNonNegativeDouble(gpsField));
            formData.extraServicePrices.put(ExtraService.CHILD_SEAT, parseNonNegativeDouble(childSeatField));
            formData.extraServicePrices.put(ExtraService.ADDITIONAL_INSURANCE, parseNonNegativeDouble(insuranceField));
            formData.extraServicePrices.put(ExtraService.EXTENDED_USE, parseNonNegativeDouble(extendedUseField));

            formData.clientDiscounts = new HashMap<>();
            formData.clientDiscounts.put(ClientCategory.STUDENT, parseDiscount(studentDiscountField));
            formData.clientDiscounts.put(ClientCategory.PENSIONER, parseDiscount(pensionerDiscountField));
            formData.clientDiscounts.put(ClientCategory.COMPANY, parseDiscount(companyDiscountField));

            return formData;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must use yyyy-mm-dd format.");
            return null;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "All prices and discounts must be valid numbers.");
            return null;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    private ClientFormData showClientFormDialog(String title, Client initialClient) {
        JTextField nameField = createTextField(initialClient == null ? "" : initialClient.getName());
        JTextField lastNameField = createTextField(initialClient == null ? "" : initialClient.getLastName());
        JTextField genderField = createTextField(initialClient == null ? "" : initialClient.getGender());
        JTextField birthdayField = createTextField(initialClient == null ? "1998-01-01" : initialClient.getBirthday().toString());
        JTextField phoneField = createTextField(initialClient == null ? "" : initialClient.getPhoneNumber());
        JTextField addressField = createTextField(initialClient == null ? "" : initialClient.getAddress());
        JTextField usernameField = createTextField(initialClient == null ? "" : initialClient.getUserName());
        JPasswordField passwordField = createPasswordField();
        if (initialClient != null) {
            passwordField.setText(initialClient.getPassword());
        }
        JTextField licenseIssueDateField = createTextField(initialClient == null ? LocalDate.now().minusYears(3).toString() : initialClient.getDriversLicenseIssueDate().toString());
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("NONE");
        for (ClientCategory clientCategory : ClientCategory.values()) {
            categoryComboBox.addItem(clientCategory.name());
        }
        categoryComboBox.setSelectedItem(initialClient == null || initialClient.getClientCategory() == null ? "NONE" : initialClient.getClientCategory().name());

        JTextField lateReturnsField = createTextField(initialClient == null ? "0" : String.valueOf(initialClient.getLateReturnsCount()));
        JComboBox<SubscriptionStatus> subscriptionStatusComboBox = new JComboBox<>(SubscriptionStatus.values());
        subscriptionStatusComboBox.setSelectedItem(initialClient == null || initialClient.getSubscription() == null ? SubscriptionStatus.ACTIVE : initialClient.getSubscription().getSubscriptionStatus());
        JTextField subscriptionExpirationField = createTextField(initialClient == null || initialClient.getSubscription() == null ? LocalDate.now().plusYears(1).toString() : initialClient.getSubscription().getExpirationDate().toString());

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
        panel.add(new JLabel("Late returns:"));
        panel.add(lateReturnsField);
        panel.add(new JLabel("Subscription status:"));
        panel.add(subscriptionStatusComboBox);
        panel.add(new JLabel("Subscription expiration:"));
        panel.add(subscriptionExpirationField);

        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            ClientFormData formData = new ClientFormData();
            formData.name = nameField.getText().trim();
            formData.lastName = lastNameField.getText().trim();
            formData.gender = genderField.getText().trim();
            formData.birthday = LocalDate.parse(birthdayField.getText().trim());
            formData.phoneNumber = phoneField.getText().trim();
            formData.address = addressField.getText().trim();
            formData.userName = usernameField.getText().trim();
            formData.password = new String(passwordField.getPassword());
            formData.driversLicenseIssueDate = LocalDate.parse(licenseIssueDateField.getText().trim());
            formData.clientCategory = parseClientCategory((String) categoryComboBox.getSelectedItem());
            formData.lateReturnsCount = Integer.parseInt(lateReturnsField.getText().trim());
            formData.subscriptionStatus = (SubscriptionStatus) subscriptionStatusComboBox.getSelectedItem();
            formData.subscriptionExpirationDate = LocalDate.parse(subscriptionExpirationField.getText().trim());

            if (formData.name.isEmpty() || formData.lastName.isEmpty() || formData.userName.isEmpty() || formData.password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, last name, username and password are required.");
                return null;
            }

            if (!formData.userName.contains("@")) {
                JOptionPane.showMessageDialog(this, "Client username must be an email address.");
                return null;
            }

            if (formData.driversLicenseIssueDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Driver license issue date cannot be in the future.");
                return null;
            }

            if (formData.driversLicenseIssueDate.isAfter(LocalDate.now().minusYears(2))) {
                JOptionPane.showMessageDialog(this, "Client must have a driver license for at least 2 years.");
                return null;
            }

            if (formData.lateReturnsCount < 0) {
                JOptionPane.showMessageDialog(this, "Late returns cannot be negative.");
                return null;
            }

            if (formData.subscriptionStatus == null) {
                JOptionPane.showMessageDialog(this, "Subscription status is required.");
                return null;
            }

            return formData;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must use yyyy-mm-dd format.");
            return null;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Late returns must be a number.");
            return null;
        }
    }

    private JTextField createTextField() {
        return createTextField("");
    }

    private JTextField createTextField(String value) {
        JTextField textField = new JTextField(value);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return passwordField;
    }

    private double parseNonNegativeDouble(JTextField textField) {
        double value = Double.parseDouble(textField.getText().trim());
        if (value < 0) {
            throw new IllegalArgumentException("Values cannot be negative.");
        }
        return value;
    }

    private double parseDiscount(JTextField textField) {
        double value = parseNonNegativeDouble(textField);
        if (value > 1) {
            throw new IllegalArgumentException("Discount must be between 0 and 1.");
        }
        return value;
    }

    private JPanel wrapWithTopMargin(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(GuiTheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel createVehicleModelComboLabel(VehicleModel vehicleModel, boolean isSelected, JList<? extends VehicleModel> list) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        if (vehicleModel != null) {
            label.setText(vehicleModel.getManufacturer() + " " + vehicleModel.getModel() + " - " + vehicleModel.getVehicleCategory());
        }
        if (isSelected) {
            label.setOpaque(true);
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        }
        return label;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);
        return actionPanel;
    }

    private int getSelectedId(JTable table) {
        if (table == null || table.getSelectedRow() < 0) {
            return -1;
        }
        int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
        Object idValue = table.getModel().getValueAt(modelRow, 0);
        if (idValue instanceof Integer) {
            return (Integer) idValue;
        }
        return Integer.parseInt(String.valueOf(idValue));
    }

    private boolean confirmDelete(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private Agent findAgentById(int id) {
        for (Agent agent : app.getAgents()) {
            if (agent.getId() == id) {
                return agent;
            }
        }
        return null;
    }

    private Administrator findAdministratorById(int id) {
        for (Administrator item : app.getAdministrators()) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    private Pricing findPricingById(int id) {
        for (Pricing pricing : app.getPricingService().getPricings()) {
            if (pricing.getId() == id) {
                return pricing;
            }
        }
        return null;
    }

    private VehicleModel findVehicleModelById(int id) {
        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            if (vehicleModel.getId() == id) {
                return vehicleModel;
            }
        }
        return null;
    }

    private Vehicle findVehicleById(int id) {
        for (Vehicle vehicle : app.getVehicleService().getVehicles()) {
            if (vehicle.getId() == id) {
                return vehicle;
            }
        }
        return null;
    }

    private Reservation findReservationById(int id) {
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getId() == id) {
                return reservation;
            }
        }
        return null;
    }

    private Client findClientById(int id) {
        for (Client client : app.getClientService().getClients()) {
            if (client.getId() == id) {
                return client;
            }
        }
        return null;
    }

    private ClientCategory parseClientCategory(String value) {
        if (value == null || value.equals("NONE")) {
            return null;
        }
        return ClientCategory.valueOf(value);
    }

    private boolean isUsernameTaken(String username, int ignoredUserId) {
        for (Agent agent : app.getAgents()) {
            if (agent.getId() != ignoredUserId && agent.getUserName().equalsIgnoreCase(username)) {
                return true;
            }
        }

        for (Administrator item : app.getAdministrators()) {
            if (item.getId() != ignoredUserId && item.getUserName().equalsIgnoreCase(username)) {
                return true;
            }
        }

        for (model.Client client : app.getClientService().getClients()) {
            if (client.getId() != ignoredUserId && client.getUserName().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    private boolean isRegistrationTaken(String registration, int ignoredVehicleId) {
        for (Vehicle vehicle : app.getVehicleService().getVehicles()) {
            if (vehicle.getId() != ignoredVehicleId && vehicle.getRegistration().equalsIgnoreCase(registration)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasOverlappingPricing(LocalDate startDate, LocalDate endDate, int ignoredPricingId) {
        for (Pricing pricing : app.getPricingService().getPricings()) {
            if (pricing.getId() == ignoredPricingId) {
                continue;
            }

            if (!endDate.isBefore(pricing.getStartDate()) && !startDate.isAfter(pricing.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAgentUsedByRental(Agent agent) {
        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            if (rental.getAgent() != null && rental.getAgent().getId() == agent.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isClientUsed(Client client) {
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getClient() != null && reservation.getClient().getId() == client.getId()) {
                return true;
            }
        }

        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            if (rental.getReservation() != null &&
                    rental.getReservation().getClient() != null &&
                    rental.getReservation().getClient().getId() == client.getId()) {
                return true;
            }
        }

        return false;
    }

    private boolean isVehicleModelUsed(VehicleModel vehicleModel) {
        for (Vehicle vehicle : app.getVehicleService().getVehicles()) {
            if (vehicle.getVehicleModel() != null && vehicle.getVehicleModel().getId() == vehicleModel.getId()) {
                return true;
            }
        }

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getVehicleModel() != null && reservation.getVehicleModel().getId() == vehicleModel.getId()) {
                return true;
            }
        }

        return false;
    }

    private boolean isVehicleUsed(Vehicle vehicle) {
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getAssignedVehicle() != null && reservation.getAssignedVehicle().getId() == vehicle.getId()) {
                return true;
            }
        }

        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            if (rental.getVehicle() != null && rental.getVehicle().getId() == vehicle.getId()) {
                return true;
            }
        }

        return false;
    }

    private boolean hasActiveRental(Vehicle vehicle) {
        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            if (rental.getVehicle() != null &&
                    rental.getVehicle().getId() == vehicle.getId() &&
                    rental.getActualEndRentDate() == null) {
                return true;
            }
        }
        return false;
    }

    private EmployeeFormData createEmployeeFormData(Employee employee) {
        EmployeeFormData formData = new EmployeeFormData();
        formData.name = employee.getName();
        formData.lastName = employee.getLastName();
        formData.gender = employee.getGender();
        formData.birthday = employee.getBirthday();
        formData.phoneNumber = employee.getPhoneNumber();
        formData.address = employee.getAddress();
        formData.userName = employee.getUserName();
        formData.password = employee.getPassword();
        formData.levelOfEducation = employee.getLevelOfEducation();
        formData.yearsOfExperience = employee.getYearsOfExperience();
        return formData;
    }

    private void updateEmployee(Employee employee, EmployeeFormData formData) {
        employee.setName(formData.name);
        employee.setLastName(formData.lastName);
        employee.setGender(formData.gender);
        employee.setBirthday(formData.birthday);
        employee.setPhoneNumber(formData.phoneNumber);
        employee.setAddress(formData.address);
        employee.setUserName(formData.userName);
        employee.setPassword(formData.password);
        employee.setLevelOfEducation(formData.levelOfEducation);
        employee.setYearsOfExperience(formData.yearsOfExperience);
    }

    private void updateClient(Client client, ClientFormData formData) {
        client.setName(formData.name);
        client.setLastName(formData.lastName);
        client.setGender(formData.gender);
        client.setBirthday(formData.birthday);
        client.setPhoneNumber(formData.phoneNumber);
        client.setAddress(formData.address);
        client.setUserName(formData.userName);
        client.setPassword(formData.password);
        client.setDriversLicenseIssueDate(formData.driversLicenseIssueDate);
        client.setClientCategory(formData.clientCategory);
        client.setLateReturnsCount(formData.lateReturnsCount);

        if (client.getSubscription() == null) {
            Subscription subscription = new Subscription(app.getNextSubscriptionId(), formData.subscriptionStatus, formData.subscriptionExpirationDate);
            client.setSubscription(subscription);
            app.getSubscriptionService().addSubscription(subscription);
        } else {
            client.getSubscription().setSubscriptionStatus(formData.subscriptionStatus);
            client.getSubscription().setExpirationDate(formData.subscriptionExpirationDate);
        }
    }

    private double getPricingMapValue(Pricing pricing, VehicleCategory category, double defaultValue) {
        if (pricing == null || pricing.getCategoryPrices().get(category) == null) {
            return defaultValue;
        }
        return pricing.getCategoryPrices().get(category);
    }

    private double getExtraServiceMapValue(Pricing pricing, ExtraService extraService, double defaultValue) {
        if (pricing == null || pricing.getExtraServicesPrices().get(extraService) == null) {
            return defaultValue;
        }
        return pricing.getExtraServicesPrices().get(extraService);
    }

    private double getClientDiscountMapValue(Pricing pricing, ClientCategory clientCategory, double defaultValue) {
        if (pricing == null || pricing.getClientCategoryDiscounts().get(clientCategory) == null) {
            return defaultValue;
        }
        return pricing.getClientCategoryDiscounts().get(clientCategory);
    }

    private void logout() {
        app.saveAll();
        new LoginFrame(app).setVisible(true);
        dispose();
    }

    private static class EmployeeFormData {
        private String name;
        private String lastName;
        private String gender;
        private LocalDate birthday;
        private String phoneNumber;
        private String address;
        private String userName;
        private String password;
        private LevelOfEducation levelOfEducation;
        private int yearsOfExperience;
    }

    private static class ClientFormData {
        private String name;
        private String lastName;
        private String gender;
        private LocalDate birthday;
        private String phoneNumber;
        private String address;
        private String userName;
        private String password;
        private LocalDate driversLicenseIssueDate;
        private ClientCategory clientCategory;
        private int lateReturnsCount;
        private SubscriptionStatus subscriptionStatus;
        private LocalDate subscriptionExpirationDate;
    }

    private static class PricingFormData {
        private LocalDate startDate;
        private LocalDate endDate;
        private double annualSubscription;
        private double penaltyPerDay;
        private Map<VehicleCategory, Double> categoryPrices;
        private Map<ExtraService, Double> extraServicePrices;
        private Map<ClientCategory, Double> clientDiscounts;
    }
}
