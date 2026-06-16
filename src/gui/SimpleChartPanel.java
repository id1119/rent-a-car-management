package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleChartPanel extends JPanel {
    public enum ChartType {
        BAR,
        PIE
    }

    private String title;
    private Map<String, Double> data = new LinkedHashMap<>();
    private ChartType chartType;

    public SimpleChartPanel(String title) {
        this(title, ChartType.BAR);
    }

    public SimpleChartPanel(String title, ChartType chartType) {
        this.title = title;
        this.chartType = chartType == null ? ChartType.BAR : chartType;
        setBackground(GuiTheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GuiTheme.BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        setPreferredSize(new Dimension(900, 260));
    }

    public void setData(Map<String, Double> data) {
        this.data = new LinkedHashMap<>();
        if (data != null) {
            this.data.putAll(data);
        }
        repaint();
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int left = 54;
        int right = 24;
        int top = 54;
        int bottom = 52;
        int chartWidth = width - left - right;
        int chartHeight = height - top - bottom;

        drawTitle(g2);

        if (data.isEmpty() || getMaxValue() <= 0) {
            drawEmptyState(g2, width, height);
            g2.dispose();
            return;
        }

        if (chartType == ChartType.PIE) {
            drawPie(g2, width, height);
        } else {
            drawAxis(g2, left, top, chartWidth, chartHeight);
            drawBars(g2, left, top, chartWidth, chartHeight);
        }

        g2.dispose();
    }

    private void drawTitle(Graphics2D g2) {
        g2.setColor(GuiTheme.PRIMARY_DARK);
        g2.setFont(new Font("Avenir Next", Font.BOLD, 16));
        g2.drawString(title, 18, 28);
    }

    private void drawEmptyState(Graphics2D g2, int width, int height) {
        g2.setColor(GuiTheme.MUTED_TEXT);
        g2.setFont(new Font("Avenir Next", Font.PLAIN, 14));
        String message = "No data for this period";
        FontMetrics metrics = g2.getFontMetrics();
        int x = (width - metrics.stringWidth(message)) / 2;
        int y = height / 2;
        g2.drawString(message, x, y);
    }

    private void drawAxis(Graphics2D g2, int left, int top, int chartWidth, int chartHeight) {
        g2.setColor(GuiTheme.BORDER);
        g2.drawLine(left, top, left, top + chartHeight);
        g2.drawLine(left, top + chartHeight, left + chartWidth, top + chartHeight);

        double maxValue = getMaxValue();
        g2.setFont(new Font("Avenir Next", Font.PLAIN, 11));
        g2.setColor(GuiTheme.MUTED_TEXT);
        g2.drawString(formatValue(maxValue), 8, top + 5);
        g2.drawString("0", 34, top + chartHeight + 4);
    }

    private void drawBars(Graphics2D g2, int left, int top, int chartWidth, int chartHeight) {
        int count = data.size();
        int gap = 14;
        int availableWidth = chartWidth - gap * (count + 1);
        int barWidth = Math.max(20, availableWidth / count);
        double maxValue = getMaxValue();
        int index = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue() == null ? 0 : entry.getValue();
            int barHeight = (int) Math.round((value / maxValue) * (chartHeight - 10));
            int x = left + gap + index * (barWidth + gap);
            int y = top + chartHeight - barHeight;

            Color barColor = index % 2 == 0 ? GuiTheme.PRIMARY : GuiTheme.ACCENT;
            g2.setPaint(new GradientPaint(x, y, barColor.brighter(), x, top + chartHeight, barColor));
            g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 10, 10));

            g2.setColor(GuiTheme.TEXT);
            g2.setFont(new Font("Avenir Next", Font.BOLD, 11));
            String valueLabel = formatValue(value);
            FontMetrics valueMetrics = g2.getFontMetrics();
            g2.drawString(valueLabel, x + (barWidth - valueMetrics.stringWidth(valueLabel)) / 2, Math.max(top + 14, y - 6));

            g2.setColor(GuiTheme.MUTED_TEXT);
            g2.setFont(new Font("Avenir Next", Font.PLAIN, 11));
            String label = shortenLabel(entry.getKey(), Math.max(5, barWidth / 7));
            FontMetrics labelMetrics = g2.getFontMetrics();
            g2.drawString(label, x + (barWidth - labelMetrics.stringWidth(label)) / 2, top + chartHeight + 22);

            index++;
        }
    }

    private void drawPie(Graphics2D g2, int width, int height) {
        double total = getTotalValue();
        int diameter = Math.min(height - 86, width / 3);
        int x = 46;
        int y = 58;
        double currentAngle = 0;
        int index = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue() == null ? 0 : entry.getValue();
            if (value <= 0) {
                continue;
            }

            double angle = value / total * 360;
            g2.setColor(getPaletteColor(index));
            g2.fillArc(x, y, diameter, diameter, (int) Math.round(currentAngle), (int) Math.round(angle));
            currentAngle += angle;
            index++;
        }

        g2.setColor(GuiTheme.SURFACE);
        int innerDiameter = Math.max(28, diameter / 2);
        int innerOffset = (diameter - innerDiameter) / 2;
        g2.fillOval(x + innerOffset, y + innerOffset, innerDiameter, innerDiameter);

        drawLegend(g2, x + diameter + 42, y + 8, total);
    }

    private void drawLegend(Graphics2D g2, int x, int y, double total) {
        int index = 0;
        int rowHeight = 24;

        g2.setFont(new Font("Avenir Next", Font.PLAIN, 12));
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue() == null ? 0 : entry.getValue();
            if (value <= 0) {
                continue;
            }

            int rowY = y + index * rowHeight;
            g2.setColor(getPaletteColor(index));
            g2.fillRoundRect(x, rowY - 11, 14, 14, 5, 5);

            double percent = total <= 0 ? 0 : value / total * 100;
            g2.setColor(GuiTheme.TEXT);
            g2.drawString(entry.getKey() + " - " + formatValue(value) + " (" + String.format("%.1f", percent) + "%)", x + 22, rowY);
            index++;
        }
    }

    private double getMaxValue() {
        double maxValue = 0;
        for (Double value : data.values()) {
            if (value != null && value > maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    private double getTotalValue() {
        double total = 0;
        for (Double value : data.values()) {
            if (value != null && value > 0) {
                total += value;
            }
        }
        return total;
    }

    private Color getPaletteColor(int index) {
        Color[] colors = {
                GuiTheme.PRIMARY,
                GuiTheme.ACCENT,
                new Color(70, 120, 170),
                new Color(143, 92, 64),
                new Color(87, 137, 103)
        };
        return colors[index % colors.length];
    }

    private String formatValue(double value) {
        if (value >= 1000) {
            return String.format("%.1fk", value / 1000);
        }
        if (value == Math.rint(value)) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value);
    }

    private String shortenLabel(String label, int maxLength) {
        if (label == null) {
            return "";
        }
        if (label.length() <= maxLength) {
            return label;
        }
        return label.substring(0, Math.max(1, maxLength - 1)) + ".";
    }
}
