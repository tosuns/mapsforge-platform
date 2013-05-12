/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.ui;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.mapsforge.gpx.analysis.models.GpxTrackSegmentStatistic;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYSeries;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.fub.mapsforge.gpx.analysis.ui//GpxTrkSegAnalysizer//EN",
        autostore = false)
public final class GpxTrkSegAnalysizerTopComponent extends TopComponent {

    private static final long serialVersionUID = 1L;
    private GpxTrackSegmentStatistic statistic;

    public GpxTrkSegAnalysizerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "CTL_GpxTrkSegAnalysizerTopComponent"));
        setToolTipText(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "HINT_GpxTrkSegAnalysizerTopComponent"));
        jSplitPane1.setDividerLocation(.75);

        int height = 0;
        for (Component child : jPanel3.getComponents()) {
            height += child.getPreferredSize().height;
        }
        jScrollPane1.getViewport().getView().setPreferredSize(new Dimension(0, height));

        addComponentListener(new ComponentAdapterImpl());
        init();
    }

    public GpxTrkSegAnalysizerTopComponent(GpxTrackSegmentStatistic statistic) {
        this();
        setStatistic(statistic);
    }

    private void init() {
        initCharts();
    }

    public void setStatistic(GpxTrackSegmentStatistic statistic) {
        this.statistic = statistic;
        this.statisticForm1.setNode(statistic == null ? Node.EMPTY : statistic.getNodeDelegate());
        updateCharts();
    }

    private void updateCharts() {
        // get chat data sets
        XYSeries velocityDataset = velocityChart.getDataset();
        velocityDataset.clear();
        XYSeries accelerationDataset = accelerationChart.getDataset();
        accelerationDataset.clear();
        XYSeries headingDataset = headingChart.getDataset();
        headingDataset.clear();

        if (statistic != null) {
            // get track
            List<Waypoint> trackSegment = statistic.getTrackSegment();

            Waypoint secondLastWayPoint = null;
            Waypoint lastWaypoint = null;
            double totalDistance = 0;
            Double lastVelocity = null;
            for (Waypoint waypoint : trackSegment) {

                if (lastWaypoint != null
                        && lastWaypoint.getTimestamp() != null
                        && waypoint.getTimestamp() != null
                        && Math.abs(lastWaypoint.getTimestamp().getTime() - waypoint.getTimestamp().getTime()) > 0) {
                    double velocity = 0;
                    double acceleration = 0;
                    double distance = GPSCalc.getDistVincentyFast(
                            lastWaypoint.getLat(),
                            lastWaypoint.getLon(),
                            waypoint.getLat(),
                            waypoint.getLon());
                    double timeDiff = (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000d;

                    if (timeDiff > 0) {
                        velocity = (distance / timeDiff);

                        totalDistance += distance;

                        velocityDataset.addOrUpdate(totalDistance, velocity * 3.6);
                        if (lastVelocity != null) {
                            acceleration = (velocity - lastVelocity) / timeDiff;
                            accelerationDataset.addOrUpdate(totalDistance, acceleration);
                        } else {
                            accelerationDataset.addOrUpdate(totalDistance, 0);
                        }
                    }

                    lastVelocity = velocity;

                    if (secondLastWayPoint != null) {
                        double heading = GPSCalc.computeHeading(secondLastWayPoint, lastWaypoint, waypoint);
                        headingDataset.addOrUpdate(totalDistance, heading);
                    }

                }
                secondLastWayPoint = lastWaypoint;
                lastWaypoint = waypoint;
            }
        }
    }

    private void initCharts() {
        // set chart titles
        velocityChart.setTitle(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.velocity.chart.name"));
        accelerationChart.setTitle(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.acceleration.chart.name"));
        headingChart.setTitle(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.heading.chart.name"));

        // config domain axis
        ValueAxis domainAxis = velocityChart.getDomainAxis();
        domainAxis.setLabel(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.velocity.chart.domainaxis.name"));
        domainAxis = accelerationChart.getDomainAxis();
        domainAxis.setLabel(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.acceleration.chart.domainaxis.name"));
        domainAxis = headingChart.getDomainAxis();
        domainAxis.setLabel(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.heading.chart.domainaxis.name"));


        // confing value axis
        ValueAxis rangeAxis = velocityChart.getRangeAxis();
        rangeAxis.setLabel(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.velocity.chart.valueaxis.name"));
        rangeAxis = accelerationChart.getRangeAxis();
        rangeAxis.setLabel(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.acceleration.chart.valueaxis.name"));
        rangeAxis = headingChart.getRangeAxis();
        rangeAxis.setLabel(NbBundle.getMessage(GpxTrkSegAnalysizerTopComponent.class, "GpxTrkSegAnalysizerTopComponent.heading.chart.valueaxis.name"));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        velocityChart = new de.fub.mapsforge.gpx.analysis.ui.charts.ChartPanelComponent();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        accelerationChart = new de.fub.mapsforge.gpx.analysis.ui.charts.ChartPanelComponent();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        headingChart = new de.fub.mapsforge.gpx.analysis.ui.charts.ChartPanelComponent();
        statisticForm1 = new de.fub.mapsforge.gpx.analysis.ui.StatisticForm();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(700);
        jSplitPane1.setResizeWeight(1.0);

        jPanel2.setPreferredSize(new java.awt.Dimension(400, 479));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setAutoscrolls(true);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setAutoscrolls(true);
        jPanel3.setMinimumSize(new java.awt.Dimension(100, 300));
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        velocityChart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        velocityChart.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        velocityChart.setMinimumSize(new java.awt.Dimension(100, 300));
        jPanel3.add(velocityChart);
        jPanel3.add(filler1);

        accelerationChart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        accelerationChart.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        accelerationChart.setMinimumSize(new java.awt.Dimension(100, 300));
        accelerationChart.setPreferredSize(new java.awt.Dimension(875, 300));
        jPanel3.add(accelerationChart);
        jPanel3.add(filler4);
        jPanel3.add(headingChart);

        jScrollPane1.setViewportView(jPanel3);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel2);

        statisticForm1.setPreferredSize(new java.awt.Dimension(400, 412));
        jSplitPane1.setRightComponent(statisticForm1);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.mapsforge.gpx.analysis.ui.charts.ChartPanelComponent accelerationChart;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private de.fub.mapsforge.gpx.analysis.ui.charts.ChartPanelComponent headingChart;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private de.fub.mapsforge.gpx.analysis.ui.StatisticForm statisticForm1;
    private de.fub.mapsforge.gpx.analysis.ui.charts.ChartPanelComponent velocityChart;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private class ComponentAdapterImpl extends ComponentAdapter {

        public ComponentAdapterImpl() {
        }

        @Override
        public void componentResized(ComponentEvent e) {
            int width = (int) (getSize().getWidth() * .66);
            jSplitPane1.getLeftComponent().setMinimumSize(new Dimension(width, 0));
            int dividerLocation = jSplitPane1.getDividerLocation();
            if (dividerLocation < width) {
                jSplitPane1.setDividerLocation(.66);
            }
        }
    }
}
