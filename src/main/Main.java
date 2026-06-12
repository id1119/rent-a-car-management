package main;

import app.App;
import gui.GuiTheme;
import gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "Rent-a-Car");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                System.out.println("Could not set system look and feel: " + e.getMessage());
            }
            GuiTheme.applyDefaults();

            App app = new App();
            app.loadAll();

            LoginFrame loginFrame = new LoginFrame(app);
            loginFrame.setVisible(true);
        });
    }
}
