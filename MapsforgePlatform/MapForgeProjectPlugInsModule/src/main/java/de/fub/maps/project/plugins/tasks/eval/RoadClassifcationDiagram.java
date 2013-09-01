/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.plugins.tasks.eval;

import java.awt.BorderLayout;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYIntervalSeriesCollection;

/**
 *
 * @author Serdar
 */
public class RoadClassifcationDiagram extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
    private final ChartPanel chartpanel;
    private final JFreeChart barChart;

    /**
     * Creates new form RoadClassifcationDiagram
     */
    public RoadClassifcationDiagram() {
        initComponents();
        barChart = ChartFactory.createXYBarChart(null, "Degree", false, null, dataset, PlotOrientation.VERTICAL, true, true, true);
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getPlot().setBackgroundPaint(Color.WHITE);
        chartpanel = new ChartPanel(barChart, false);
        add(chartpanel, BorderLayout.CENTER);
    }

    public XYIntervalSeriesCollection getDataset() {
        return dataset;
    }

    public ChartPanel getChartpanel() {
        return chartpanel;
    }

    public JFreeChart getBarChart() {
        return barChart;
    }

    public XYPlot getXYPlot() {
        return barChart.getXYPlot();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
