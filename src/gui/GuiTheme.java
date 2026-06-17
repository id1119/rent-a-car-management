package gui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class GuiTheme {
    public static final Color BACKGROUND = UIManager.getColor("Panel.background");
    public static final Color SURFACE = UIManager.getColor("Panel.background");
    public static final Color BORDER = new Color(170, 170, 170);
    public static final Color TEXT = Color.BLACK;
    public static final Color MUTED_TEXT = new Color(80, 80, 80);
    public static final Color PRIMARY = UIManager.getColor("Button.background");
    public static final Color PRIMARY_DARK = Color.BLACK;
    public static final Color ACCENT = new Color(90, 90, 90);

    private static final Font BASE_FONT = new Font("Dialog", Font.PLAIN, 12);
    private static final Font TITLE_FONT = new Font("Dialog", Font.BOLD, 16);
    private static final Font SECTION_FONT = new Font("Dialog", Font.BOLD, 13);

    private GuiTheme() {
    }

    public static void applyDefaults() {
        UIManager.put("defaultFont", BASE_FONT);
        UIManager.put("Label.font", BASE_FONT);
        UIManager.put("Button.font", BASE_FONT);
        UIManager.put("TextField.font", BASE_FONT);
        UIManager.put("PasswordField.font", BASE_FONT);
        UIManager.put("Table.font", BASE_FONT);
        UIManager.put("TableHeader.font", BASE_FONT.deriveFont(Font.BOLD));
        UIManager.put("TabbedPane.font", BASE_FONT);
    }

    public static JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
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
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        return button;
    }

    public static void styleTable(JTable table) {
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(22);
        table.setShowGrid(true);
        table.setGridColor(new Color(210, 210, 210));
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
    }
}
