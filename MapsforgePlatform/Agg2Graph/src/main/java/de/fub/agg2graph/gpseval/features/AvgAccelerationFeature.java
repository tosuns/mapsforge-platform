/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;

/**
 * Feature that computes the average acceleration of gps tracks.
 *
 * @author Serdar
 */
public class AvgAccelerationFeature extends Feature {

    private int pointCount = 0;
    private Waypoint lastWaypoint = null;
    private double sumAcceleration = 0;
    private Double lastVelocity = 0d;

    @Override
    public void reset() {
        pointCount = 0;
        lastWaypoint = null;
        sumAcceleration = 0;
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        if (lastWaypoint != null
                && waypoint != null
                && waypoint.getTimestamp() != null
                && lastWaypoint.getTimestamp() != null) {

            double distance = GPSCalc.getDistVincentyFast(
                    lastWaypoint.getLat(),
                    lastWaypoint.getLon(),
                    waypoint.getLat(),
                    waypoint.getLon());
            double timeDiff = (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000d;

            if (timeDiff > 0) {

                double velocity = distance / timeDiff;


                double acceleration = (velocity - lastVelocity) / timeDiff;
//                Logger.getLogger(getClass().getName()).info(MessageFormat.format("acceleration: {0}", acceleration));
                // consider positive acceleration only
                if (acceleration > 0) {
                    sumAcceleration += acceleration;
                    pointCount++;
                }

                lastVelocity = velocity;
            }
        }
        lastWaypoint = waypoint;
    }

    @Override
    public double getResult() {
        return pointCount == 0 ? 0 : sumAcceleration / pointCount;
    }
}
