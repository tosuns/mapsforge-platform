/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.controller;

import de.fub.agg2graphui.layers.LayerEvent;
import de.fub.agg2graphui.layers.LayerListener;
import de.fub.mapviewer.ui.MapViewer;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 */
public class LayerManager implements LayerListener {

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final List<AbstractLayer<?>> layers = Collections.synchronizedList(new ArrayList<AbstractLayer<?>>());
    private final Set<LayerListener> layerListenerSet = Collections.synchronizedSet(new HashSet<LayerListener>());
    private Rectangle2D.Double projectionArea; // visible rect in projection
    private Rectangle2D.Double gpsArea; // visible rect in gps coordinates
    private float thicknessFactor = 1;
    private boolean renderWeight = true; // false = renderAvgDist
    private final MapViewer mapViewer;

    public LayerManager(MapViewer mapViewer) {
        assert mapViewer != null;
        this.mapViewer = mapViewer;
    }

    public void requestUpdate() {
        for (AbstractLayer<?> layer : getLayers()) {
            layer.fireRepaintEvent();
        }
    }

    public Dimension getSize() {
        return getMapViewer().getSize();
    }

    public MapViewer getMapViewer() {
        return mapViewer;
    }

    public Rectangle2D.Double getProjectionArea() {
        return projectionArea;
    }

    public void setArea(Rectangle2D.Double gpsArea,
            Rectangle2D.Double projectionArea) {
        if (gpsArea == null) {
            this.gpsArea = null;
        } else {
            this.gpsArea = (Rectangle2D.Double) gpsArea.clone();
        }
        if (projectionArea == null) {
            this.projectionArea = null;
        } else {
            this.projectionArea = (Rectangle2D.Double) projectionArea.clone();
        }
    }

    public Rectangle2D.Double getGpsArea() {
        return gpsArea;
    }

    public List<AbstractLayer<?>> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    public synchronized void addLayer(AbstractLayer<?> layer) {
        assert layer != null;
        layer.getRenderingOptions().setzIndex(layers.size());
        layers.add(layer);
        layer.setLayerManager(this);
        layer.addLayerListener(LayerManager.this);
        changeSupport.fireChange();
    }

    public synchronized void remove(AbstractLayer<?> layer) {
        assert layer != null;
        if (layers.remove(layer)) {
            layer.removeLayerListener(LayerManager.this);
            layer.setLayerManager(null);
            changeSupport.fireChange();
        }
    }

    public synchronized void removeAllLayers() {
        layers.clear();
        changeSupport.fireChange();
    }

    public synchronized AbstractLayer<?> getLayer(String name) {
        for (AbstractLayer<?> layer : getLayers()) {
            if (layer.getName().equals(name)) {
                return layer;
            }
        }
        return null; // old: new Layer(name, new RenderingOptions());
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public void addLayerListener(LayerListener layerListener) {
        layerListenerSet.add(layerListener);
    }

    public void removeLayerListener(LayerListener layerListener) {
        layerListenerSet.remove(layerListener);
    }

    public boolean add(LayerListener layerListener) {
        return layerListenerSet.add(layerListener);
    }

    public boolean remove(LayerListener layerListener) {
        return layerListenerSet.remove(layerListener);
    }

    public float getThicknessFactor() {
        return thicknessFactor;
    }

    public void setThicknessFactor(float thicknessFactor) {
        this.thicknessFactor = thicknessFactor;
    }

    public boolean isRenderWeight() {
        return renderWeight;
    }

    public void setRenderWeight(boolean renderWeight) {
        this.renderWeight = renderWeight;
    }

    @Override
    public synchronized void requestRender(LayerEvent layerEvent) {
        for (LayerListener layerListener : layerListenerSet) {
            layerListener.requestRender(layerEvent);
        }
    }

    public interface Provider {

        public LayerManager getLayerManager();
    }
}
