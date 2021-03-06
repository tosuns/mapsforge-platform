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

import de.fub.agg2graph.management.Statistics;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.plugins.tasks.eval.evaluator.EvalutationItem;
import de.fub.utilsmodule.text.CustomNumberFormat;
import java.awt.Color;
import java.util.List;
import javax.swing.JLabel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class OverviewChart extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private List<EvalutationItem> roadNetworkStatisticsList;

    /**
     * Creates new form OverviewChart
     */
    public OverviewChart() {
        initComponents();
        barchart1.getRangeAxis().setLabel(NbBundle.getMessage(OverviewChart.class, "overview.chart1.rangeaxis1.name"));
        barchart1.getPlot().setRangeAxis(1, new NumberAxis(NbBundle.getMessage(OverviewChart.class, "overview.chart1.rangeaxis2.name")));
        barchart1.getPlot().getRangeAxis(1).setUpperMargin(.1);
        barchart1.getPlot().getRangeAxis(0).setUpperMargin(.1);
        barchart1.getPlot().getRenderer(0).setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                        StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                        new CustomNumberFormat()));
        barchart1.getPlot().getRenderer(0).setBaseItemLabelsVisible(true);
        BarRenderer barRenderer = new BarRenderer();
        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        barRenderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                        StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                        new CustomNumberFormat()));
        barRenderer.setBaseItemLabelsVisible(true);
        barRenderer.setAutoPopulateSeriesFillPaint(true);
        barRenderer.setAutoPopulateSeriesPaint(true);
        barRenderer.setShadowVisible(false);
        barchart1.getPlot().setRenderer(1, barRenderer);
        CategoryPlot plot = barchart1.getPlot();
        plot.setDataset(1, new DefaultCategoryDataset());
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
    }

    public OverviewChart(List<EvalutationItem> roadNetworkStatisticList) {
        this();
        this.roadNetworkStatisticsList = roadNetworkStatisticList;
        init();
    }

    public void setEvaluationItems(List<EvalutationItem> roadNetworkStatisticList) {
        this.roadNetworkStatisticsList = roadNetworkStatisticList;
        init();
    }

    private void init() {
        for (int i = 0; i < barchart1.getPlot().getDatasetCount(); i++) {
            CategoryDataset dataset = barchart1.getPlot().getDataset(i);
            if (dataset instanceof DefaultCategoryDataset) {
                ((DefaultCategoryDataset) dataset).clear();
            }
        }
        if (roadNetworkStatisticsList != null) {
            for (EvalutationItem item : roadNetworkStatisticsList) {
                RoadNetworkProcess roadNetworkProcess = item.getRoadNetworkProcess();
                if (roadNetworkProcess != null && roadNetworkProcess.getResult() != null) {
                    handle(item.getAggregator().getAggregatorDescriptor().getName(), roadNetworkProcess);
                }
            }
        }
        for (int i = 0; i < barchart1.getPlot().getRendererCount(); i++) {
            initRenderer(barchart1.getPlot().getRenderer(i));
        }
    }

    private void initRenderer(CategoryItemRenderer renderer) {
        Color[] colors = new Color[]{Color.red, Color.blue, Color.orange, Color.cyan, Color.yellow, Color.magenta, Color.green, Color.pink, Color.black, Color.gray};

        if (!this.roadNetworkStatisticsList.isEmpty()) {
            for (int i = 0; i < this.roadNetworkStatisticsList.size(); i++) {
                renderer.setSeriesPaint(i, colors[i % colors.length]);
            }
        }
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public DefaultCategoryDataset getDataset() {
        return barchart1.getDataset();
    }

    public JFreeChart getChart() {
        return barchart1.getChart();
    }

    public CategoryPlot getPlot() {
        return barchart1.getPlot();
    }

    public CategoryAxis getDomainAxis() {
        return barchart1.getDomainAxis();
    }

    public ValueAxis getRangeAxis() {
        return barchart1.getRangeAxis();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        barchart1 = new de.fub.maps.project.plugins.tasks.eval.Barchart();

        setBackground(new java.awt.Color(255, 216, 178));
        setPreferredSize(new java.awt.Dimension(0, 470));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 24));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(932, 24));
        jPanel1.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(OverviewChart.class, "OverviewChart.titleLabel.text")); // NOI18N
        jPanel1.add(titleLabel, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.GridLayout(1, 3));

        barchart1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 0, new java.awt.Color(204, 204, 204)));
        jPanel3.add(barchart1);

        add(jPanel3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.maps.project.plugins.tasks.eval.Barchart barchart1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    private void handle(String name, RoadNetworkProcess roadNetworkProcess) {
        if (roadNetworkProcess != null && roadNetworkProcess.getResult() != null) {
            RoadNetwork roadNetwork = roadNetworkProcess.getResult();

            CategoryDataset dataSet = barchart1.getPlot().getDataset(0);
            if (dataSet instanceof DefaultCategoryDataset) {
                DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset) dataSet;
                categoryDataset.addValue(roadNetwork.getTotalRoadLength(), name, Statistics.PROP_NAME_TOTAL_ROAD_LENGTH);
                categoryDataset.addValue(roadNetwork.getAverageRoadLength(), name, Statistics.PROP_NAME_AVERAGE_ROAD_LENGTH);
                categoryDataset.addValue(null, name, Statistics.PROP_NAME_TOTAL_NUMBER_OF_ROADS);

                categoryDataset.addValue(null, name, Statistics.PROP_NAME_TOTAL_NUMBER_OF_INTERSECTIONS);
            }

            dataSet = barchart1.getPlot().getDataset(1);
            if (dataSet instanceof DefaultCategoryDataset) {
                DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset) dataSet;
                categoryDataset.addValue(null, name, Statistics.PROP_NAME_TOTAL_ROAD_LENGTH);
                categoryDataset.addValue(null, name, Statistics.PROP_NAME_AVERAGE_ROAD_LENGTH);
                categoryDataset.addValue(roadNetwork.getRoadCount(), name, Statistics.PROP_NAME_TOTAL_NUMBER_OF_ROADS);

                categoryDataset.addValue(roadNetwork.getIntersectionCount(), name, Statistics.PROP_NAME_TOTAL_NUMBER_OF_INTERSECTIONS);
            }
        }
    }
}
