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
public class TrackLengthFeature extends Feature {

    private Waypoint lastWaypoint = null;
    // in meters
    private double length = 0;

    @Override
    public void reset() {
        lastWaypoint = null;
        length = 0;
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        if (lastWaypoint != null) {
            length += GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), entry.getLat(), entry.getLon());
        }
        lastWaypoint = entry;
    }

    @Override
    public double getResult() {
        return length;
    }
}
