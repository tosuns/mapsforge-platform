package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * The MaxSpeedFeature determines the maximum speed of a GPS-track.
 */
public class MaxSpeedFeature extends Feature {

    private double mMaxSpeed = 0;

    @Override
    public void addWaypoint(Waypoint entry) {
        if (entry.getmSpeed() > mMaxSpeed) {
            mMaxSpeed = entry.getmSpeed();
        }
    }

    @Override
    public double getResult() {
        return mMaxSpeed;
    }

    @Override
    public void reset() {
        mMaxSpeed = 0;
    }
}
