/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;

/**
 *
 * @author Serdar
 */
public class ErrorRateFeature extends Feature {

    private int errorCount = 0;
    private int totalCount = 0;
    private Waypoint lastWaypoint = null;
    private double lastVelocity = -1;

    @Override
    public void reset() {
        lastWaypoint = null;
        lastVelocity = -1;
        errorCount = 0;
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        if (lastWaypoint != null
                && lastWaypoint.getTimestamp() != null
                && waypoint.getTimestamp() != null) {
            double distance = GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), waypoint.getLat(), waypoint.getLon());
            long timeDiff = (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000;
            double velocity = distance / timeDiff;
            if (velocity > 0
                    && lastVelocity > -1
                    && velocity > lastVelocity * 3) {
                errorCount++;
            }
            lastVelocity = velocity;
            totalCount++;
        }
        lastWaypoint = waypoint;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public double getResult() {
        return totalCount > 0 ? errorCount / lastVelocity : 0;
    }
}
