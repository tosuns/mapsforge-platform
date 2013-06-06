/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.roadgen.Road;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapsforge.plugins.mapmatcher.MapMatcher;
import de.fub.mapsforge.project.utils.LayerTableCellRender;
import de.fub.mapsforgeplatform.openstreetmap.service.MapProvider;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Show_Hide_Layers=Shows/Hides Layers",
    "CLT_Show_Hide_Layer_View=Shows/Hides Layer View",
    "CLT_Fit_Map_To_Size_Tooltip=Zoom the map to the size of the viewport.",
    "CLT_StatusBar_Button_Tooltip=Status bar enable/disable"
})
public class EvaluatorTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;
    @StaticResource
    private static final String LAYER_ICON_PATH = "de/fub/mapsforge/plugins/tasks/eval/layersIcon.png";
    @StaticResource
    private static final String LAYERVIEW_ICON_PATH = "de/fub/mapsforge/plugins/tasks/eval/layerview.png";
    @StaticResource
    private static final String FIT_MAP_TO_SIZE_BUTTON_ICON_PATH = "de/fub/mapsforge/plugins/tasks/eval/zoomToMap.png";
//    @StaticResource
//    private static final String STATUS_BAR_VISIBLE_ICON_PATH = "de/fub/mapsforge/plugins/tasks/eval/statusbarIcon.png";
    private final OSMEvaluatorProcess osmEvaluatorProcess = new OSMEvaluatorProcess();
    private final ExplorerManager explorerManager = new ExplorerManager();
    private JComboBox<TileSource> tileSourceComboBox;
    private JPopupMenu layersMenu;
    private JButton layersButton;
    private JToggleButton layerViewButton;
    private JButton fitToSizeButton;
    private transient final LayerNodeFactory nodeFactory = new LayerNodeFactory();
    private transient final Object MUTEX_UPDATE = new Object();
    private transient final RequestProcessor requestProcessor = new RequestProcessor();
    private RoadNetwork roadNetwork;

    /**
     * Creates new form EvaluatorTopComponent
     */
    public EvaluatorTopComponent() {
        initComponents();
        initGui();
    }

    public EvaluatorTopComponent(MapMatcher mapMatcher, MapProvider mapProvider, RoadNetwork roadNetwork) {
        this();
        assert mapMatcher != null && mapProvider != null && roadNetwork != null;
        setDisplayName(MessageFormat.format("Map Evaluation: [{0}/{1}]", mapMatcher.getClass().getSimpleName(), mapProvider.getClass().getSimpleName()));
        this.roadNetwork = roadNetwork;
        osmEvaluatorProcess.setInput(roadNetwork);
        osmEvaluatorProcess.setMapMatcher(mapMatcher);
        osmEvaluatorProcess.setMapProvider(mapProvider);
    }

    private void initGui() {
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        mappingCost.setText(String.valueOf(0));
        avgMappingDistance.setText(String.valueOf(0));
        explorerManager.setRootContext(new AbstractNode(Children.create(nodeFactory, true)));
        outlineView1.getOutline().setRowHeight(300);
        outlineView1.getOutline().setRootVisible(false);
        outlineView1.getOutline().setDefaultRenderer(Object.class, new LayerTableCellRender());
        jSplitPane1.setDividerLocation(Integer.MAX_VALUE);

        for (AbstractLayer<?> layer : osmEvaluatorProcess.getLayers()) {
            aggTopComponent1.addLayer(layer);
        }

        // set up fit map to size button
        if (fitToSizeButton == null) {
            fitToSizeButton = new JButton(ImageUtilities.loadImageIcon(FIT_MAP_TO_SIZE_BUTTON_ICON_PATH, true));
            fitToSizeButton.setToolTipText(Bundle.CLT_Fit_Map_To_Size_Tooltip());
            fitToSizeButton.addActionListener(new FitToSizeButtonActionListenerImpl());
            toolbar.add(fitToSizeButton, 0);
            toolbar.add(new JToolBar.Separator(), 0);
        }

        // set up layer view button
        if (layerViewButton == null) {
            layerViewButton = new JToggleButton(ImageUtilities.loadImageIcon(LAYERVIEW_ICON_PATH, true));
            layerViewButton.setSelected(false);
            layerViewButton.setToolTipText(Bundle.CLT_Show_Hide_Layer_View());
            layerViewButton.addActionListener(new LayerViewButtonActionListenerImpl());
            toolbar.add(layerViewButton, 0);
        }

        // set up layer show/hide drop down button
        if (layersMenu == null) {
            layersMenu = new JPopupMenu();
            initPopupMenu();
            layersButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon(LAYER_ICON_PATH, true), layersMenu);
            layersButton.setToolTipText(Bundle.CLT_Show_Hide_Layers());
            layersButton.addActionListener(new LayerButtonActionListenerImpl());
            toolbar.add(layersButton, 0);
        } else {
            layersMenu.removeAll();
            initPopupMenu();
        }

        // set up tilesource combobox
        if (tileSourceComboBox == null) {
            ArrayList<? extends TileSource> tileSources = new ArrayList<TileSource>(Lookup.getDefault().lookupResult(TileSource.class).allInstances());
            Collections.sort(tileSources, new Comparator<TileSource>() {
                @Override
                public int compare(TileSource o1, TileSource o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            tileSourceComboBox = new JComboBox<TileSource>(tileSources.toArray(new TileSource[tileSources.size()]));
            tileSourceComboBox.setMaximumSize(new Dimension(100, 16));
            if (!tileSources.isEmpty()) {
                tileSourceComboBox.setSelectedItem(tileSources.iterator().next());
            }
            tileSourceComboBox.addItemListener(new TileSourceComboBoxItemListenerImpl());

            toolbar.add(new JToolBar.Separator(), 0);
            toolbar.add(tileSourceComboBox, 0);
        }

    }

    /**
     * initializes and sets up the drop down menu of the Layer visiblity menu.
     */
    private void initPopupMenu() {
        nodeFactory.clear();
        aggTopComponent1.clear();

        for (final AbstractLayer<?> layer : osmEvaluatorProcess.getLayers()) {
            aggTopComponent1.addLayer(layer);
            nodeFactory.add(layer);
            final JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(layer.getName());
            jCheckBoxMenuItem.setState(layer.isVisible());
            jCheckBoxMenuItem.addActionListener(new LayerActionListener(layer, jCheckBoxMenuItem));
            layersMenu.add(jCheckBoxMenuItem);
        }

    }

    @Override
    protected void componentOpened() {
        super.componentOpened();
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                osmEvaluatorProcess.start();
                mappingCost.setText(String.format(Locale.ENGLISH, "%f", osmEvaluatorProcess.getMappingCost()));
                avgMappingDistance.setText(String.format(Locale.ENGLISH, "%f", osmEvaluatorProcess.getAvgMappingDistance()));
            }
        });
    }

    private void updateMap() {
        if (fitToSizeButton.isEnabled()) {
            synchronized (MUTEX_UPDATE) {
                fitToSizeButton.setEnabled(false);
                try {
                    Area totalBoundingBox = new Area();

                    if (roadNetwork != null) {
                        Set<Road> roads = roadNetwork.getRoads();
                        for (Road road : roads) {
                            for (ILocation location : road.getNodes()) {
                                totalBoundingBox.add(new Area(new Rectangle2D.Double(location.getLat(), location.getLon(), 0.00000025, 0.00000025)));
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
                } finally {
                    fitToSizeButton.setEnabled(true);
                }
            }
        }
    }

    private void updateLayerView() {
        if (layerViewButton.isSelected()) {
            jSplitPane1.setEnabled(true);
            jSplitPane1.setDividerLocation(.8);
        } else {
            jSplitPane1.setEnabled(false);
            jSplitPane1.setDividerLocation(1d);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    private static class LayerActionListener implements ActionListener {

        private final AbstractLayer<?> layer;
        private final JCheckBoxMenuItem jCheckBoxMenuItem;

        public LayerActionListener(AbstractLayer<?> layer, JCheckBoxMenuItem jCheckBoxMenuItem) {
            this.layer = layer;
            this.jCheckBoxMenuItem = jCheckBoxMenuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            layer.setVisible(jCheckBoxMenuItem.getState());
        }
    }

    private class LayerViewButtonActionListenerImpl implements ActionListener {

        public LayerViewButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateLayerView();
        }
    }

    private class TileSourceComboBoxItemListenerImpl implements ItemListener {

        public TileSourceComboBoxItemListenerImpl() {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (tileSourceComboBox.getSelectedItem() instanceof TileSource) {
                aggTopComponent1.setTileSource((TileSource) tileSourceComboBox.getSelectedItem());
            }
        }
    }

    private class LayerButtonActionListenerImpl implements ActionListener {

        public LayerButtonActionListenerImpl() {
        }
        private boolean allVisible = true;

        @Override
        public void actionPerformed(ActionEvent e) {
            allVisible = !allVisible;
            MenuElement[] subElements = layersMenu.getSubElements();
            for (MenuElement element : subElements) {
                if (element instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem) element;
                    item.doClick();
                }
            }
        }
    }

    private class FitToSizeButtonActionListenerImpl implements ActionListener {

        public FitToSizeButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            requestProcessor.post(new Runnable() {
                @Override
                public void run() {
                    updateMap();
                }
            });
        }
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
        toolbar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        avgMappingDistance = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(16, 0), new java.awt.Dimension(16, 0), new java.awt.Dimension(16, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        mappingCost = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        aggTopComponent1 = new de.fub.agg2graphui.AggTopComponent();
        outlineView1 = new org.openide.explorer.view.OutlineView("Layers");

        setLayout(new java.awt.BorderLayout());

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel1.setMinimumSize(new java.awt.Dimension(10, 24));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 24));
        jPanel1.setLayout(new java.awt.BorderLayout());

        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EvaluatorTopComponent.class, "EvaluatorTopComponent.jLabel1.text")); // NOI18N
        toolbar.add(jLabel1);
        toolbar.add(filler1);

        avgMappingDistance.setBackground(new java.awt.Color(204, 204, 204));
        avgMappingDistance.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        avgMappingDistance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(avgMappingDistance, org.openide.util.NbBundle.getMessage(EvaluatorTopComponent.class, "EvaluatorTopComponent.avgMappingDistance.text")); // NOI18N
        avgMappingDistance.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        avgMappingDistance.setMaximumSize(new java.awt.Dimension(120, 14));
        avgMappingDistance.setMinimumSize(new java.awt.Dimension(100, 14));
        toolbar.add(avgMappingDistance);
        toolbar.add(filler2);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EvaluatorTopComponent.class, "EvaluatorTopComponent.jLabel3.text")); // NOI18N
        toolbar.add(jLabel3);
        toolbar.add(filler3);

        mappingCost.setBackground(new java.awt.Color(204, 204, 204));
        mappingCost.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        mappingCost.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(mappingCost, org.openide.util.NbBundle.getMessage(EvaluatorTopComponent.class, "EvaluatorTopComponent.mappingCost.text")); // NOI18N
        mappingCost.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        mappingCost.setMaximumSize(new java.awt.Dimension(100, 14));
        toolbar.add(mappingCost);

        jPanel1.add(toolbar, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setLeftComponent(aggTopComponent1);
        jSplitPane1.setRightComponent(outlineView1);

        jPanel2.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.agg2graphui.AggTopComponent aggTopComponent1;
    private javax.swing.JLabel avgMappingDistance;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel mappingCost;
    private org.openide.explorer.view.OutlineView outlineView1;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
