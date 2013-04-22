/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;
import java.util.PriorityQueue;

/**
 *
 * @author Serdar
 */
public class MaxNAccelerationFeature extends Feature {

    private PriorityQueue<Double> priorityQueue = new PriorityQueue<Double>();
    private int maxN = 1;
    private Waypoint lastWaypoint;
    private Double lastVelocity = null;

    public MaxNAccelerationFeature(int n) {
        setMaxN(n);
    }

    @Override
    public void reset() {
        lastWaypoint = null;
        priorityQueue.clear();
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {
        double acceleration = 0;

        if (lastWaypoint != null
                && lastWaypoint.getTimestamp() != null
                && waypoint.getTimestamp() != null) {
            double timeDiff = Math.max(0, (waypoint.getTimestamp().getTime() - lastWaypoint.getTimestamp().getTime()) / 1000);

            if (timeDiff > 0) {

                double distance = GPSCalc.getDistVincentyFast(lastWaypoint.getLat(), lastWaypoint.getLon(), waypoint.getLat(), waypoint.getLon());
                // meter per sec^2
                double velocity = distance / timeDiff;

                if (lastVelocity != null) {

                    acceleration = (velocity - lastVelocity) / timeDiff;
                    addToQueue(acceleration);
                }

                lastVelocity = velocity;
            }
        }
        lastWaypoint = waypoint;
    }

    private void addToQueue(double acceleration) {
        priorityQueue.add(acceleration);

        if (priorityQueue.size() > maxN) {
            priorityQueue.poll();
            assert priorityQueue.size() == maxN;
        }
    }

    @Override
    public double getResult() {
        Double peek = priorityQueue.peek();
        return peek != null ? peek : 0;
    }

    private void setMaxN(int n) {
        assert n > 0;
        this.maxN = n;
        reset();
    }

    public int getMaxN() {
        return maxN;
    }
}
