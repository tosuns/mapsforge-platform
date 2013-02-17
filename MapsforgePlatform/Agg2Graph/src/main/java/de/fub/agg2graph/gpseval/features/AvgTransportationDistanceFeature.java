package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.TransportationDistance;
import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 *
 */
public class AvgTransportationDistanceFeature extends Feature {

    private int mCount = 0;
    private double mSumDistance = 0;

    @Override
    public void reset() {
        mCount = 0;
        mSumDistance = 0;
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        mSumDistance += TransportationDistance.getInstance().getNearestDistance(entry.mLat, entry.mLon);
        ++mCount;
    }

    @Override
    public double getResult() {
        return mCount > 0 ? (mSumDistance / mCount) : 0;
    }
}
