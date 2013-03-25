/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.ui;

import de.fub.mapsforge.project.datasource.spi.factories.NodeFactory;
import de.fub.mapsforge.project.datasource.spi.TrackSegmentBehaviour;
import de.fub.mapsforge.project.datasource.spi.TrksegWrapper;
import de.fub.mapsforge.project.datasource.spi.actions.TrackSemgentExportAction;
import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.service.GPXProvider;
import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.Trk;
import de.fub.gpxmodule.xml.gpx.Trkseg;
import de.fub.gpxmodule.xml.gpx.Wpt;
import de.fub.mapsforge.project.datasource.spi.actions.SplitpanelAction;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import de.fub.utilsmodule.color.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.swing.outline.Outline;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_GPX_VISUAL",
        iconBase = "de/fub/gpxmodule/gpx.png",
        mimeType = "text/gpx+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "GPXVisual",
        position = 500)
@NbBundle.Messages("LBL_GPX_VISUAL=Visual")
public class MapViewElement extends javax.swing.JPanel implements MultiViewElement, ChangeListener, ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;
    private final JToolBar toolbar = new JToolBar();
    private final ObservableList<TrksegWrapper> trackSegmentList = new ObservableArrayList<TrksegWrapper>();
    private final HashMap<TrksegWrapper, List<CustomMapMarker>> markerMap = new HashMap<TrksegWrapper, List<CustomMapMarker>>();
    private final ExplorerManager explorerManager = new ExplorerManager();
    private GPXDataObject obj;
    private transient MultiViewElementCallback callback;
    private GPXProvider gpxProvide;
    private boolean modelChanged = true;
    // mouselistener to check and display only the treseg that are visible
    private MouseListener mouseListener = new MouseAdapterImpl();
    private boolean splitPanelVisble;

    /**
     * Creates new form MapViewElement
     */
    public MapViewElement(Lookup lkp) {
        obj = lkp.lookup(GPXDataObject.class);
        assert obj != null;
        init();
    }

    private void init() {
        initComponents();
        initToolBar();
        explorerManager.setRootContext(new AbstractNode(Children.create(new NodeFactory(trackSegmentList), true)));
        gpxProvide = obj.getLookup().lookup(GPXProvider.class);
        gpxProvide.addChangeListener(WeakListeners.change(MapViewElement.this, gpxProvide));
        update();

        jSplitPane1.setDividerLocation(Integer.MAX_VALUE);
    }

    private void initOutlineView() {
        outlineView1.setPropertyColumnDescription("visible", "Visible On/Off");
        outlineView1.setDefaultActionAllowed(false);
        Outline outline = outlineView1.getOutline();
        outline.addMouseListener(WeakListeners.create(MouseListener.class, mouseListener, outline));
        outline.setRootVisible(false);
        TableColumnModel columnModel = outline.getColumnModel();
        // replace the node column in which the segments bounding box is displayed
        // to the end of the columns
        TableColumn column = outline.getColumn("Segment");
        columnModel.removeColumn(column);
        columnModel.addColumn(column);

        // set a fix width for the visible checkbox column
        column = outline.getColumn("V");
        if (column != null) {
            column.setMaxWidth(24);
            column.setMinWidth(24);
            column.setPreferredWidth(24);
        }
        outlineView1.revalidate();
        outline.repaint();
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.add(new JToolBar.Separator());
        JButton jButton = new JButton(new TrackSemgentExportAction(getExplorerManager()));
        toolbar.add(jButton);
        toolbar.add(new JToggleButton(new SplitpanelAction(MapViewElement.this)));
    }

    private void update() {
        abstractMapViewer1.removeAllMapMarkers();
        if (gpxProvide != null) {
            Gpx gpx = gpxProvide.getGpx();
            if (gpx != null) {
                ColorUtil.ColorProvider colorProvider = ColorUtil.createColorProvider();
                for (Trk trk : gpx.getTrk()) {
                    for (Trkseg trkseg : trk.getTrkseg()) {
                        Color color = colorProvider.getNextColor();
                        TrksegWrapper trksegWrapper = new TrksegWrapper(trkseg, color);
                        trackSegmentList.add(trksegWrapper);

                        for (Wpt trkpt : trkseg.getTrkpt()) {
                            CustomMapMarker mapMarkerDot = new CustomMapMarker(
                                    color,
                                    trkpt.getLat().doubleValue(),
                                    trkpt.getLon().doubleValue());
                            putMarkerToMap(trksegWrapper, mapMarkerDot);
                            abstractMapViewer1.addMapMarker(mapMarkerDot);
                        }
                    }
                }
                abstractMapViewer1.setDisplayToFitMapMarkers();
            }
        }
        modelChanged = false;
    }

    @Override
    public String getName() {
        return "GPXVisualElement";
    }

    @Override
    public void addNotify() {
        super.addNotify();
        abstractMapViewer1.setDisplayToFitMapMarkers();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        abstractMapViewer1 = new de.fub.mapviewer.ui.AbstractMapViewer();
        outlineViewPanel = new javax.swing.JPanel();
        outlineView1 = new org.openide.explorer.view.OutlineView("Segment");

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(1000000);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setLeftComponent(abstractMapViewer1);

        outlineViewPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        outlineViewPanel.setPreferredSize(new java.awt.Dimension(0, 100));
        outlineViewPanel.setLayout(new java.awt.BorderLayout());

        outlineView1.setPropertyColumns(new String[] {"visible", "V"});
        outlineView1.setQuickSearchAllowed(false);
        outlineViewPanel.add(outlineView1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(outlineViewPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.mapviewer.ui.AbstractMapViewer abstractMapViewer1;
    private javax.swing.JSplitPane jSplitPane1;
    private org.openide.explorer.view.OutlineView outlineView1;
    private javax.swing.JPanel outlineViewPanel;
    // End of variables declaration//GEN-END:variables

    public void setSplitPanelVisible(boolean visible) {
        this.splitPanelVisble = visible;
        Component rightComponent = jSplitPane1.getRightComponent();
        if (splitPanelVisble) {
            jSplitPane1.setDividerSize(5);
            jSplitPane1.setDividerLocation(.75);
        } else {
            jSplitPane1.setDividerSize(0);
            jSplitPane1.setDividerLocation(1.0);
        }
    }

    public boolean isSplitpanelVisible() {
        return this.splitPanelVisble;
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return obj.getLookup();
    }

    @Override
    public void componentOpened() {
        initOutlineView();
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
        if (modelChanged) {
            update();
        }
        abstractMapViewer1.setDisplayToFitMapMarkers();
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
        if (callback != null) {
            Image icon = obj.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16);
            if (icon != null) {
                callback.getTopComponent().setIcon(icon);
            }
        }
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() != this) {
            modelChanged = true;
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void putMarkerToMap(TrksegWrapper trkseg, CustomMapMarker mapMarkerDot) {
        if (!markerMap.containsKey(trkseg)) {
            markerMap.put(trkseg, new ArrayList<CustomMapMarker>());
        }
        markerMap.get(trkseg).add(mapMarkerDot);
    }

    private static class CustomMapMarker extends MapMarkerDot {

        protected Color color = Color.white;
        private Color selectedColor = null;
        private boolean selected = false;
        private boolean visible = true;

        public CustomMapMarker(double lat, double lon) {
            super(lat, lon);
        }

        public CustomMapMarker(Color color, double lat, double lon) {
            super(color, lat, lon);
            Color selColor = UIManager.getDefaults().getColor("Table.selectionBackground");
            if (selColor != null) {
                selectedColor = selColor;
            }
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public void paint(Graphics g, Point position) {
            if (visible) {
                int circleRadius = 5;
                int circleDiameter = circleRadius * 2;
                g.setColor(selected ? selectedColor : color);
                g.fillOval(position.x - circleDiameter, position.y - circleDiameter, circleDiameter, circleDiameter);
                g.setColor(Color.black);
                g.drawOval(position.x - circleDiameter, position.y - circleDiameter, circleDiameter, circleDiameter);
            }
        }
    }

    private class MouseAdapterImpl extends MouseAdapter {

        public MouseAdapterImpl() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Children children = getExplorerManager().getRootContext().getChildren();
            for (Node node : children.getNodes()) {
                if (node instanceof TrackSegmentBehaviour) {
                    TrackSegmentBehaviour trksegNode = (TrackSegmentBehaviour) node;
                    TrksegWrapper trkseg = node.getLookup().lookup(TrksegWrapper.class);
                    if (trkseg != null && markerMap.containsKey(trkseg)) {
                        for (CustomMapMarker marker : markerMap.get(trkseg)) {
                            marker.setSelected(trksegNode.isSelected());
                            marker.setVisible(trksegNode.isVisible());
                        }
                    }
                    abstractMapViewer1.repaint();
                }
            }
        }
    }
}
