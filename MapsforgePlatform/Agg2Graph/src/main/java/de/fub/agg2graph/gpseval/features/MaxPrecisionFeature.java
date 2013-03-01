package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * The MaxPrecisionFeature determines the maximum precesion of a GPS-track.
 */
public class MaxPrecisionFeature extends Feature {

    private double mMaxPrecision = 0;

    @Override
    public void addWaypoint(Waypoint entry) {
        if (entry.getmPrecision() > mMaxPrecision) {
            mMaxPrecision = entry.getmPrecision();
        }
    }

    @Override
    public double getResult() {
        return mMaxPrecision;
    }

    @Override
    public void reset() {
        mMaxPrecision = 0;
    }
}
