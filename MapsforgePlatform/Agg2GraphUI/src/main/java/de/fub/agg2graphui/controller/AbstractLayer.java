/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.controller;

import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.Hideable;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.structs.XYPoint;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.LayerContainer;
import de.fub.agg2graphui.layers.Drawable;
import de.fub.agg2graphui.layers.LayerEvent;
import de.fub.agg2graphui.layers.LayerListener;
import de.fub.agg2graphui.layers.Line;
import de.fub.agg2graphui.layers.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 * @param <T>
 */
public abstract class AbstractLayer<T> implements Hideable, PropertyChangeListener, Comparable<AbstractLayer<?>> {

    protected static final Logger LOG = Logger.getLogger(AbstractLayer.class.getName());
    protected final Object MUTEX = new Object();
    protected final Object ITEM_LIST_MUTEX = new Object();
    protected final Object DRAWABLE_LIST_MUTEX = new Object();
    private int drawnPointsCounter = 0;
    public static final int MAX_VISIBLE_POINTS_INTELLIGENT_LABELS = 7;
    public static final int MAX_VISIBLE_POINTS_INTELLIGENT_POINT = 10;
    public static final double CLIPPING_AREA_BORDER = 5000;
    public static final int AGG_CONTAINER_WEIGHT_LIMIT = 2;
    private RenderingOptions options;
    private String name;
    private String description;
    private BufferedImage image;
    private Rectangle2D.Double lastGpsArea;
    private final List<T> itemList = new ArrayList<T>();
    private final List<Drawable> drawables = new ArrayList<Drawable>();
    private final Node nodeDelegate;
    private final Set<LayerListener> listeners = new HashSet<LayerListener>();
    private LayerManager layerManager;
    private boolean visible = true;
    private LayerContainer layerPanel;

    public AbstractLayer(String name, RenderingOptions renderingOptions) {
        this(name, "", renderingOptions);
    }

    public AbstractLayer(String name, String description, RenderingOptions renderingOptions) {
        assert name != null;
        assert description != null;
        assert renderingOptions != null;
        this.name = name;
        this.description = description;
        this.options = renderingOptions;
        this.options.addPropertyChangeListener(WeakListeners.propertyChange(AbstractLayer.this, this.options));
        nodeDelegate = new LayerNode(AbstractLayer.this);
    }

    public LayerContainer getLayerPanel() {
        if (layerPanel == null) {
            layerPanel = new LayerContainer(this);
        }
        return layerPanel;
    }

    @Override
    public void setVisible(boolean visibility) {
        if (this.visible != visibility) {
            this.visible = visibility;
            fireRepaintEvent();
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public Node getNodeDelegate() {
        return nodeDelegate;
    }

    public void add(T item) {
        synchronized (ITEM_LIST_MUTEX) {
            itemList.add(item);
            drawables.clear();
            fireRepaintEvent();
        }
    }

    public void addAll(Collection<T> items) {
        synchronized (ITEM_LIST_MUTEX) {
            itemList.addAll(items);
            drawables.clear();
            fireRepaintEvent();
        }
    }

    public void remove(T item) {
        synchronized (ITEM_LIST_MUTEX) {
            itemList.remove(item);
            drawables.clear();
            fireRepaintEvent();
        }
    }

    public void clearRenderObjects() {
        synchronized (ITEM_LIST_MUTEX) {
            itemList.clear();
            drawables.clear();
            fireRepaintEvent();
        }
    }

    public RenderingOptions getRenderingOptions() {
        return options;
    }

    public void setOptions(RenderingOptions renderingOptions) {
        this.options = renderingOptions;
    }

    protected List<Drawable> getDrawables() {
        synchronized (DRAWABLE_LIST_MUTEX) {
            return new ArrayList<Drawable>(drawables);
        }
    }

    /**
     * @return the itemList
     */
    public List<T> getItemList() {
        synchronized (ITEM_LIST_MUTEX) {
            return new ArrayList<T>(itemList);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the listeners
     */
    protected Set<LayerListener> getListeners() {
        return listeners;
    }

    /**
     * @return the drawnPointsCounter
     */
    protected int getDrawnPointsCounter() {
        return drawnPointsCounter;
    }

    /**
     * @param drawnPointsCounter the drawnPointsCounter to set
     */
    protected void setDrawnPointsCounter(int drawnPointsCounter) {
        this.drawnPointsCounter = drawnPointsCounter;
    }

    /**
     * @return the lastGpsArea
     */
    protected Rectangle2D.Double getLastGpsArea() {
        return lastGpsArea;
    }

    /**
     * @param lastGpsArea the lastGpsArea to set
     */
    protected void setLastGpsArea(Rectangle2D.Double lastGpsArea) {
        this.lastGpsArea = lastGpsArea;
    }

    /**
     * @return the layerManager
     */
    public LayerManager getLayerManager() {
        return layerManager;
    }

    /**
     * @param layerManager the layerManager to set
     */
    protected void setLayerManager(LayerManager layerManager) {
        this.layerManager = layerManager;
    }

    protected Polygon createArrow() {
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, (4 * 1));
        arrowHead.addPoint((-4 * 1), (-8 * 1));
        arrowHead.addPoint((4 * 1), (-8 * 1));
        return arrowHead;
    }

    public Rectangle2D.Double projectRect(Rectangle2D.Double source) {
        ILocation ul = new GPSPoint(source.x, source.y);
        ILocation lr = new GPSPoint(source.getMaxX(), source.getMaxY());
        source.x = ul.getX();
        source.y = ul.getY();
        source.width = lr.getX() - ul.getX();
        source.height = lr.getY() - ul.getY();
        return source;
    }

    protected void drawLine(ILocation location1, ILocation location2, RenderingOptions ro) {
        drawLine(location1, location2, null, ro, 1);
    }

    protected void drawLine(ILocation location1, ILocation location2, RenderingOptions ro, float weightFactor) {
        drawLine(location1, location2, null, ro, weightFactor);
    }

    protected void drawLine(ILocation location1, ILocation location2, String label, RenderingOptions ro) {
        drawLine(location1, location2, label, ro, 1);
    }

    protected void drawLine(ILocation location1, ILocation location2, String label, RenderingOptions ro, float weightFactor) {
        drawLine(location1, location2, null, ro, weightFactor, true);
    }

    protected void drawLine(ILocation location1, ILocation location2, String label, RenderingOptions ro, float weightFactor, boolean flag) {
        if (location1 == null || location2 == null || ro.getRenderingType() == RenderingOptions.RenderingType.POINTS) {
            return;
        }

        // make sure we only render what's visible
        Point2D p1 = getLayerManager().getMapViewer().convertGeoPositionToPoint(new GeoPosition(location1.getLat(), location1.getLon()));
        Point2D p2 = getLayerManager().getMapViewer().convertGeoPositionToPoint(new GeoPosition(location2.getLat(), location2.getLon()));
        Line line = new Line(new XYPoint(location1.getID(), p1.getX(), p1.getY()), new XYPoint(location2.getID(), p2.getX(), p2.getY()), ro, weightFactor, flag);
        line.setLabel(label);
        drawables.add(line);
    }

    protected void drawPoint(ILocation location, RenderingOptions ro) {
        if (location == null || ro.getRenderingType() == RenderingOptions.RenderingType.LINES) {
            return;
        }
        // make sure we only render what's visible
        Point2D mapPosition = getLayerManager().getMapViewer().convertGeoPositionToPoint(new GeoPosition(location.getLat(), location.getLon()));
        if (mapPosition == null) {
            return;
        }
        drawables.add(new Point(new XYPoint(location.getID(), mapPosition.getX(), mapPosition.getY()), ro));
        setDrawnPointsCounter(getDrawnPointsCounter() + 1);
    }

    public void paintLayer(Graphics g, Rectangle rectangle) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            // queue draw operations
            setDrawnPointsCounter(0);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            synchronized (DRAWABLE_LIST_MUTEX) {
                drawables.clear();
                if (isVisible()) {
                    drawDrawables(g2d, rectangle);

                    for (Drawable drawObject : getDrawables()) {
                        // set color
                        if (drawObject != null) {
                            RenderingOptions ro = drawObject.getRenderingOptions();
                            Color lineColor = new Color(
                                    (ro.getColor().getRed() / 255f),
                                    (ro.getColor().getGreen() / 255f),
                                    (ro.getColor().getBlue() / 255f),
                                    (float) ro.getOpacity());
                            g2d.setColor(lineColor);

                            if (drawObject instanceof Point) {
                                Point p = (Point) drawObject;
                                paintPoint(p, g2d);
                            } else if (drawObject instanceof Line) {
                                Line line = (Line) drawObject;
                                paintLine(line, g2d);
                            }
                        }
                    }
                }
            }
        } finally {
            g2d.dispose();
        }
    }

    private void paintPoint(Point point, Graphics2D g2d) {
        RenderingOptions renderingOptions = point.getRenderingOptions();
        // draw point
        if (renderingOptions.getRenderingType() == RenderingOptions.RenderingType.POINTS) {
            float width = renderingOptions.getStrokeBaseWidthFactor() * RenderingOptions.getBasicStroke().getLineWidth();
            g2d.fillOval((int) (point.getAt().getX() - width / 2), (int) (point.getAt().getY() - width / 2), (int) width, (int) width);
        }
        // draw label
        // do not draw label if not fully opaque
        if (renderingOptions.getOpacity() >= 1
                && point.getAt().getID() != null
                && renderingOptions.getLabelRenderingType() != RenderingOptions.LabelRenderingType.NEVER
                && (renderingOptions.getLabelRenderingType() != RenderingOptions.LabelRenderingType.INTELLIGENT
                || getDrawnPointsCounter() <= MAX_VISIBLE_POINTS_INTELLIGENT_LABELS)) {

            g2d.drawString(point.getAt().getID(),
                    (int) point.getAt().getX(),
                    (int) (point.getAt().getY() + getLayerManager().getMapViewer().getHeight() * 0.075));
        }
    }

    private void paintLine(Line line, Graphics2D g2d) {
        // draw line
        if (line.getRenderingOptions().getRenderingType() != RenderingOptions.RenderingType.POINTS) {

            float width = line.getWeightFactor() * getLayerManager().getThicknessFactor();
            Stroke stroke = line.getRenderingOptions().getStroke(width);
            g2d.setStroke(stroke);
            int x1 = (int) line.getFrom().getX();
            int y1 = (int) line.getFrom().getY();
            int x2 = (int) line.getTo().getX();
            int y2 = (int) line.getTo().getY();
            g2d.drawLine(x1, y1, x2, y2);

            // draw start end endpoint of line
            if (line.getRenderingOptions().getRenderingType() == RenderingOptions.RenderingType.ALL) {
                Ellipse2D.Double ellipse = new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4);
                g2d.fill(ellipse);
                ellipse = new Ellipse2D.Double(x2 - 2, y2 - 2, 4, 4);
                g2d.fill(ellipse);
            }

            // paint the line label
            paintLineLabel(line, g2d);

            // make a nice arrow :) (code from
            // http://stackoverflow.com/a/3094933)
            if ((line.getRenderingOptions().getRenderingType() != RenderingOptions.RenderingType.LINES)
                    && (line.getRenderingOptions().getRenderingType() != RenderingOptions.RenderingType.INTELLIGENT_ALL
                    || getDrawnPointsCounter() <= MAX_VISIBLE_POINTS_INTELLIGENT_POINT)) {

                // draw arrows
                if (line.isDirected()) {
                    paintArrows(line, g2d);
                }
            }
        }
    }

    private void paintLineLabel(Line line, Graphics2D g2d) {
        if (line.getLabel() != null) {
            double angle = Math.atan2(line.getTo().getY() - line.getFrom().getY(), line.getTo().getX() - line.getFrom().getX());
            AffineTransform oldTx = g2d.getTransform();

            // rotate to vertical axis and draw string
            AffineTransform tx = new AffineTransform(oldTx);
            tx.translate(line.getTo().getX(), line.getTo().getY());
            tx.rotate((angle - Math.PI / 2d));
            g2d.setTransform(tx);
            g2d.drawString(line.getLabel(), 0, 0);
            g2d.setTransform(oldTx);
        }
    }

    private void paintArrows(Line line, Graphics2D g2d) {
        int zoom = getLayerManager().getMapViewer().getZoom();
        float width = line.getWeightFactor() * getLayerManager().getThicknessFactor();
        double angle = Math.atan2(line.getTo().getY() - line.getFrom().getY(), line.getTo().getX() - line.getFrom().getX());
        AffineTransform oldTx = g2d.getTransform();// rotate to vertical axis and draw string
        AffineTransform tx = new AffineTransform(oldTx);
        tx.translate(line.getTo().getX(), line.getTo().getY());
        tx.rotate((angle - Math.PI / 2d));
        g2d.setTransform(tx);
        g2d.setStroke(line.getRenderingOptions().getStroke(getLayerManager().getThicknessFactor()));
        // the diraction arrows of the line
        g2d.drawLine(0, 0, (int) (10 * 1.0 / zoom), (int) (-15 * 1.0 / zoom));
        g2d.drawLine(0, 0, (int) (-10 * 1.0 / zoom), (int) (-15 * 1.0 / zoom));

        g2d.setTransform(oldTx);
    }

    protected void fireRepaintEvent() {
        synchronized (MUTEX) {
            LayerEvent event = new LayerEvent(AbstractLayer.this);
            for (LayerListener listener : getListeners()) {
                listener.requestRender(event);
            }
        }
    }

    public void addLayerListener(LayerListener listener) {
        synchronized (MUTEX) {
            listeners.add(listener);
        }
    }

    public void removeLayerListener(LayerListener listener) {
        synchronized (MUTEX) {
            listeners.remove(listener);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireRepaintEvent();
    }

    protected abstract void drawDrawables(Graphics2D graphics, Rectangle rectangle);

    @Override
    public int compareTo(AbstractLayer<?> layer) {
        return ((Integer) getRenderingOptions().getzIndex()).compareTo(layer.getRenderingOptions().getzIndex());
    }
}
