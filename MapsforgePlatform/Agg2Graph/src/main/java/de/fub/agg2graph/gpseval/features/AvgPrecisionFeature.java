package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * The AvgPrecisionFeature calculates the average precision for a GPS-track.
 */
public class AvgPrecisionFeature extends Feature {

    private int mCount = 0;
    private double mSumPrecision = 0;

    @Override
    public void addWaypoint(Waypoint entry) {
        mSumPrecision += entry.getmPrecision();
        ++mCount;
    }

    @Override
    public double getResult() {
        return mCount > 0 ? (mSumPrecision / mCount) : 0;
    }

    @Override
    public void reset() {
        mCount = 0;
        mSumPrecision = 0;
    }
}
