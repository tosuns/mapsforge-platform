package de.fub.agg2graph.gpseval.data.filter;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.utils.Parameterizable;

/**
 * A WaypointFilter is used to limit the Waypoints of a GPS-track.
 */
public abstract class WaypointFilter extends Parameterizable {

    private String mIdentifier;

    /**
     * Resets the waypoint filter so that it is prepared to be used with the
     * next track.
     */
    public abstract void reset();

    /**
     * Apply the filter for the given Waypoint.
     *
     * @param gpsData
     * @return True, if the Waypoint passes the filter, false otherwise.
     */
    public abstract boolean filter(Waypoint gpsData);

    /**
     * Get the identifier of an WaypointFilter-class based on its class-name.
     *
     * @param waypointFilterClass
     * @return
     */
    public static String getWaypointFilterIdentifier(Class<? extends WaypointFilter> waypointFilterClass) {
        String className = waypointFilterClass.getSimpleName();
        int endIndex = className.lastIndexOf("WaypointFilter");
        return (endIndex < 1) ? className : className.substring(0, endIndex);
    }

    /**
     * Get the identifier of this WaypointFilter based on its class name.
     *
     * @return
     */
    public String getIdentifier() {
        if (mIdentifier == null) {
            mIdentifier = getWaypointFilterIdentifier(this.getClass());
        }
        return mIdentifier;
    }
}
