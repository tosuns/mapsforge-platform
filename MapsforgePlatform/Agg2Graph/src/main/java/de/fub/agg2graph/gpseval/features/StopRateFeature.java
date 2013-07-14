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
 * Computes the Stop rate of an GPS segment according to Yu Zheng et al.
 * "Understanding Mobility Based on GPS Data".
 *
 * ST = |P_s| / Distance, and P_s = {p_i | p_i € GPS-Segment, p_i.V &le; V_s}
 *
 * where V_s the minimum velocity threshold
 *
 * @author Serdar
 */
public class StopRateFeature extends Feature {

    private Waypoint lastWaypoint = null;
    private List<Waypoint> waypointList = new ArrayList<Waypoint>(300);
    /// meter per seconds
    private double minimumVelocityThreshold = 1;
    private double totalSegmentLength = 0;

    public StopRateFeature() {
    }

    public StopRateFeature(double minimumVelocityThreshold) {
        this.minimumVelocityThreshold = minimumVelocityThreshold;
    }

    @Override
    public void reset() {
        totalSegmentLength = 0;
        lastWaypoint = null;
        waypointList.clear();

    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        if (waypoint.getTimestamp() != null) {

            if (lastWaypoint != null) {
                long timeDiff = (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000;
                double distance = GPSCalc.getDistVincentyFast(waypoint.getLat(), waypoint.getLon(), lastWaypoint.getLat(), lastWaypoint.getLon());
                totalSegmentLength += distance;
                // meter per second
                double velocity = distance / timeDiff;

                if (velocity <= minimumVelocityThreshold) {
                    waypointList.add(waypoint);
                }
            }
            lastWaypoint = waypoint;
        }
    }

    @Override
    public double getResult() {
        return totalSegmentLength > 0 ? waypointList.size() / totalSegmentLength : Math.random();  // only for the propose to not have duplicate nominal value labels.
    }

    public double getMinimumVelocityThreshold() {
        return minimumVelocityThreshold;
    }

    public void setMinimumVelocityThreshold(double minimumThreshold) {
        this.minimumVelocityThreshold = minimumThreshold;
    }
}
