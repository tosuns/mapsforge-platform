/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval.heading;

import de.fub.agg2graph.roadgen.Road;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.ILocation;
import de.fub.mapsforge.plugins.tasks.eval.evaluator.EvalutationItem;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Serdar
 */
public class HeadingHistogrammContainer extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Creates new form HeadingHistogrammContainer
     */
    public HeadingHistogrammContainer() {
        initComponents();
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headingHistogramm1 = new de.fub.mapsforge.plugins.tasks.eval.heading.HeadingHistogramm();
        jPanel1 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 216, 178));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        setLayout(new java.awt.BorderLayout());
        add(headingHistogramm1, java.awt.BorderLayout.CENTER);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 24));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(592, 24));
        jPanel1.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(HeadingHistogrammContainer.class, "HeadingHistogrammContainer.titleLabel.text")); // NOI18N
        jPanel1.add(titleLabel, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.mapsforge.plugins.tasks.eval.heading.HeadingHistogramm headingHistogramm1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    public void setEvaluationItems(List<EvalutationItem> roadNetworkStatisticList) {
        headingHistogramm1.getDataset().removeAllSeries();
        int seriesIndex = 0;

        for (EvalutationItem item : roadNetworkStatisticList) {
            String name = item.getAggregator().getAggregatorDescriptor().getName();
            XYSeries series = new XYSeries(name);
            Set<Road> roads = item.getRoadNetworkProcess().getResult().getRoads();
            int[] historamm = new int[180];

            for (Road road : roads) {

                ILocation lastLocation = null;
                ILocation secondLastLocation = null;

                int i = 0;
                for (ILocation location : road.getNodes()) {

                    if (lastLocation != null && secondLastLocation != null && i < historamm.length) {
                        double angleBetweenEdges = GPSCalc.getAngleBetweenEdges(lastLocation, secondLastLocation, secondLastLocation, location);
                        int value = (int) angleBetweenEdges;
                        if (value < historamm.length) {
                            historamm[value]++;
                        }

                        i++;
                    }

                    lastLocation = secondLastLocation;
                    secondLastLocation = location;
                }
            }

            for (int j = 0; j < historamm.length; j++) {
                series.add(j, historamm[j] == 0 ? null : historamm[j] / (double) historamm.length);
            }

            headingHistogramm1.getDataset().addSeries(series);

            XYItemRenderer renderer = headingHistogramm1.getBarChart().getXYPlot().getRenderer();
            if (renderer instanceof LayeredBarRenderer) {
                LayeredBarRenderer layeredBarRenderer = (LayeredBarRenderer) renderer;
                layeredBarRenderer.setSeriesBarWidth(seriesIndex, 1. / roadNetworkStatisticList.size() * (seriesIndex + 1));
            }
            seriesIndex++;
        }
    }
}
