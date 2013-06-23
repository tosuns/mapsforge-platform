/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui.charts;

import de.fub.utilsmodule.text.CustomNumberFormat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class ClassificationBarChart extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final ChartPanel chartPanel;
    private final DefaultCategoryDataset relDataset = new DefaultCategoryDataset();
    private final DefaultCategoryDataset absDataset = new DefaultCategoryDataset();
    private final JFreeChart barChart;
    private final CategoryPlot plot;
    private Color relColor = new Color(0x00, 0x7a, 0xe0);
    private Color absColor = new Color(0xe0, 0x00, 0x00);

    /**
     * Creates new form ClassificationBarChart
     */
    public ClassificationBarChart() {
        super();
        initComponents();
        plot = new CustomCategoryPlot();
        barChart = new JFreeChart(NbBundle.getMessage(ClassificationBarChart.class, "CLT_Chart_Classify_Name"), null, plot, true);
        ChartFactory.getChartTheme().apply(barChart);
        plot.setDomainAxis(new CategoryAxis());
        plot.getDomainAxis().setLabel(NbBundle.getMessage(ClassificationBarChart.class, "CLT_Doman_Axis_Name"));
        plot.setOrientation(PlotOrientation.VERTICAL);

        Font font = new JLabel().getFont().deriveFont(Font.BOLD, 14);
        barChart.getTitle().setFont(font);
        barChart.getTitle().setPaint(new Color(153, 153, 153));

        plot.setDataset(0, relDataset);
        plot.setDataset(1, absDataset);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        NumberAxis relAxis = new NumberAxis(NbBundle.getMessage(ClassificationBarChart.class, "CLT_Value_Axis_Name"));
        relAxis.setAutoRange(true);
        relAxis.setUpperMargin(.20);
        NumberAxis absAxis = new NumberAxis(NbBundle.getMessage(ClassificationBarChart.class, "CLT_Value_Rel_Axis_Name"));
        absAxis.setAutoRange(true);
        absAxis.setUpperMargin(.20);

        plot.setRangeAxis(0, relAxis);
        plot.setRangeAxis(1, absAxis);
        plot.setRangeAxisLocation(0, AxisLocation.TOP_OR_LEFT);
        plot.setRangeAxisLocation(1, AxisLocation.TOP_OR_RIGHT);

        BarRenderer relRenderer = new BarRenderer();
        relRenderer.setBasePaint(relColor);
        relRenderer.setAutoPopulateSeriesPaint(false);
        relRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        relRenderer.setBarPainter(new StandardBarPainter());
        relRenderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                new CustomNumberFormat()));
        relRenderer.setBaseItemLabelsVisible(true);

        BarRenderer absRenderer = new BarRenderer();
        absRenderer.setBasePaint(absColor);
        absRenderer.setAutoPopulateSeriesPaint(false);
        absRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        absRenderer.setBarPainter(new StandardBarPainter());
        absRenderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                new CustomNumberFormat()));
        absRenderer.setBaseItemLabelsVisible(true);

        plot.setRenderer(0, relRenderer);
        plot.setRenderer(1, absRenderer);

        plot.setBackgroundPaint(Color.white);
        barChart.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        chartPanel = new ChartPanel(barChart, false);
        chartPanel.setVerticalAxisTrace(false);
        chartPanel.setDisplayToolTips(true);
        chartPanel.setBackground(Color.white);
        add(chartPanel, BorderLayout.CENTER);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public DefaultCategoryDataset getRelDataset() {
        return relDataset;
    }

    public DefaultCategoryDataset getAbsDataset() {
        return absDataset;
    }

    public JFreeChart getBarChart() {
        return barChart;
    }

    public CategoryPlot getPlot() {
        return plot;
    }

    public void setTitle(TextTitle title) {
        barChart.setTitle(title);
    }

    public CategoryAxis getDomainAxis() {
        return plot.getDomainAxis();
    }

    public ValueAxis getRangeAxis() {
        return plot.getRangeAxis();
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

    /* ===========================================================
     * JFreeChart : a free chart library for the Java(tm) platform
     * ===========================================================
     *
     * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
     *
     * Project Info:  http://www.jfree.org/jfreechart/index.html
     *
     * This library is free software; you can redistribute it and/or modify it under the terms
     * of the GNU Lesser General Public License as published by the Free Software Foundation;
     * either version 2.1 of the License, or (at your option) any later version.
     *
     * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
     * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
     * See the GNU Lesser General Public License for more details.
     *
     * You should have received a copy of the GNU Lesser General Public License along with this
     * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
     * Boston, MA 02111-1307, USA.
     *
     * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
     * in the United States and other countries.]
     *
     * ------------------
     * DualAxisDemo5.java
     * ------------------
     * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
     *
     * Original Author:  David Gilbert (for Object Refinery Limited);
     * Contributor(s):   -;
     *
     * $Id: DualAxisDemo5.java,v 1.12 2004/04/29 07:54:56 mungady Exp $
     *
     * Changes
     * -------
     * 19-Sep-2003 : Version 1 (DG);
     * 06-Feb-2004 : Modified to correct legend (DG);
     *
     */
    private static class CustomCategoryPlot extends CategoryPlot {

        private static final long serialVersionUID = 1L;

        @Override
        public LegendItemCollection getLegendItems() {
            final LegendItemCollection result = new LegendItemCollection();

            final CategoryDataset data = getDataset();
            if (data != null && data.getRowCount() > 0 && data.getColumnCount() > 0) {
                final CategoryItemRenderer r = getRenderer();
                if (r != null) {
                    final LegendItem item = r.getLegendItem(0, 0);
                    result.add(item);
                }
            }

            final CategoryDataset dset2 = getDataset(1);
            if (dset2 != null && dset2.getRowCount() > 1 && dset2.getColumnCount() > 1) {
                final CategoryItemRenderer renderer2 = getRenderer(1);
                if (renderer2 != null) {
                    final LegendItem item = renderer2.getLegendItem(1, 1);
                    result.add(item);
                }
            }

            return result;
        }
    }
}
