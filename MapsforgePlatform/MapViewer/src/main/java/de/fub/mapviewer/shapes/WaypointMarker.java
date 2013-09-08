/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
