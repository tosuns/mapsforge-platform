/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.shapes;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

/**
 *
 * @author Serdar
 */
public class OsmRectangle implements MapRectangle {

    public static final OsmRectangle DEFAULT_RECTANGLE = new OsmRectangle(0, 0, 0, 0);
    private final Coordinate topLeft;
    private final Coordinate bottomRight;

    public OsmRectangle(Rectangle rectangle) {
        this(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public OsmRectangle(double left, double top, double right, double bottom) {
        this(new Coordinate(top, left), new Coordinate(bottom, right));
    }

    public OsmRectangle(Coordinate topLeft, Coordinate bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public Coordinate getTopLeft() {
        return topLeft;
    }

    @Override
    public Coordinate getBottomRight() {
        return bottomRight;
    }

    public void setTopLeft(Coordinate topLeft) {
        getTopLeft().setLat(topLeft.getLat());
        getTopLeft().setLon(topLeft.getLon());
    }

    public void setBottomRight(Coordinate bottomRight) {
        getBottomRight().setLat(bottomRight.getLat());
        getBottomRight().setLon(bottomRight.getLon());
    }

    public void setLeft(double left) {
        getTopLeft().setLon(left);
    }

    public void setTop(double top) {
        getTopLeft().setLat(top);
    }

    public void setRight(double right) {
        getBottomRight().setLon(right);
    }

    public void setBottom(double bottom) {
        getBottomRight().setLat(bottom);
    }

    @Override
    public void paint(Graphics grphcs, Point point, Point point1) {
        // do nothing
    }
}
