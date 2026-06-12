package gui;

import app.App;
import model.Administrator;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {
    private App app;
    private Administrator administrator;

    public AdminMainFrame(App app, Administrator administrator) {
        this.app = app;
        this.administrator = administrator;

        setTitle("Administrator - " + administrator.getName() + " " + administrator.getLastName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 680);
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

        JPanel cardPanel = GuiTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Administrator functions will be added here.", SwingConstants.CENTER);
        label.setForeground(GuiTheme.MUTED_TEXT);
        cardPanel.add(label, BorderLayout.CENTER);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void logout() {
        app.saveAll();
        new LoginFrame(app).setVisible(true);
        dispose();
    }
}
