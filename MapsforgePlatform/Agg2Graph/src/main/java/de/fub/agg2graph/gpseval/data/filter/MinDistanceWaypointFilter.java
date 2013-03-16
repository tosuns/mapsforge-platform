package de.fub.agg2graph.gpseval.data.filter;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.structs.GPSCalc;

/**
 * A WaypointFilter used to only return waypoints with a defined distance
 * between each other.
 *
 * It has one parameter "distance" that specifies the minimal distance in meters
 * between two waypoints.
 */
public class MinDistanceWaypointFilter extends WaypointFilter {

    private boolean mReset = true;
    private double mLastAcceptedLat = 0.0;
    private double mLastAcceptedLon = 0.0;
    private double mDistance = 0;

    @Override
    public void reset() {
        mReset = true;
        mDistance = getIntParam("distance", 0);
    }

    @Override
    public boolean filter(Waypoint gpsData) {
        if (!mReset) {
            double distance = GPSCalc.getDistVincentyFast(mLastAcceptedLat, mLastAcceptedLon, gpsData.getLat(), gpsData.getLon());
            if (distance < mDistance) {
                return false;
            }
        }
        mReset = false;
        mLastAcceptedLat = gpsData.getLat();
        mLastAcceptedLon = gpsData.getLon();
        return true;
    }
}
