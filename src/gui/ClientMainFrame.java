package gui;

import app.App;
import model.Client;
import model.ExtraService;
import model.Pricing;
import model.Reservation;
import model.ReservationStatus;
import model.Subscription;
import model.SubscriptionStatus;
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

public class ClientMainFrame extends JFrame {
    private App app;
    private Client client;
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    private DefaultTableModel spendingSummaryTableModel;
    private DefaultTableModel spendingDetailsTableModel;

    public ClientMainFrame(App app, Client client) {
        this.app = app;
        this.client = client;

        setTitle("Client - " + client.getName() + " " + client.getLastName());
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

        JLabel headerLabel = GuiTheme.createTitleLabel("Welcome, " + client.getName());
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JLabel usernameLabel = new JLabel(client.getUserName());
        usernameLabel.setForeground(GuiTheme.MUTED_TEXT);
        headerPanel.add(usernameLabel, BorderLayout.SOUTH);

        JButton logoutButton = GuiTheme.createSecondaryButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(GuiTheme.BACKGROUND);
        tabbedPane.addTab("Profile", createProfilePanel());
        tabbedPane.addTab("My Reservations", createReservationsPanel());
        tabbedPane.addTab("My Spending", createSpendingPanel());
        tabbedPane.addTab("Available Models", createAvailableModelsPanel());
        tabbedPane.addTab("New Reservation", createNewReservationPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createProfilePanel() {
        JPanel outerPanel = GuiTheme.createCardPanel();
        outerPanel.setLayout(new BorderLayout(0, 6));

        outerPanel.add(GuiTheme.createSectionLabel("Profile details"), BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 14, 12));
        detailsPanel.setOpaque(false);

        addRow(detailsPanel, "ID", String.valueOf(client.getId()));
        addRow(detailsPanel, "Name", client.getName());
        addRow(detailsPanel, "Last name", client.getLastName());
        addRow(detailsPanel, "Gender", client.getGender());
        addRow(detailsPanel, "Birthday", String.valueOf(client.getBirthday()));
        addRow(detailsPanel, "Phone", client.getPhoneNumber());
        addRow(detailsPanel, "Address", client.getAddress());
        addRow(detailsPanel, "Username", client.getUserName());
        addRow(detailsPanel, "Driver license issued", String.valueOf(client.getDriversLicenseIssueDate()));
        addRow(detailsPanel, "Client category", String.valueOf(client.getClientCategory()));
        addRow(detailsPanel, "Late returns", String.valueOf(client.getLateReturnsCount()));
        addRow(detailsPanel, "Reservation blocked until", String.valueOf(client.getReservationBlockedUntil()));

        Subscription subscription = client.getSubscription();
        if (subscription != null) {
            addRow(detailsPanel, "Subscription status", String.valueOf(subscription.getSubscriptionStatus()));
            addRow(detailsPanel, "Subscription expires", String.valueOf(subscription.getExpirationDate()));
        }

        outerPanel.add(detailsPanel, BorderLayout.CENTER);
        JButton renewalButton = GuiTheme.createPrimaryButton("Request subscription renewal");
        renewalButton.addActionListener(e -> requestSubscriptionRenewal());
        outerPanel.add(renewalButton, BorderLayout.SOUTH);
        return wrapWithMargin(outerPanel);
    }

    private JPanel createReservationsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("My reservations"), BorderLayout.WEST);

        JButton cancelButton = GuiTheme.createSecondaryButton("Cancel selected");
        cancelButton.addActionListener(e -> cancelSelectedReservation());
        titlePanel.add(cancelButton, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Model", "Start", "End", "Status", "Base price", "Extra services", "Total price"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        refreshReservationsTable();

        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GuiTheme.styleTable(reservationsTable);
        panel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        return wrapWithMargin(panel);
    }

    private void refreshReservationsTable() {
        reservationsTableModel.setRowCount(0);
        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getClient().getId() == client.getId()) {
                VehicleModel vehicleModel = reservation.getVehicleModel();
                reservationsTableModel.addRow(new Object[]{
                        reservation.getId(),
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
    }

    private JPanel createSpendingPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("My spending"));

        JLabel hintLabel = new JLabel("Approved reservations, late-return penalties and current subscription cost.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createSpendingSummaryPanel(), createSpendingDetailsPanel());
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(splitPane, BorderLayout.CENTER);

        refreshSpendingTables();
        return wrapWithMargin(panel);
    }

    private JPanel createSpendingSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(GuiTheme.createSectionLabel("Summary"), BorderLayout.NORTH);

        spendingSummaryTableModel = new DefaultTableModel(new String[]{"Metric", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(spendingSummaryTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSpendingDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(GuiTheme.createSectionLabel("Details"), BorderLayout.NORTH);

        spendingDetailsTableModel = new DefaultTableModel(new String[]{"Type", "Reference", "Date", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(spendingDetailsTableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshSpendingTables() {
        if (spendingSummaryTableModel == null || spendingDetailsTableModel == null) {
            return;
        }

        spendingSummaryTableModel.setRowCount(0);
        spendingDetailsTableModel.setRowCount(0);

        double reservationTotal = 0;
        double penaltyTotal = 0;
        double subscriptionTotal = calculateSubscriptionSpending();

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getClient().getId() != client.getId() || reservation.getReservationStatus() != ReservationStatus.APPROVED) {
                continue;
            }

            reservationTotal += reservation.getPrice();
            VehicleModel vehicleModel = reservation.getVehicleModel();
            spendingDetailsTableModel.addRow(new Object[]{
                    "Reservation",
                    "#" + reservation.getId() + " - " + vehicleModel.getManufacturer() + " " + vehicleModel.getModel(),
                    reservation.getStartDate(),
                    reservation.getPrice()
            });
        }

        for (VehicleRental rental : app.getVehicleRentalService().getVehicleRentals()) {
            if (rental.getReservation() == null ||
                    rental.getReservation().getClient().getId() != client.getId() ||
                    rental.getPenaltyForBeingLate() <= 0) {
                continue;
            }

            penaltyTotal += rental.getPenaltyForBeingLate();
            spendingDetailsTableModel.addRow(new Object[]{
                    "Late penalty",
                    "Rental #" + rental.getId(),
                    rental.getActualEndRentDate() == null ? rental.getEndRentDate() : rental.getActualEndRentDate(),
                    rental.getPenaltyForBeingLate()
            });
        }

        if (subscriptionTotal > 0) {
            spendingDetailsTableModel.addRow(new Object[]{
                    "Subscription",
                    "Annual membership",
                    client.getSubscription().getExpirationDate().minusYears(1),
                    subscriptionTotal
            });
        }

        spendingSummaryTableModel.addRow(new Object[]{"Approved reservation total", reservationTotal});
        spendingSummaryTableModel.addRow(new Object[]{"Late penalty total", penaltyTotal});
        spendingSummaryTableModel.addRow(new Object[]{"Subscription total", subscriptionTotal});
        spendingSummaryTableModel.addRow(new Object[]{"Grand total", reservationTotal + penaltyTotal + subscriptionTotal});
    }

    private double calculateSubscriptionSpending() {
        if (client.getSubscription() == null || client.getSubscription().getExpirationDate() == null) {
            return 0;
        }

        LocalDate subscriptionStartDate = client.getSubscription().getExpirationDate().minusYears(1);
        Pricing pricing = app.getPricingService().findValidPricing(subscriptionStartDate);
        if (pricing == null) {
            return 0;
        }
        return pricing.getAnnualSubscription();
    }

    private JPanel createAvailableModelsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("Available vehicle models"));

        JLabel hintLabel = new JLabel("Filter by model, manufacturer, category and availability date range.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField manufacturerField = createTextField("");
        JTextField modelField = createTextField("");
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("ANY");
        for (VehicleCategory category : VehicleCategory.values()) {
            categoryComboBox.addItem(category.name());
        }
        JTextField startDateField = createTextField("2026-07-01");
        JTextField endDateField = createTextField("2026-07-05");

        String[] columns = {"ID", "Manufacturer", "Model", "Category"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        GuiTheme.styleTable(table);

        JButton searchButton = GuiTheme.createPrimaryButton("Search available models");
        searchButton.addActionListener(e -> refreshAvailableModelsTable(
                tableModel,
                manufacturerField,
                modelField,
                categoryComboBox,
                startDateField,
                endDateField
        ));

        addFormRow(filterPanel, gbc, 0, "Manufacturer", manufacturerField);
        addFormRow(filterPanel, gbc, 1, "Model", modelField);
        addFormRow(filterPanel, gbc, 2, "Category", categoryComboBox);
        addFormRow(filterPanel, gbc, 3, "Start date", startDateField);
        addFormRow(filterPanel, gbc, 4, "End date", endDateField);

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(searchButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshAvailableModelsTable(tableModel, manufacturerField, modelField, categoryComboBox, startDateField, endDateField);
        return wrapWithMargin(panel);
    }

    private JPanel createNewReservationPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("New reservation request"));

        JLabel hintLabel = new JLabel("Filter available models, choose date range and optional extra services.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField manufacturerFilterField = createTextField("");
        JTextField modelFilterField = createTextField("");
        JComboBox<String> categoryFilterComboBox = new JComboBox<>();
        categoryFilterComboBox.addItem("ANY");
        for (VehicleCategory category : VehicleCategory.values()) {
            categoryFilterComboBox.addItem(category.name());
        }

        JComboBox<VehicleModel> vehicleModelComboBox = new JComboBox<>();
        vehicleModelComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
            if (value != null) {
                label.setText(value.getManufacturer() + " " + value.getModel() + " - " + value.getVehicleCategory());
            }
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });

        JTextField startDateField = createTextField(LocalDate.now().plusDays(1).toString());
        JTextField endDateField = createTextField(LocalDate.now().plusDays(3).toString());
        JButton searchModelsButton = GuiTheme.createSecondaryButton("Search models");
        searchModelsButton.addActionListener(e -> refreshReservationModelComboBox(
                vehicleModelComboBox,
                manufacturerFilterField,
                modelFilterField,
                categoryFilterComboBox,
                startDateField,
                endDateField
        ));

        JCheckBox gpsCheckBox = new JCheckBox("GPS navigation");
        JCheckBox childSeatCheckBox = new JCheckBox("Child seat");
        JCheckBox insuranceCheckBox = new JCheckBox("Additional insurance");
        JCheckBox extendedUseCheckBox = new JCheckBox("Extended use");
        styleCheckBox(gpsCheckBox);
        styleCheckBox(childSeatCheckBox);
        styleCheckBox(insuranceCheckBox);
        styleCheckBox(extendedUseCheckBox);

        JPanel extraServicesPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        extraServicesPanel.setOpaque(false);
        extraServicesPanel.add(gpsCheckBox);
        extraServicesPanel.add(childSeatCheckBox);
        extraServicesPanel.add(insuranceCheckBox);
        extraServicesPanel.add(extendedUseCheckBox);

        addFormRow(formPanel, gbc, 0, "Manufacturer", manufacturerFilterField);
        addFormRow(formPanel, gbc, 1, "Model", modelFilterField);
        addFormRow(formPanel, gbc, 2, "Category", categoryFilterComboBox);
        addFormRow(formPanel, gbc, 3, "Start date", startDateField);
        addFormRow(formPanel, gbc, 4, "End date", endDateField);
        addFormRow(formPanel, gbc, 5, "Available model", vehicleModelComboBox);
        addFormRow(formPanel, gbc, 6, "", searchModelsButton);
        addFormRow(formPanel, gbc, 7, "Extra services", extraServicesPanel);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton createButton = GuiTheme.createPrimaryButton("Create reservation request");
        createButton.addActionListener(e -> createReservationRequest(
                vehicleModelComboBox,
                startDateField,
                endDateField,
                gpsCheckBox,
                childSeatCheckBox,
                insuranceCheckBox,
                extendedUseCheckBox
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(createButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshReservationModelComboBox(
                vehicleModelComboBox,
                manufacturerFilterField,
                modelFilterField,
                categoryFilterComboBox,
                startDateField,
                endDateField
        );
        return wrapWithMargin(panel);
    }

    private JTextField createTextField(String value) {
        JTextField textField = new JTextField(value);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return textField;
    }

    private void refreshAvailableModelsTable(DefaultTableModel tableModel, JTextField manufacturerField, JTextField modelField, JComboBox<String> categoryComboBox, JTextField startDateField, JTextField endDateField) {
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

        String manufacturerFilter = manufacturerField.getText().trim().toLowerCase();
        String modelFilter = modelField.getText().trim().toLowerCase();
        String categoryFilter = (String) categoryComboBox.getSelectedItem();

        tableModel.setRowCount(0);
        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            if (!manufacturerFilter.isEmpty() && !vehicleModel.getManufacturer().toLowerCase().contains(manufacturerFilter)) {
                continue;
            }

            if (!modelFilter.isEmpty() && !vehicleModel.getModel().toLowerCase().contains(modelFilter)) {
                continue;
            }

            if (categoryFilter != null && !categoryFilter.equals("ANY") && !vehicleModel.getVehicleCategory().name().equals(categoryFilter)) {
                continue;
            }

            if (!app.getReservationService().hasAvailableVehicle(vehicleModel, startDate, endDate)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    vehicleModel.getId(),
                    vehicleModel.getManufacturer(),
                    vehicleModel.getModel(),
                    vehicleModel.getVehicleCategory()
            });
        }
    }

    private void refreshReservationModelComboBox(JComboBox<VehicleModel> vehicleModelComboBox,
                                                 JTextField manufacturerField,
                                                 JTextField modelField,
                                                 JComboBox<String> categoryComboBox,
                                                 JTextField startDateField,
                                                 JTextField endDateField) {
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

        String manufacturerFilter = manufacturerField.getText().trim().toLowerCase();
        String modelFilter = modelField.getText().trim().toLowerCase();
        String categoryFilter = (String) categoryComboBox.getSelectedItem();

        vehicleModelComboBox.removeAllItems();
        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            if (!matchesReservationModelFilter(vehicleModel, manufacturerFilter, modelFilter, categoryFilter)) {
                continue;
            }

            if (!app.getReservationService().hasAvailableVehicle(vehicleModel, startDate, endDate)) {
                continue;
            }

            vehicleModelComboBox.addItem(vehicleModel);
        }

        if (vehicleModelComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No available models match selected filters.");
        }
    }

    private boolean matchesReservationModelFilter(VehicleModel vehicleModel,
                                                  String manufacturerFilter,
                                                  String modelFilter,
                                                  String categoryFilter) {
        if (vehicleModel == null) {
            return false;
        }

        if (!manufacturerFilter.isEmpty() && !vehicleModel.getManufacturer().toLowerCase().contains(manufacturerFilter)) {
            return false;
        }

        if (!modelFilter.isEmpty() && !vehicleModel.getModel().toLowerCase().contains(modelFilter)) {
            return false;
        }

        return categoryFilter == null ||
                categoryFilter.equals("ANY") ||
                vehicleModel.getVehicleCategory().name().equals(categoryFilter);
    }

    private void requestSubscriptionRenewal() {
        if (client.getSubscription() == null) {
            JOptionPane.showMessageDialog(this, "Client has no subscription.");
            return;
        }

        if (client.getSubscription().getSubscriptionStatus() == SubscriptionStatus.PENDING_APPROVAL) {
            JOptionPane.showMessageDialog(this, "Renewal request is already pending.");
            return;
        }

        boolean requested = app.getSubscriptionService().requestRenewal(client);
        if (!requested) {
            JOptionPane.showMessageDialog(this, app.getSubscriptionService().getRenewalRequestError(client));
            app.saveAll();
            new ClientMainFrame(app, client).setVisible(true);
            dispose();
            return;
        }

        app.saveAll();
        JOptionPane.showMessageDialog(this, "Renewal request submitted.");
        new ClientMainFrame(app, client).setVisible(true);
        dispose();
    }

    private void cancelSelectedReservation() {
        Reservation reservation = getSelectedReservation();
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        boolean canceled = app.getReservationService().cancelReservationByClient(reservation);
        if (!canceled) {
            JOptionPane.showMessageDialog(this, app.getReservationService().getReservationCancellationError(reservation));
            return;
        }

        app.saveAll();
        JOptionPane.showMessageDialog(this, "Reservation canceled. You cannot create a new reservation for the next 24 hours.");
        new ClientMainFrame(app, client).setVisible(true);
        dispose();
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

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setOpaque(false);
        checkBox.setForeground(GuiTheme.TEXT);
        checkBox.setFocusPainted(false);
    }

    private void createReservationRequest(JComboBox<VehicleModel> vehicleModelComboBox, JTextField startDateField, JTextField endDateField, JCheckBox gpsCheckBox, JCheckBox childSeatCheckBox, JCheckBox insuranceCheckBox, JCheckBox extendedUseCheckBox) {
        VehicleModel selectedModel = (VehicleModel) vehicleModelComboBox.getSelectedItem();
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateField.getText().trim());
            endDate = LocalDate.parse(endDateField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must use yyyy-mm-dd format.");
            return;
        }

        List<ExtraService> extraServices = new ArrayList<>();
        if (gpsCheckBox.isSelected()) {
            extraServices.add(ExtraService.GPS);
        }
        if (childSeatCheckBox.isSelected()) {
            extraServices.add(ExtraService.CHILD_SEAT);
        }
        if (insuranceCheckBox.isSelected()) {
            extraServices.add(ExtraService.ADDITIONAL_INSURANCE);
        }
        if (extendedUseCheckBox.isSelected()) {
            extraServices.add(ExtraService.EXTENDED_USE);
        }

        String reservationError = app.getReservationService().getReservationCreationError(client, selectedModel, startDate, endDate);
        if (reservationError != null) {
            JOptionPane.showMessageDialog(this, reservationError);
            return;
        }

        double rentalBasePrice = app.getPricingService().calculateRentalBasePrice(selectedModel, client.getClientCategory(), startDate, endDate);
        double reservationExtraServicesPrice = app.getPricingService().calculateExtraServicesPrice(extraServices, startDate, endDate, startDate);
        double price = rentalBasePrice + reservationExtraServicesPrice;
        if (price <= 0) {
            JOptionPane.showMessageDialog(this, "No valid pricing found for selected date.");
            return;
        }

        Reservation reservation = app.getReservationService().createReservationRequest(
                app.getNextReservationId(),
                client,
                selectedModel,
                startDate,
                endDate,
                price,
                rentalBasePrice,
                reservationExtraServicesPrice,
                extraServices
        );

        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Reservation request was not created.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Reservation request created. Price: " + price);
        app.saveAll();
        new ClientMainFrame(app, client).setVisible(true);
        dispose();
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

    private void addRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setForeground(GuiTheme.MUTED_TEXT);
        panel.add(labelComponent);

        JLabel valueComponent = new JLabel(value == null ? "" : value);
        valueComponent.setForeground(GuiTheme.TEXT);
        panel.add(valueComponent);
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
