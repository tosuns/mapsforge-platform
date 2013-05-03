package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;

/**
 * The AvgSpeedFeature calculates the average speed for a GPS-track.
 */
public class AvgVelocityFeature extends Feature {

    private int mCount = 0;
    private double mSumSpeed = 0;
    private Waypoint lasWaypoint = null;

    @Override
    public void addWaypoint(Waypoint entry) {
        mSumSpeed += entry.getSpeed();
        double velocity = entry.getSpeed();
        if (velocity == 0 && lasWaypoint != null) {
            if (entry.getTimestamp() != null && lasWaypoint.getTimestamp() != null) {
                double distance = GPSCalc.getDistVincentyFast(entry.getLat(), entry.getLon(), lasWaypoint.getLat(), lasWaypoint.getLon());
                long timeDiff = (entry.getTimestamp().getTime() - lasWaypoint.getTimestamp().getTime()) / 1000;
                velocity = distance / timeDiff;
            }
        }
        if (velocity > 0
                && !Double.isInfinite(velocity)
                && !Double.isNaN(velocity)) {
            mSumSpeed += velocity;
            ++mCount;
        }
        lasWaypoint = entry;
    }

    @Override
    public double getResult() {
        return mCount > 0 ? (mSumSpeed / mCount) : 0;
    }

    @Override
    public void reset() {
        mCount = 0;
        mSumSpeed = 0;
        lasWaypoint = null;
    }
}
