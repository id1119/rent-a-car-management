package gui;

import app.App;
import model.Client;
import model.ExtraService;
import model.Reservation;
import model.Subscription;
import model.VehicleModel;

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

    public ClientMainFrame(App app, Client client) {
        this.app = app;
        this.client = client;

        setTitle("Client - " + client.getName() + " " + client.getLastName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(900, 620));
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
        tabbedPane.addTab("Available Models", createAvailableModelsPanel());
        tabbedPane.addTab("New Reservation", createNewReservationPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createProfilePanel() {
        JPanel outerPanel = GuiTheme.createCardPanel();
        outerPanel.setLayout(new BorderLayout(0, 14));

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

        Subscription subscription = client.getSubscription();
        if (subscription != null) {
            addRow(detailsPanel, "Subscription status", String.valueOf(subscription.getSubscriptionStatus()));
            addRow(detailsPanel, "Subscription expires", String.valueOf(subscription.getExpirationDate()));
        }

        outerPanel.add(detailsPanel, BorderLayout.CENTER);
        return wrapWithMargin(outerPanel);
    }

    private JPanel createReservationsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));
        panel.add(GuiTheme.createSectionLabel("My reservations"), BorderLayout.NORTH);

        String[] columns = {"ID", "Model", "Start", "End", "Status", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Reservation reservation : app.getReservationService().getReservations()) {
            if (reservation.getClient().getId() == client.getId()) {
                VehicleModel vehicleModel = reservation.getVehicleModel();
                tableModel.addRow(new Object[]{
                        reservation.getId(),
                        vehicleModel.getManufacturer() + " " + vehicleModel.getModel(),
                        reservation.getStartDate(),
                        reservation.getEndDate(),
                        reservation.getReservationStatus(),
                        reservation.getPrice()
                });
            }
        }

        JTable table = new JTable(tableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return wrapWithMargin(panel);
    }

    private JPanel createAvailableModelsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));
        panel.add(GuiTheme.createSectionLabel("Vehicle models"), BorderLayout.NORTH);

        String[] columns = {"ID", "Manufacturer", "Model", "Category"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            tableModel.addRow(new Object[]{
                    vehicleModel.getId(),
                    vehicleModel.getManufacturer(),
                    vehicleModel.getModel(),
                    vehicleModel.getVehicleCategory()
            });
        }

        JTable table = new JTable(tableModel);
        GuiTheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return wrapWithMargin(panel);
    }

    private JPanel createNewReservationPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(GuiTheme.createSectionLabel("New reservation request"));

        JLabel hintLabel = new JLabel("Choose a model and date range. Creation logic will be connected through ReservationService.");
        hintLabel.setForeground(GuiTheme.MUTED_TEXT);
        titlePanel.add(hintLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<VehicleModel> vehicleModelComboBox = new JComboBox<>();
        for (VehicleModel vehicleModel : app.getVehicleModels()) {
            vehicleModelComboBox.addItem(vehicleModel);
        }
        vehicleModelComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
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

        JTextField startDateField = createTextField("2026-07-01");
        JTextField endDateField = createTextField("2026-07-05");

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

        addFormRow(formPanel, gbc, 0, "Vehicle model", vehicleModelComboBox);
        addFormRow(formPanel, gbc, 1, "Start date", startDateField);
        addFormRow(formPanel, gbc, 2, "End date", endDateField);
        addFormRow(formPanel, gbc, 3, "Extra services", extraServicesPanel);

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

        return wrapWithMargin(panel);
    }

    private JTextField createTextField(String value) {
        JTextField textField = new JTextField(value);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return textField;
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

        if (!app.getReservationService().canCreateReservation(client, selectedModel, startDate, endDate)) {
            JOptionPane.showMessageDialog(this, "Reservation cannot be created. Check subscription, driver license, dates or vehicle availability.");
            return;
        }

        double price = app.getPricingService().calculateReservationPrice(selectedModel, client.getClientCategory(), startDate, endDate, extraServices);
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
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private void logout() {
        app.saveAll();
        new LoginFrame(app).setVisible(true);
        dispose();
    }
}
