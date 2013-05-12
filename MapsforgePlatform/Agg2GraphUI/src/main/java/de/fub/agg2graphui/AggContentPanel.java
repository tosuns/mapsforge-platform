/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.agg2graphui.controller.LayerManager;
import de.fub.agg2graphui.layers.LayerEvent;
import de.fub.agg2graphui.layers.LayerListener;
import de.fub.mapviewer.ui.MapViewer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.explorer.ExplorerManager;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 *
 * @author Serdar
 */
public class AggContentPanel extends MapViewer implements
        LayerManager.Provider,
        ExplorerManager.Provider,
        ChangeListener,
        LayerListener {

    private static final Logger LOG = Logger.getLogger(AggContentPanel.class.getName());
    private static final long serialVersionUID = 1L;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private transient final LayerManager layerManager = new LayerManager(this);
    private boolean labelsVisible = true;
    private int highlightIndex = 0;
    private int savedZIndex = Integer.MAX_VALUE;
    private boolean transparentHighlighting = true;

    /**
     * Creates new form AggContentPanel
     */
    public AggContentPanel() {
        initComponents();
        setScrollWrapEnabled(true);
        layerManager.addChangeListener(AggContentPanel.this);
        layerManager.addLayerListener(AggContentPanel.this);

        for (MouseWheelListener listener : getMouseWheelListeners()) {
            removeMouseWheelListener(listener);
        }

        addMouseWheelListener(new MouseWheelListenerImpl());
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
    public boolean isLabelsVisible() {
        return labelsVisible;
    }

    public void setLabelsVisible(boolean labelsVisible) {
        if (this.labelsVisible != labelsVisible) {
            this.labelsVisible = labelsVisible;
            for (AbstractLayer<?> layer : getLayerManager().getLayers()) {
                if (labelsVisible) {
                    layer.getRenderingOptions().setLabelRenderingType(RenderingOptions.LabelRenderingType.ALWAYS);
                } else {
                    layer.getRenderingOptions().setLabelRenderingType(RenderingOptions.LabelRenderingType.NEVER);
                }
            }
            repaint();
        }
    }

    @Override
    public LayerManager getLayerManager() {
        return layerManager;
    }

    public void addLayer(AbstractLayer<?> layer) {
        assert layer != null;
        getLayerManager().addLayer(layer);
        repaint();
    }

    public void removeLayer(AbstractLayer<?> layer) {
        assert layer != null;
        getLayerManager().remove(layer);
        repaint();
    }

    private void highlightLayer(int wheelRotation) {
        if (savedZIndex != Integer.MAX_VALUE) {
            getLayerManager().getLayers().get(highlightIndex).getRenderingOptions().setzIndex(savedZIndex);
        }
        highlightIndex = (highlightIndex + wheelRotation + getLayerManager().getLayers().size())
                % getLayerManager().getLayers().size();
        savedZIndex = getLayerManager().getLayers().get(highlightIndex).getRenderingOptions().getzIndex();
        getLayerManager().getLayers().get(highlightIndex).getRenderingOptions().setzIndex(999);

        if (transparentHighlighting) {
            for (AbstractLayer<?> layer : getLayerManager().getLayers()) {
                layer.getRenderingOptions().setOpacity(0.3);
            }
            getLayerManager().getLayers().get(highlightIndex).getRenderingOptions().setOpacity(1);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (getTreeLock()) {
            Graphics2D g2d = (Graphics2D) g.create();
            try {
                Coordinate position = getPosition(0, getHeight());
                GPSPoint leftTop = new GPSPoint(position.getLat(), position.getLon());
                position = getPosition(getWidth(), 0);
                GPSPoint bottomRight = new GPSPoint(position.getLat(), position.getLon());
                Rectangle2D.Double gpsArea = new Rectangle2D.Double(leftTop.getLat(), leftTop.getLon(), bottomRight.getLat() - leftTop.getLat(), bottomRight.getLon() - leftTop.getLon());
                Rectangle2D.Double projectionArea = new Rectangle2D.Double(leftTop.getX(), leftTop.getY(), bottomRight.getX() - leftTop.getX(), bottomRight.getY() - leftTop.getY());
                getLayerManager().setArea(gpsArea, projectionArea);
                paintLayers(g2d);
            } finally {
                g2d.dispose();
            }
        }
    }

    private void paintLayers(Graphics2D g2) {

        if (getLayerManager().getLayers().isEmpty() || getLayerManager().getGpsArea() == null) {
            return;
        }
        // paint the layers
        ArrayList<AbstractLayer<?>> paintLayers = new ArrayList<AbstractLayer<?>>();
        paintLayers.addAll(getLayerManager().getLayers());
        // sort layers
        Collections.sort(paintLayers, new LayerComparator());
        for (AbstractLayer<?> layer : paintLayers) {
            layer.paintLayer(g2, new Rectangle(getSize()));
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refreshView();
    }

    @Override
    public void requestRender(LayerEvent layerEvent) {
        refreshView();
    }

    private void refreshView() {
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    repaint();
                }
            });
        }
    }

    void removeAllLayers() {
        layerManager.removeAllLayers();
    }

    private static class LayerComparator implements Comparator<AbstractLayer<?>>, Serializable {

        private static final long serialVersionUID = 1L;

        public LayerComparator() {
        }

        @Override
        public int compare(AbstractLayer<?> o1, AbstractLayer<?> o2) {
            return o1.getRenderingOptions().getzIndex() - o2.getRenderingOptions().getzIndex();
        }
    }

    private class MouseWheelListenerImpl implements MouseWheelListener {

        public MouseWheelListenerImpl() {
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                highlightLayer(e.getWheelRotation());
                e.consume();
            } else {
                // push down
                if (getZoom() <= getTileController().getTileSource().getMaxZoom()
                        || e.getWheelRotation() > 0) {
                    setZoom(getZoom() - e.getWheelRotation(), e.getPoint());
                    LOG.log(Level.FINEST, "zoom level: {0}, Maxzoom: {1}",
                            new Object[]{getZoom(), getTileController().getTileSource().getMaxZoom()});
                }
            }
            layerManager.requestUpdate();
        }
    }
}
