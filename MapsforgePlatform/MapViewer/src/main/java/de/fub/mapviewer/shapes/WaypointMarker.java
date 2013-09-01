/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.UIManager;
import org.jdesktop.swingx.mapviewer.DefaultWaypoint;

/**
 *
 * @author Serdar
 */
public class WaypointMarker extends DefaultWaypoint {

    protected Color color = Color.white;
    private Color selectedColor = null;
    private boolean selected = false;
    private boolean visible = true;

    public WaypointMarker(double lat, double lon) {
        this(Color.BLUE, lat, lon);
    }

    public WaypointMarker(Color color, double lat, double lon) {
        super(lat, lon);
        Color selColor = UIManager.getDefaults().getColor("Table.selectionBackground");
        if (selColor != null) {
            selectedColor = selColor;
        }
        this.color = color;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
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
