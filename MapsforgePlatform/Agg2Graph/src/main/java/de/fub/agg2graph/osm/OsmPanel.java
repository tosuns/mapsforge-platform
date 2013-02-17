/**
 * *****************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Affero Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Contributors: Johannes Mitlmeier - initial API and implementation
 *****************************************************************************
 */
package de.fub.agg2graph.osm;

import de.fub.agg2graph.structs.DoubleRect;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

/**
 * A panel showing OSM data based on {@link JMapViewer}.
 *
 * @author Johannes Mitlmeier
 *
 */
public class OsmPanel extends JMapViewer {

    private static final long serialVersionUID = -5275269629147780997L;
    public static final int MAX_ZOOM = 32;

    public OsmPanel() {
        super(new MemoryTileCache(), 4);
        DefaultMapController controller = new DefaultMapController(this);
        controller.setMovementMouseButton(MouseEvent.BUTTON1);
        setZoomContolsVisible(false);
        setIgnoreRepaint(true);
    }

    public void showArea(DoubleRect area) {
        OSMMapRect mapRect = new OSMMapRect(area);
        addMapRectangle(mapRect);
        setDisplayToFitMapRectangle();
        removeMapRectangle(mapRect);
    }

    @Override
    public void tileLoadingFinished(Tile tile, boolean success) {
        repaint();
    }

    /**
     *
     * @author Johannes Mitlmeier
     *
     */
    public static class OSMMapRect implements MapRectangle {

        public boolean debug = false;
        private Coordinate topLeft;
        private Coordinate bottomRight;

        public OSMMapRect(DoubleRect rect) {
            DoubleRect clone = (DoubleRect) rect.clone();
            clone.enlarge(1.1);
            topLeft = new Coordinate(clone.getMinX(), clone.getMinY());
            bottomRight = new Coordinate(clone.getMaxX(), clone.getMaxY());
        }

        @Override
        public Coordinate getTopLeft() {
            return topLeft;
        }

        @Override
        public Coordinate getBottomRight() {
            return bottomRight;
        }

        @Override
        public void paint(Graphics g, Point p1, Point p2) {
            if (debug) {
                g.setColor(Color.BLACK);
                g.drawRect(p1.x, p2.y, p2.x - p1.x, p1.y - p2.y);
            }
        }
    }
}
