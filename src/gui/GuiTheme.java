package gui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class GuiTheme {
    public static final Color BACKGROUND = new Color(244, 241, 234);
    public static final Color SURFACE = new Color(255, 253, 247);
    public static final Color BORDER = new Color(222, 214, 201);
    public static final Color TEXT = new Color(34, 35, 33);
    public static final Color MUTED_TEXT = new Color(101, 96, 87);
    public static final Color PRIMARY = new Color(22, 87, 80);
    public static final Color PRIMARY_DARK = new Color(14, 59, 55);
    public static final Color ACCENT = new Color(191, 128, 64);

    private static final Font BASE_FONT = new Font("Avenir Next", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Avenir Next", Font.BOLD, 24);
    private static final Font SECTION_FONT = new Font("Avenir Next", Font.BOLD, 17);

    private GuiTheme() {
    }

    public static void applyDefaults() {
        UIManager.put("defaultFont", BASE_FONT);
        UIManager.put("Label.font", BASE_FONT);
        UIManager.put("Button.font", BASE_FONT.deriveFont(Font.BOLD));
        UIManager.put("TextField.font", BASE_FONT);
        UIManager.put("PasswordField.font", BASE_FONT);
        UIManager.put("Table.font", BASE_FONT);
        UIManager.put("TableHeader.font", BASE_FONT.deriveFont(Font.BOLD));
        UIManager.put("TabbedPane.font", BASE_FONT.deriveFont(Font.BOLD));
    }

    public static JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        return panel;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SECTION_FONT);
        label.setForeground(PRIMARY_DARK);
        return label;
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(SURFACE);
        button.setForeground(PRIMARY_DARK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)
        ));
        return button;
    }

    public static void styleTable(JTable table) {
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 235, 232));
        table.setSelectionForeground(TEXT);
        table.setBackground(SURFACE);
        table.setForeground(TEXT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
}
