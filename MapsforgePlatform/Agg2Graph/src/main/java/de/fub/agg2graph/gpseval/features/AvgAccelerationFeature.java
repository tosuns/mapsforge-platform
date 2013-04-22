/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Feature that computes the average acceleration of gps tracks.
 *
 * @author Serdar
 */
public class AvgAccelerationFeature extends Feature {

    private int pointCount = 0;
    private Waypoint lastWaypoint = null;
    private double sumAcceleration = 0;
    private Double lastVelocity = null;

    @Override
    public void reset() {
        pointCount = 0;
        lastWaypoint = null;
        sumAcceleration = 0;
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        if (lastWaypoint != null
                && entry != null
                && entry.getTimestamp() != null
                && lastWaypoint.getTimestamp() != null) {
            double timeDiff = ((double) (entry.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime())) / 1000 + 0.1;


            if (timeDiff > 0) {
                double distance = GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), entry.getLat(), entry.getLon());
                double velocity = distance / timeDiff;


                if (lastVelocity != null) {
                    double acceleration = (velocity - lastVelocity) / timeDiff;
                    Logger.getLogger(getClass().getName()).info(MessageFormat.format("acceleration: {0}", acceleration));
                    sumAcceleration += acceleration;
                    pointCount++;
                }
                lastVelocity = velocity;
            }
        }
        lastWaypoint = entry;
    }

    @Override
    public double getResult() {
        return pointCount == 0 ? 0 : sumAcceleration / pointCount;
    }
}
