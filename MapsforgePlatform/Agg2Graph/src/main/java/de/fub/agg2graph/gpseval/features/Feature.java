package de.fub.agg2graph.gpseval.features;

import de.fub.agg2graph.gpseval.data.Waypoint;
import de.fub.agg2graph.gpseval.utils.Parameterizable;

/**
 * A Feature-instance is used to combine Waypoint-data of a GPS-track into a
 * single value, for example to determine average speed for a GPS-track.
 */
public abstract class Feature extends Parameterizable {

    private String mIdentifier;

    /**
     * Reset the Feature-instance so that it can be used for the next GPS-track.
     */
    public abstract void reset();

    /**
     * Add the specified Waypoint.
     *
     * @param entry
     */
    public abstract void addWaypoint(Waypoint entry);

    /**
     * Get the result which was calculated based on the added Waypoints.
     *
     * @return
     */
    public abstract double getResult();

    /**
     * Get the identifier of a Feature-class based on its class-name.
     *
     * @param featureClass
     * @return
     */
    public static String getFeatureIdentifier(Class<? extends Feature> featureClass) {
        String className = featureClass.getSimpleName();
        int endIndex = className.lastIndexOf("Feature");
        return (endIndex < 1) ? className : className.substring(0, endIndex);
    }

    /**
     * Get the identifier of this Feature based on its class name.
     *
     * @return
     */
    public String getIdentifier() {
        if (mIdentifier == null) {
            mIdentifier = getFeatureIdentifier(this.getClass());
        }
        return mIdentifier;
    }
}
