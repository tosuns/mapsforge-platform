package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * The AvgBearingChangeFeature calculates the average bearing change for a
 * GPS-track.
 */
public class AvgBearingChangeFeature extends Feature {

    private int mLastSegment = 0;
    private double mSumBearingChange = 0;
    private Double mLastBearing = null;
    private int mCount = 0;
    private int mBearingChangeThreshold = 0;

    @Override
    public void reset() {
        mSumBearingChange = 0;
        mLastBearing = null;
        mCount = 0;
        mBearingChangeThreshold = getIntParam("bearingChangeThreshold", 0);
    }

    @Override
    public void addWaypoint(Waypoint entry) {
        // If segment has changed, use current waypoint as new "start"-waypoint
        // for following calculations.
        if (entry.mSegment != mLastSegment) {
            mLastBearing = null;
            mLastSegment = entry.mSegment;
        }

        if (mLastBearing != null) {
            double bearMax = Math.max(mLastBearing, entry.mBearing);
            double bearMin = Math.min(mLastBearing, entry.mBearing);
            double bearChange = bearMax - bearMin;
            bearChange = bearChange > 180 ? 360 - bearChange : bearChange;
            if (bearChange > mBearingChangeThreshold) {
                mSumBearingChange += bearChange;
                ++mCount;
            }
        }
        mLastBearing = entry.mBearing;
    }

    @Override
    public double getResult() {
        return mCount > 0 ? (mSumBearingChange / mCount) : 0;
    }
}
