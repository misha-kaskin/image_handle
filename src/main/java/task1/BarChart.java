package task1;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChart extends JPanel {
    private static final long serialVersionUID = 1L;

    static ChartPanel getBarChart(Map<Integer, Double> calculated) {
        CategoryDataset dataset = fillDataSet(calculated);
        CategoryAxis categoryAxis = new CategoryAxis("Пиксель");
        ValueAxis valueAxis = new NumberAxis("Частота");
        LayeredBarRenderer renderer = new LayeredBarRenderer();

        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis,
                valueAxis, renderer);

        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart("Гистограмма яркости",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);

        chart.setBackgroundPaint(new Color(232, 232, 232));

        renderer.setSeriesBarWidth(0, 1.0);
        renderer.setSeriesBarWidth(1, 0.4);

        renderer.setItemMargin(0.01);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.25);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setLowerMargin(0.05);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(840, 570));

        return chartPanel;
    }

    public static CategoryDataset fillDataSet(Map<Integer, Double> calculated) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Integer key : calculated.keySet()) {
            dataset.addValue(calculated.get(key), "Пиксель", key.toString());
        }

        return dataset;
    }
}
