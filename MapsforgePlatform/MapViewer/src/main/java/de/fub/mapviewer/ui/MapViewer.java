/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui;

import de.fub.mapviewer.shapes.WaypointMarker;
import de.fub.mapviewer.tilesources.OSMTileFactory;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.MouseInputListener;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.input.PanMouseInputListener;
import org.jdesktop.swingx.input.ZoomMouseWheelListenerCursor;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author Serdar
 */
public class MapViewer extends JXMapViewer {

    private static final long serialVersionUID = 1L;
    private final Set<WaypointMarker> waypoints = new HashSet<WaypointMarker>(200);
    private final CompoundPainter<JXMapViewer> painters;
    private WaypointPainter<WaypointMarker> waypointPainter;

    public MapViewer() {
        super();
        this.painters = new CompoundPainter<JXMapViewer>();

        init();
    }

    private void init() {
        DefaultTileFactory defaultTileFactory = new OSMTileFactory();
        setTileFactory(defaultTileFactory);
        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(MapViewer.this);
        addMouseListener(mia);
        addMouseMotionListener(mia);
        addMouseWheelListener(new ZoomMouseWheelListenerCursor(MapViewer.this));
        waypointPainter = new WaypointPainter<WaypointMarker>();
        waypointPainter.setRenderer(new WaypointRenderer<WaypointMarker>() {

            @Override
            public void paintWaypoint(Graphics2D g, JXMapViewer map, WaypointMarker waypoint) {
                Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
                if (waypoint.isVisible()) {
                    int circleRadius = 5;
                    int circleDiameter = circleRadius * 2;
                    g.setColor(waypoint.isSelected() ? waypoint.getSelectedColor() : waypoint.getColor());
                    g.fillOval((int) point.getX() - circleDiameter, (int) point.getY() - circleDiameter, circleDiameter, circleDiameter);
                    g.setColor(Color.black);
                    g.drawOval((int) point.getX() - circleDiameter, (int) point.getY() - circleDiameter, circleDiameter, circleDiameter);
                }
            }
        });
        painters.addPainter(waypointPainter);
        super.setOverlayPainter(painters);
    }

    public void setDisplayToFitMapMarkers() {
        HashSet<GeoPosition> points = new HashSet<GeoPosition>();
        for (Waypoint waypoint : waypoints) {
            points.add(waypoint.getPosition());
        }
        calculateZoomFrom(points);
    }

    @Override
    public void setOverlayPainter(Painter<? super JXMapViewer> overlay) {
    }

    public void addWaypoint(WaypointMarker waypoint) {
        waypoints.add(waypoint);
        waypointPainter.setWaypoints(waypoints);
        repaint();
    }

    public void removeWaypoint(WaypointMarker waypoint) {
        waypoints.remove(waypoint);
        waypointPainter.setWaypoints(waypoints);
        repaint();
    }

    public void clearWaypoints() {
        waypoints.clear();
        waypointPainter.setWaypoints(waypoints);
        repaint();
    }

    public void addPainter(AbstractPainter<JXMapViewer> painter) {
        assert painter != null;
        painters.addPainter(painter);
        repaint();
    }

    public void removePainter(AbstractPainter<JXMapViewer> painter) {
        painters.removePainter(painter);
        repaint();
    }

}
