package de.fub.agg2graph.gpseval.data.filter;

import de.fub.agg2graph.gpseval.data.Waypoint;

import java.util.Date;

/**
 * A WaypointFilter used to only return waypoints with a defined time interval
 * between each other.
 *
 * It has one parameter "timeDiff" that specifies the minimal time in seconds
 * between two waypoints.
 */
public class MinTimeDiffWaypointFilter extends WaypointFilter {

    private Date mLastAcceptedWaypointTime;
    private int mInterval = 0;

    @Override
    public void reset() {
        mLastAcceptedWaypointTime = null;
        mInterval = getIntParam("timeDiff", 0);
    }

    @Override
    public boolean filter(Waypoint gpsData) {
        if (mLastAcceptedWaypointTime != null) {
            long time1 = mLastAcceptedWaypointTime.getTime();
            long time2 = gpsData.getTimestamp().getTime();
            if ((time2 - time1) < (mInterval * 1000) - 1) {
                return false;
            }
        }
        mLastAcceptedWaypointTime = gpsData.getTimestamp();
        return true;
    }
}
