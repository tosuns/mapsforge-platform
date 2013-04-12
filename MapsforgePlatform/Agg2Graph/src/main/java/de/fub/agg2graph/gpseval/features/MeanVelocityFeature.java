/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author Serdar
 */
public class MeanVelocityFeature extends Feature {

    private List<Double> list = new ArrayList<Double>(100);
    private Waypoint lastWaypoint;

    @Override
    public void reset() {
        list.clear();
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        if (lastWaypoint != null
                && lastWaypoint.getTimestamp() != null
                && waypoint.getTimestamp() != null) {
            double velocity = 0;
            long seconds = Math.max(0, (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000);
            if (seconds > 0) {
                velocity = GPSCalc.getDistVincentyFast(waypoint.getLat(), waypoint.getLon(), lastWaypoint.getLat(), lastWaypoint.getLon()) / seconds;
            }
            list.add(velocity);
        }
        lastWaypoint = waypoint;
    }

    @Override
    public double getResult() {
        Collections.sort(list);
        return list.isEmpty() ? 0 : list.get(list.size() / 2);
    }
}
