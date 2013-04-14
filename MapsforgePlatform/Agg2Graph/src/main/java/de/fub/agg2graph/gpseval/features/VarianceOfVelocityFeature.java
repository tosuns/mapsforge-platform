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
public class VarianceOfVelocityFeature extends Feature {

    private Waypoint firstWaypoint = null;
    private Waypoint lastWaypoint = null;
    private double length = 0;

    @Override
    public void reset() {
        firstWaypoint = null;
        lastWaypoint = null;
        length = 0;
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        if (lastWaypoint != null) {
            length += GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), waypoint.getLat(), waypoint.getLon());
        }
        if (firstWaypoint == null) {
            firstWaypoint = waypoint;
        }
        lastWaypoint = waypoint;
    }

    @Override
    public double getResult() {
        double varianceOfVelocity = 0;

        if (firstWaypoint != null && lastWaypoint != null
                && firstWaypoint.getTimestamp() != null
                && lastWaypoint.getTimestamp() != null) {
            varianceOfVelocity = length / ((lastWaypoint.getTimestamp().getTime() - firstWaypoint.getTimestamp().getTime()) / 1000);
        }
        return varianceOfVelocity;
    }
}
