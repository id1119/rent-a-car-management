package gui;

import app.App;
import model.Agent;
import model.Reservation;
import model.ReservationStatus;
import model.VehicleModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AgentMainFrame extends JFrame {
    private App app;
    private Agent agent;
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;

    public AgentMainFrame(App app, Agent agent) {
        this.app = app;
        this.agent = agent;

        setTitle("Agent - " + agent.getName() + " " + agent.getLastName());
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
        tabbedPane.addTab("Reservations", createReservationsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createReservationsPanel() {
        JPanel panel = GuiTheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 14));

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

        titlePanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Client", "Model", "Start", "End", "Status", "Price"};
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
                    reservation.getPrice()
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

        boolean approved = app.getReservationService().approveReservation(reservation);
        if (!approved) {
            JOptionPane.showMessageDialog(this, "Reservation cannot be approved because no vehicle is available in that period.");
            return;
        }

        app.saveAll();
        refreshReservationsTable();
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

        boolean rejected = app.getReservationService().rejectReservation(reservation);
        if (!rejected) {
            JOptionPane.showMessageDialog(this, "Reservation was not rejected.");
            return;
        }

        app.saveAll();
        refreshReservationsTable();
        JOptionPane.showMessageDialog(this, "Reservation rejected.");
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
