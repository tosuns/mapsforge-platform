package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * The MinPrecisionFeature determines the minimum precesion of a GPS-track.
 */
public class MinPrecisionFeature extends Feature {

    private double mMinPrecision = Integer.MAX_VALUE;

    @Override
    public void addWaypoint(Waypoint entry) {
        if (entry.getmPrecision() < mMinPrecision) {
            mMinPrecision = entry.getmPrecision();
        }
    }

    @Override
    public double getResult() {
        return mMinPrecision;
    }

    @Override
    public void reset() {
        mMinPrecision = Integer.MAX_VALUE;
    }
}
