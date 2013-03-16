package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 *
 *
 */
public class MaxAccelerationFeature extends Feature {

    private int mLastSegment = 0;
    private Waypoint mLastWaypoint;
    private double mMaxAcc = 0;

    @Override
    public void addWaypoint(Waypoint entry) {
        // If segment has changed, use current waypoint as new "start"-waypoint
        // for following calculations.
        if (entry.getSegment() != mLastSegment) {
            mLastWaypoint = null;
            mLastSegment = entry.getSegment();
        }

        if (mLastWaypoint != null) {
            double timeDiff = ((double) (entry.getTimestamp().getTime() - mLastWaypoint.getTimestamp().getTime())) / 1000;
            double speedDiff = entry.getSpeed() - mLastWaypoint.getSpeed();

            if (timeDiff > 0) {
                double acc = speedDiff / timeDiff;
                if (acc > mMaxAcc) {
                    mMaxAcc = acc;
                }
            }
        }
        mLastWaypoint = entry;
    }

    @Override
    public double getResult() {
        return mMaxAcc;
    }

    @Override
    public void reset() {
        mLastWaypoint = null;
        mMaxAcc = 0;
    }
}
