package gui;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XChartDashboardPanel extends JPanel {
    private JPanel revenueChartContainer;
    private JPanel statusChartContainer;
    private JPanel agentWorkloadChartContainer;

    public XChartDashboardPanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(GuiTheme.BACKGROUND);

        revenueChartContainer = createContainer();
        statusChartContainer = createContainer();
        agentWorkloadChartContainer = createContainer();

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(statusChartContainer);
        bottomPanel.add(agentWorkloadChartContainer);

        add(revenueChartContainer, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }

    public void updateCharts(Map<String, LinkedHashMap<String, Double>> monthlyRevenue,
                             Map<String, Integer> processedReservationsByAgent,
                             Map<String, Integer> reservationStatuses) {
        replaceChart(revenueChartContainer, createRevenueChart(monthlyRevenue));
        replaceChart(statusChartContainer, new XChartPanel<>(createPieChart("Status rezervacija u prethodnih 30 dana", reservationStatuses)));
        replaceChart(agentWorkloadChartContainer, new XChartPanel<>(createPieChart("Opterecenje agenata u prethodnih 30 dana", processedReservationsByAgent)));
        revalidate();
        repaint();
    }

    private JPanel createContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GuiTheme.SURFACE);
        panel.setBorder(BorderFactory.createEtchedBorder());
        return panel;
    }

    private void replaceChart(JPanel container, JComponent chartPanel) {
        container.removeAll();
        container.add(chartPanel, BorderLayout.CENTER);
    }

    private XChartPanel<CategoryChart> createRevenueChart(Map<String, LinkedHashMap<String, Double>> monthlyRevenue) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(980)
                .height(300)
                .title("Prihodi po kategoriji klijenta")
                .xAxisTitle("Mesec")
                .yAxisTitle("Prihod")
                .build();

        styleXYChart(chart);

        List<String> months = extractMonths(monthlyRevenue);
        for (Map.Entry<String, LinkedHashMap<String, Double>> entry : monthlyRevenue.entrySet()) {
            List<Double> yData = new ArrayList<>();
            for (String month : months) {
                yData.add(entry.getValue().getOrDefault(month, 0.0));
            }
            CategorySeries series = chart.addSeries(entry.getKey(), months, yData);
            series.setChartCategorySeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
        }

        return new XChartPanel<>(chart);
    }

    private PieChart createPieChart(String title, Map<String, Integer> source) {
        PieChart chart = new PieChartBuilder()
                .width(470)
                .height(300)
                .title(title)
                .build();

        stylePieChart(chart);

        boolean hasData = false;
        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            int value = entry.getValue() == null ? 0 : entry.getValue();
            if (value <= 0) {
                continue;
            }
            chart.addSeries(entry.getKey(), value);
            hasData = true;
        }

        if (!hasData) {
            chart.addSeries("Nema podataka", 1);
        }

        return chart;
    }

    private void styleXYChart(CategoryChart chart) {
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setChartBackgroundColor(GuiTheme.SURFACE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setChartFontColor(GuiTheme.TEXT);
        chart.getStyler().setAxisTickLabelsColor(GuiTheme.TEXT);
        chart.getStyler().setXAxisLabelRotation(45);
        chart.getStyler().setToolTipsEnabled(false);
        chart.getStyler().setChartTitleFont(new Font("Dialog", Font.BOLD, 13));
        chart.getStyler().setAxisTitleFont(new Font("Dialog", Font.PLAIN, 12));
        chart.getStyler().setAxisTickLabelsFont(new Font("Dialog", Font.PLAIN, 11));
        chart.getStyler().setLegendFont(new Font("Dialog", Font.PLAIN, 11));
    }

    private void stylePieChart(PieChart chart) {
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setChartBackgroundColor(GuiTheme.SURFACE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setChartFontColor(GuiTheme.TEXT);
        chart.getStyler().setLabelsVisible(false);
        chart.getStyler().setToolTipsEnabled(false);
        chart.getStyler().setChartTitleFont(new Font("Dialog", Font.BOLD, 13));
        chart.getStyler().setLegendFont(new Font("Dialog", Font.PLAIN, 11));
    }

    private List<String> extractMonths(Map<String, LinkedHashMap<String, Double>> monthlyRevenue) {
        for (LinkedHashMap<String, Double> series : monthlyRevenue.values()) {
            return new ArrayList<>(series.keySet());
        }
        return new ArrayList<>();
    }

}
