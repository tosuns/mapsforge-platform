/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.mapsforge.plugins.tasks.eval.evaluator.EvalutationItem;
import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import geofiletypeapi.GeoUtil;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public final class RoadNetworkStatisticComparationPanel extends javax.swing.JPanel implements ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private Aggregator aggregator;

    /**
     * Creates new form RoadNetworkStatisticComparationPanel
     */
    public RoadNetworkStatisticComparationPanel() {
        initComponents();
        if (aggTopComponent1.isStatusBarVisible()) {
            aggTopComponent1.setStatusBarVisible(false);
        }
        aggTopComponent1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateMap();
                aggTopComponent1.setDisplayToFitMapMarkers();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                updateMap();
                aggTopComponent1.setDisplayToFitMapMarkers();
            }
        });
        getPropertySheetView().setDescriptionAreaVisible(false);
    }

    private void updateMap() {
        if (aggregator != null) {
            List<Source> sourceList = aggregator.getSourceList();
            if (sourceList != null) {
                Area totalBoundingBox = new Area();
                for (Source source : sourceList) {
                    String url = source.getUrl();
                    if (url != null) {
                        File file = new File(url);
                        Rectangle2D boundingBox = GeoUtil.getBoundingBox(file);
                        if (boundingBox != null) {
                            totalBoundingBox.add(new Area(boundingBox));
                        }
                    }
                }
                Rectangle2D bounds = totalBoundingBox.getBounds2D();
                aggTopComponent1.showArea(
                        new DoubleRect(
                        bounds.getX(),
                        bounds.getY(),
                        bounds.getWidth(),
                        bounds.getHeight()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public RoadNetworkStatisticComparationPanel(EvalutationItem evaluationItem) {
        this();
        aggregator = evaluationItem.getAggregator();
        if (aggregator != null) {
            getTitleLabel().setText(aggregator.getDataObject().getNodeDelegate().getDisplayName());
            RoadNetworkProcess roadNetworkProcess = evaluationItem.getRoadNetworkProcess();
            if (roadNetworkProcess != null) {
                for (AbstractLayer<?> layer : roadNetworkProcess.getLayers()) {
                    try {
                        AbstractLayer cloneLayer = layer.getClass().newInstance();
                        for (Object item : layer.getItemList()) {
                            cloneLayer.add(item);
                        }
                        aggTopComponent1.addLayer(cloneLayer);
                    } catch (InstantiationException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            updateMap();
        }
        StatisticNode statisticNode = new StatisticNode(evaluationItem);
        explorerManager.setRootContext(statisticNode);
        try {
            explorerManager.setSelectedNodes(new Node[]{statisticNode});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public JPanel getMapView() {
        return aggTopComponent1;
    }

    public PropertySheetView getPropertySheetView() {
        return propertySheetView1;
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

        jPanel1 = new javax.swing.JPanel();
        mapContainer = new javax.swing.JPanel();
        aggTopComponent1 = new de.fub.agg2graphui.AggTopComponent();
        statisticContainer = new javax.swing.JPanel();
        propertySheetView1 = new org.openide.explorer.propertysheet.PropertySheetView();
        jPanel2 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 216, 178));
        setMaximumSize(new java.awt.Dimension(2147483647, 330));
        setMinimumSize(new java.awt.Dimension(416, 300));
        setPreferredSize(new java.awt.Dimension(0, 330));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel1.setLayout(new java.awt.BorderLayout(8, 0));

        mapContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        mapContainer.setMinimumSize(new java.awt.Dimension(300, 300));
        mapContainer.setOpaque(false);
        mapContainer.setPreferredSize(new java.awt.Dimension(300, 300));
        mapContainer.setLayout(new java.awt.BorderLayout());
        mapContainer.add(aggTopComponent1, java.awt.BorderLayout.CENTER);

        jPanel1.add(mapContainer, java.awt.BorderLayout.WEST);

        statisticContainer.setOpaque(false);
        statisticContainer.setLayout(new java.awt.BorderLayout());

        propertySheetView1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        propertySheetView1.setOpaque(false);
        statisticContainer.add(propertySheetView1, java.awt.BorderLayout.CENTER);

        jPanel1.add(statisticContainer, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 177, 101)));
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 24));
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(960, 24));
        jPanel2.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(RoadNetworkStatisticComparationPanel.class, "RoadNetworkStatisticComparationPanel.titleLabel.text")); // NOI18N
        titleLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(246, 197, 151)));
        jPanel2.add(titleLabel, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.agg2graphui.AggTopComponent aggTopComponent1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel mapContainer;
    private org.openide.explorer.propertysheet.PropertySheetView propertySheetView1;
    private javax.swing.JPanel statisticContainer;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private static class StatisticNode extends AbstractNode {

        private final EvalutationItem evaluationItem;
        private static final String TOTAL_ROAD_LENGTH_ITEM = "total road length";
        private static final String AVG_ROAD_LENGTH_ITEM = "average road length";
        private static final String ROAD_COUNT_ITEM = "total number of roads";
        private static final String INTERSECTION_COUNT_ITEM = "number of real intersections";
        private static final String PSEUDO_INTERSECTION_COUNT_ITEM = "number of pseudo intersections";

        public StatisticNode(EvalutationItem evaluationItem) {
            super(Children.LEAF);
            this.evaluationItem = evaluationItem;
        }

        @NbBundle.Messages({
            "CLT_StatisticNode_Set_Name=Road network metrics"
        })
        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            set.setDisplayName(Bundle.CLT_StatisticNode_Set_Name());
            sheet.put(set);
            if (evaluationItem != null) {
                List<String> itemList = Arrays.asList(TOTAL_ROAD_LENGTH_ITEM, AVG_ROAD_LENGTH_ITEM,
                        ROAD_COUNT_ITEM, ROAD_COUNT_ITEM,
                        INTERSECTION_COUNT_ITEM, PSEUDO_INTERSECTION_COUNT_ITEM);

                for (String itemName : itemList) {
                    Property<?> property = createProperty(itemName);
                    if (property != null) {
                        set.put(property);
                    }
                }
            }
            return sheet;
        }

        private Property<?> createProperty(String statisticItemName) {
            Property<?> property = null;
            if (statisticItemName != null) {
                try {
                    RoadNetworkProcess roadNetwork = evaluationItem.getRoadNetworkProcess();
                    for (StatisticProvider.StatisticSection section : roadNetwork.getStatisticData()) {
                        if ("Road Network Statistics".equals(section.getName())) {
                            List<StatisticProvider.StatisticItem> statisticsItemList = section.getStatisticsItemList();
                            for (StatisticProvider.StatisticItem item : statisticsItemList) {
                                if (statisticItemName.equals(item.getName())) {
                                    property = new StatisticProperty(item);
                                }
                            }
                            break;
                        }
                    }
                } catch (StatisticProvider.StatisticNotAvailableException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return property;
        }
    }

    private static class StatisticProperty extends ReadOnly<String> {

        private final StatisticProvider.StatisticItem item;
        private static final String PROP_NAME_SUPPRESS_CUSTOM_EDITOR = "suppressCustomEditor";

        public StatisticProperty(StatisticProvider.StatisticItem item) {
            super(item.getName(), String.class, item.getName(), item.getDescription());
            this.item = item;
            setValue(PROP_NAME_SUPPRESS_CUSTOM_EDITOR, Boolean.TRUE);
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return item.getValue();
        }
    }
}
