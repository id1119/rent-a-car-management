package gui;

import app.App;
import model.Administrator;
import model.Agent;
import model.Client;
import model.LoginResult;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private App app;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(App app) {
        this.app = app;
        setTitle("Rent-a-Car Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = GuiTheme.createPagePanel();
        mainPanel.setPreferredSize(new Dimension(420, 300));

        JPanel contentPanel = GuiTheme.createCardPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = GuiTheme.createTitleLabel("Rent-a-Car Agency");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(6));

        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setForeground(GuiTheme.MUTED_TEXT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);

        contentPanel.add(Box.createVerticalStrut(18));

        contentPanel.add(createFieldPanel("Username", usernameField = createTextField()));
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(createFieldPanel("Password", passwordField = createPasswordField()));

        contentPanel.add(Box.createVerticalStrut(16));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(120, 28));
        loginButton.setMaximumSize(new Dimension(120, 28));
        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton);
        contentPanel.add(loginButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(labelText);
        label.setForeground(GuiTheme.TEXT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        styleInputField(textField);
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        styleInputField(passwordField);
        return passwordField;
    }

    private void styleInputField(JTextField textField) {
        textField.setPreferredSize(new Dimension(260, 24));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        LoginResult loginResult = app.getAuthService().login(username, password);
        if (loginResult == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            return;
        }

        User user = loginResult.getUser();

        switch (loginResult.getRole()) {
            case CLIENT:
                new ClientMainFrame(app, (Client) user).setVisible(true);
                break;
            case AGENT:
                new AgentMainFrame(app, (Agent) user).setVisible(true);
                break;
            case ADMINISTRATOR:
                new AdminMainFrame(app, (Administrator) user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown user role.");
                return;
        }

        dispose();
    }
}
