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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 *
 * @author Serdar
 */
public abstract class AbstractLayer<T> implements Hideable, PropertyChangeListener {

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

    public RenderingOptions getOptions() {
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
    protected List<T> getItemList() {
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
        drawLine(location1, location2, ro, 1);
    }

    protected void drawLine(ILocation location1, ILocation location2, RenderingOptions ro, float weightFactor) {
        drawLine(location1, location2, ro, weightFactor, false);
    }

    protected void drawLine(ILocation location1, ILocation location2, RenderingOptions ro, float weightFactor, boolean flag) {
        if (location1 == null || location2 == null || ro.getRenderingType() == RenderingOptions.RenderingType.POINTS) {
            return;
        }

        // make sure we only render what's visible
        java.awt.Point p1 = getLayerManager().getMapViewer().getMapPosition(location1.getLat(), location1.getLon(), false);
        java.awt.Point p2 = getLayerManager().getMapViewer().getMapPosition(location2.getLat(), location2.getLon(), false);
        drawables.add(new Line(new XYPoint(location1.getID(), p1.x, p1.y), new XYPoint(location2.getID(), p2.x, p2.y), ro, weightFactor));
    }

    protected void drawPoint(ILocation location, RenderingOptions ro) {
        if (location == null || ro.getRenderingType() == RenderingOptions.RenderingType.LINES) {
            return;
        }
        // make sure we only render what's visible
        java.awt.Point mapPosition = getLayerManager().getMapViewer().getMapPosition(new Coordinate(location.getLat(), location.getLon()), true);
        if (mapPosition == null) {
            return;
        }
        drawables.add(new Point(new XYPoint(location.getID(), mapPosition.x, mapPosition.y), ro));
        setDrawnPointsCounter(getDrawnPointsCounter() + 1);
    }

    public void paintLayer(Graphics g, Rectangle rectangle) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            Font labelFont = new Font(Font.SERIF, Font.PLAIN, 24);
            // queue draw operations
            setDrawnPointsCounter(0);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            synchronized (DRAWABLE_LIST_MUTEX) {

                drawables.clear();

                if (isVisible()) {

                    drawDrawables(drawables, g2d, rectangle);

                    labelFont = labelFont.deriveFont((float) (12 - 2 * Math.log(getDrawnPointsCounter())));
                    Polygon arrowHead = createArrow();
                    for (Drawable drawObject : getDrawables()) {
                        // set color
                        if (drawObject == null) {
                            continue;
                        }
                        RenderingOptions ro = drawObject.getRenderingOptions();
                        Color lineColor = new Color(
                                (ro.getColor().getRed() / 255f),
                                (ro.getColor().getGreen() / 255f),
                                (ro.getColor().getBlue() / 255f),
                                (float) ro.getOpacity());
                        g2d.setColor(lineColor);

                        if (drawObject instanceof Point) {
                            Point p = (Point) drawObject;
                            // draw point
                            if (ro.getRenderingType() == RenderingOptions.RenderingType.POINTS) {
                                float width = ro.getStrokeBaseWidthFactor() * RenderingOptions.getBasicStroke().getLineWidth();
                                g2d.fillOval((int) (p.getAt().getX() - width / 2), (int) (p.getAt().getY() - width / 2), (int) width, (int) width);
                            }
                            // draw label
                            // do not draw label if not fully opaque
                            if (ro.getOpacity() < 1) {
                                continue;
                            }
                            if (p.getAt().getID() == null
                                    || ro.getLabelRenderingType() == RenderingOptions.LabelRenderingType.NEVER) {
                                continue;
                            }
                            if (ro.getLabelRenderingType() == RenderingOptions.LabelRenderingType.INTELLIGENT
                                    && getDrawnPointsCounter() > MAX_VISIBLE_POINTS_INTELLIGENT_LABELS) {
                                continue;
                            }
                            // g2.drawString(p.at.getID(), (int) (p.at.getX() - layerManager
                            // .getMainPanel().getWidth() * 0.0),
                            // (int) (p.at.getY() + layerManager.getMainPanel()
                            // .getHeight() * 0.025));
                            g2d.drawString(p.getAt().getID(), (int) p.getAt().getX(), (int) (p.getAt().getY() + getLayerManager().getMapViewer().getHeight() * 0.025));

                        } else if (drawObject instanceof Line) {
                            // draw line
                            if (ro.getRenderingType() == RenderingOptions.RenderingType.POINTS) {
                                continue;
                            }
                            Line line = (Line) drawObject;
                            float width = line.getWeightFactor() * getLayerManager().getThicknessFactor();
                            g2d.setStroke(ro.getStroke(((Line) drawObject).getWeightFactor() * getLayerManager().getThicknessFactor()));
                            g2d.drawLine((int) line.getFrom().getX(), (int) line.getFrom().getY(), (int) line.getTo().getX(), (int) line.getTo().getY());
                            // make a nice arrow :) (code from
                            // http://stackoverflow.com/a/3094933)
                            if (ro.getRenderingType() == RenderingOptions.RenderingType.LINES) {
                                continue;
                            }
                            if (ro.getRenderingType() == RenderingOptions.RenderingType.INTELLIGENT_ALL
                                    && getDrawnPointsCounter() > MAX_VISIBLE_POINTS_INTELLIGENT_POINT) {
                                continue;
                            }
                            double angle = Math.atan2(line.getTo().getY() - line.getFrom().getY(), line.getTo().getX() - line.getFrom().getX());
                            AffineTransform oldTx = g2d.getTransform();
                            AffineTransform tx = new AffineTransform(oldTx);
                            tx.translate(line.getTo().getX(), line.getTo().getY());
                            tx.rotate((angle - Math.PI / 2d));
                            g2d.setTransform(tx);
                            g2d.setStroke(ro.getStroke(getLayerManager().getThicknessFactor()));
                            g2d.drawLine(0, 0, (int) (6 + width * 2), (int) (-8 - width * 2));
                            g2d.drawLine(0, 0, (int) (-6 - width * 2), (int) (-8 - width * 2));
                            g2d.setTransform(oldTx);
                        }
                    }
                }
            }
        } finally {
            g2d.dispose();
        }
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

    protected abstract void drawDrawables(List<Drawable> drawables, Graphics2D graphics, Rectangle rectangle);
}
