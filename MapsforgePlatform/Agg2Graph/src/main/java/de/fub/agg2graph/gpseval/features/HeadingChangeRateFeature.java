/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.MutableWaypoint;
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
                double heading = Math.abs(computeHeading(secondLasWaypoint, lastWaypoint, waypoint));
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

    public double computeHeading(Waypoint secondLasWaypoint, Waypoint lastWaypoint, Waypoint waypoint) {
        // get vectors and shift to origin
        MutableWaypoint vector1 = new MutableWaypoint();
        vector1.setLat(lastWaypoint.getLat() - secondLasWaypoint.getLat());
        vector1.setLon(lastWaypoint.getLon() - secondLasWaypoint.getLon());
        MutableWaypoint vector2 = new MutableWaypoint();
        vector2.setLat(waypoint.getLat() - lastWaypoint.getLat());
        vector2.setLon(waypoint.getLon() - lastWaypoint.getLon());

        double x = vector1.getLat() * vector2.getLat() + vector1.getLon() * vector2.getLon();
        double y = Math.sqrt(Math.pow(vector1.getLat(), 2) + Math.pow(vector1.getLon(), 2)) * Math.sqrt(Math.pow(vector2.getLat(), 2) + Math.pow(vector2.getLon(), 2));
        double header = StrictMath.acos(x / y) * 180 / Math.PI;
        return header;
    }
    //    public double computeHeading(Waypoint firstWaypoint, Waypoint secondWaypoint) {
//        // deltaLong in radians
//        double deltaLong = Math.toRadians(secondWaypoint.getLon() - firstWaypoint.getLon());
//        double y = Math.sin(deltaLong * Math.cos(Math.toRadians(secondWaypoint.getLat())));
//        double x = (Math.cos(Math.toRadians(firstWaypoint.getLat())) * Math.sin(Math.toRadians(secondWaypoint.getLat())))
//                - (Math.sin(Math.toRadians(firstWaypoint.getLat())) * Math.cos(Math.toRadians(secondWaypoint.getLat())) * Math.cos(deltaLong));
//        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
//    }
}
