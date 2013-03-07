/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * Feature that computes the average acceleration of gps tracks.
 *
 * @author Serdar
 */
public class AvgAccelerationFeature extends Feature {

    private int pointCount = 0;
    private Waypoint lastWaypoint = null;
    private double sumAcceleration = 0;

    @Override
    public void reset() {
        pointCount = 0;
        lastWaypoint = null;
        sumAcceleration = 0;
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        if (lastWaypoint != null) {
            double timeDiff = ((double) (entry.getmDate().getTime() - lastWaypoint.getmDate().getTime())) / 1000;
            double speedDiff = entry.getmSpeed() - lastWaypoint.getmSpeed();

            if (timeDiff > 0) {
                double acceleration = speedDiff / timeDiff;
                sumAcceleration += acceleration;
                pointCount++;
            }
        }
        lastWaypoint = entry;
    }

    @Override
    public double getResult() {
        return pointCount == 0 ? 0 : sumAcceleration / pointCount;
    }
}
