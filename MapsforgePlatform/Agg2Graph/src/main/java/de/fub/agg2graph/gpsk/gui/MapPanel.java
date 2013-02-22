/*
 * Copyright (C) 2013 Christian Windolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.agg2graph.gpsk.gui;

import de.fub.agg2graph.gpsk.Main;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;
import java.awt.*;
import java.util.List;
import javax.swing.JPanel;

import static java.awt.Color.WHITE;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Windolf
 */
public class MapPanel extends JPanel {

    private static Logger log = Logger.getLogger(MapPanel.class);
    private static final int xSpace = 200, ySpace = 200;
    private static final int X = 0, Y = 1;
    private static final long serialVersionUID = 1L;
    private List<GPSSegment> segments;
    private double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE;
    private double yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
    //display resolution:
    private final int xRes, yRes;

    public MapPanel() {
        super();
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
        DisplayMode dMode = gDevice.getDisplayMode();
        xRes = dMode.getWidth();
        yRes = dMode.getHeight();
        setPreferredSize(new Dimension(xRes - xSpace, yRes - ySpace));
        log.debug("Preferred size: " + getPreferredSize());
    }

    public void setMap(List<GPSTrack> tracks) {
        segments = new LinkedList<GPSSegment>();
        for (GPSTrack track : tracks) {
            for (GPSSegment segment : track) {
                segments.add(segment);
            }
        }
        calculateMinMax();
        detectPanelSize();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(WHITE);
    }

    private void calculateMinMax() {
        for (int i = 0; i < segments.size(); i++) {
            for (int j = 0; j < segments.get(i).size(); j++) {
                if (xMin > segments.get(i).get(j).getX()) {
                    xMin = segments.get(i).get(j).getX();
                }
                if (yMin > segments.get(i).get(j).getY()) {
                    yMin = segments.get(i).get(j).getY();
                }
                if (xMax < segments.get(i).get(j).getX()) {
                    xMax = segments.get(i).get(j).getX();
                }
                if (yMax < segments.get(i).get(j).getY()) {
                    yMax = segments.get(i).get(j).getY();
                }
            }

        }
        //log.debug("The x-values are between " + xMin + " and " + xMax);
        //log.debug("The y-values are between " + yMin + " and " + yMax);
    }

    private void detectPanelSize() {
        if (xMin > xMax) {
            throw new IllegalStateException("the detectPanelSize() method can only"
                    + "be called, AFTER xMin and xMax have been calculated!");
        } else if (yMin > yMax) {
            throw new IllegalStateException("the detectPanelSize() method can only"
                    + "be called, AFTER yMin and yMax have been calculated!");
        }

        double xDiff = xMax - xMin;
        double yDiff = yMax - yMin;

        int x = 0, y = 0;

        double pPpdP = 0; //projection Pixel per display Pixel

        if (xDiff > yDiff) {
            x = xRes - xSpace;
            pPpdP = xDiff / x;
            y = ((int) (yDiff / pPpdP)) + 1;
        } else {
            y = yRes - ySpace;
            pPpdP = yDiff / y;
            x = ((int) (xDiff / pPpdP)) + 1;
        }
        setPreferredSize(new Dimension(x, y));
        Main.mainWindow.pack();
    }

    /**
     * calculates from the projection pixels to the display pixels in the panel
     */
    private class Proj2Display {

        private final int x;
        private final int y;
        private final double pPpdP; //projection Pixel per display Pixel

        Proj2Display(int x, int y, double pPpdP) {
            this.x = x;
            this.y = y;
            this.pPpdP = pPpdP;
        }

        int[] convert(double xProj, double yProj) {
            int[] coordinate = new int[2];
            xProj -= xMin;
            coordinate[X] = (int) (xProj / pPpdP);
            yProj -= yProj;
            coordinate[Y] = (int) (yProj / pPpdP);
            return coordinate;
        }
    }
}
