package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * The AvgSpeedFeature calculates the average speed for a GPS-track.
 */
public class AvgSpeedFeature extends Feature {

    private int mCount = 0;
    private double mSumSpeed = 0;

    @Override
    public void addWaypoint(Waypoint entry) {
        mSumSpeed += entry.getmSpeed();
        if (entry.getmSpeed() > 0) {
            ++mCount;
        }
    }

    @Override
    public double getResult() {
        return mCount > 0 ? (mSumSpeed / mCount) : 0;
    }

    @Override
    public void reset() {
        mCount = 0;
        mSumSpeed = 0;
    }
}
