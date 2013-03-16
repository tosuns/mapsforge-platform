/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import java.util.PriorityQueue;

/**
 * Feature that computes the n-th max value of gps tracks. Inital n has the
 * default value of 1.
 *
 * @author Serdar
 */
public class MaxNSpeedFeature extends Feature {

    private PriorityQueue<Double> priorityQueue = new PriorityQueue<Double>();
    private int n = 1;

    public MaxNSpeedFeature() {
        this(1);
    }

    public MaxNSpeedFeature(int nthMaxValue) {
        setMaxN(nthMaxValue);
    }

    public int getMaxN() {
        return n;
    }

    /**
     * sets the n max value that should be computed from the gps track.
     *
     * @param n > 0
     */
    public final void setMaxN(int n) {
        assert n > 0;
        this.n = n;
        reset();
    }

    @Override
    public void reset() {
        priorityQueue.clear();
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        priorityQueue.add(entry.getSpeed());
        if (priorityQueue.size() > n) {
            priorityQueue.poll();
            assert priorityQueue.size() <= 3;
        }
    }

    @Override
    public double getResult() {
        return priorityQueue.peek();
    }
}
