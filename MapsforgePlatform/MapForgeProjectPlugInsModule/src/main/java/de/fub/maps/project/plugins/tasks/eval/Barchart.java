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
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Serdar
 */
public class Barchart extends JPanel {

    private static final long serialVersionUID = 1L;
    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private final JFreeChart chart;
    private final ChartPanel chartPanel;
    private final CategoryPlot plot;

    public Barchart() {
        setLayout(new BorderLayout());
        plot = new CategoryPlot() {
            private static final long serialVersionUID = 1L;

            @Override
            public LegendItemCollection getLegendItems() {

                CategoryItemRenderer renderer = getRenderer(0);
                return renderer.getLegendItems();
            }
        };
        plot.setRangeAxis(new NumberAxis());
        plot.setDomainAxis(new CategoryAxis());
        plot.getDomainAxis().setMaximumCategoryLabelLines(3);
        plot.getDomainAxis().setCategoryLabelPositionOffset(5);
        plot.setDataset(dataset);
        plot.setOrientation(PlotOrientation.VERTICAL);
        chart = new JFreeChart(null, null, plot, true);
        chart.setBackgroundPaint(Color.white);
        BarRenderer renderer = new BarRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setAutoPopulateSeriesFillPaint(true);
        renderer.setAutoPopulateSeriesPaint(true);
        renderer.setShadowVisible(false);
        renderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator());
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        chartPanel = new ChartPanel(chart, false);
        chartPanel.setBackground(Color.white);
        add(chartPanel, BorderLayout.CENTER);
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public CategoryPlot getPlot() {
        return plot;
    }

    public CategoryAxis getDomainAxis() {
        return plot.getDomainAxis();
    }

    public ValueAxis getRangeAxis() {
        return plot.getRangeAxis();
    }
}
