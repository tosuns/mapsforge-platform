/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Formula: h = atan2( sin(deltalong) * cos(lat2), cos(lat1) * sin(lat2) −
 * sin(lat1) * cos(lat2) * cos(deltalong) )
 *
 *
 * @author Serdar
 */
public class HeadingChangeRateFeature extends Feature {

    // value in degree
    private double headingThreshold = 0;
    private double totalSegmentLength = 0;
    private List<Waypoint> waypointList = new ArrayList<Waypoint>();
    private Waypoint lastWaypoint = null;
    private Waypoint secondLasWaypoint = null;

    @Override
    public void reset() {
        totalSegmentLength = 0;
        lastWaypoint = null;
        secondLasWaypoint = null;
        waypointList.clear();
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        if (lastWaypoint != null) {

            // compute distance and add to total segment length
            totalSegmentLength += GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), waypoint.getLat(), waypoint.getLon());

            if (secondLasWaypoint != null) {
                // heading of lastWaypoint
                double heading = Math.abs(GPSCalc.computeHeading(secondLasWaypoint, lastWaypoint, waypoint));
                if (heading > headingThreshold) {
                    waypointList.add(lastWaypoint);
                }
            }
        }
        secondLasWaypoint = lastWaypoint;
        lastWaypoint = waypoint;
    }

    @Override
    public double getResult() {
        return totalSegmentLength > 0 ? waypointList.size() / totalSegmentLength : 0;
    }

    public double getHeadingThreshold() {
        return headingThreshold;
    }

    public void setHeadingThreshold(double headingThreshold) {
        this.headingThreshold = headingThreshold;
    }
}
