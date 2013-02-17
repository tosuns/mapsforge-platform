package de.fub.agg2graph.gpseval.data.filter;

import de.fub.agg2graph.gpseval.data.Waypoint;

/**
 * A WaypointFilter used to limit the number of waypoints per track.
 *
 * It has one parameter "limit" that specifies the number of waypoints, that
 * passes the filter for each track.
 */
public class LimitWaypointFilter extends WaypointFilter {

    private int mCount = 0;
    private int mLimit = 0;

    @Override
    public void reset() {
        mCount = 0;
        mLimit = getIntParam("limit", 0);
    }

    @Override
    public boolean filter(Waypoint gpsData) {
        boolean res = mCount < mLimit;

        if (res) {
            mCount++;
        }

        return res;
    }
}
