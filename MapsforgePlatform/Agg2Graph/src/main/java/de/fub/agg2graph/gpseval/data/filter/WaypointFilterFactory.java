package de.fub.agg2graph.gpseval.data.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The WaypointFilterFactory can be used to create WaypointFilter-instances
 * based on the WaypointFilter-name.
 */
public class WaypointFilterFactory {

    private static WaypointFilterFactory instance = new WaypointFilterFactory();
    private Map<String, Class<? extends WaypointFilter>> mWaypointFilterClasses = new HashMap<>();

    private WaypointFilterFactory() {
        registerBuiltInWaypointFilters();
    }

    /**
     * Register the built-in WaypointFilter-classes.
     */
    private void registerBuiltInWaypointFilters() {
        registerWaypointFilterClass(LimitWaypointFilter.class);
        registerWaypointFilterClass(MinDistanceWaypointFilter.class);
        registerWaypointFilterClass(MinTimeDiffWaypointFilter.class);
    }

    /**
     * Returns the WaypointFilterFactory-instance.
     *
     * @return
     */
    public static WaypointFilterFactory getFactory() {
        return instance;
    }

    /**
     * Register a WaypointFilter-class with the given name.
     *
     * @param name
     * @param waypointFilterClass 
     */
    public void registerWaypointFilterClass(String name, Class<? extends WaypointFilter> waypointFilterClass) {
        mWaypointFilterClasses.put(name, waypointFilterClass);
    }

    /**
     * Register a WaypointFilter-class. The WaypointFilter's identifier will be
     * used as name.
     *
     * @param waypointFilterClass 
     */
    public void registerWaypointFilterClass(Class<? extends WaypointFilter> waypointFilterClass) {
        mWaypointFilterClasses.put(WaypointFilter.getWaypointFilterIdentifier(waypointFilterClass), waypointFilterClass);
    }

    /**
     * Get the WaypointFilter-class for the given name.
     *
     * @param name
     * @return
     */
    public Class<? extends WaypointFilter> getWaypointFilterClass(String name) {
        return mWaypointFilterClasses.get(name);
    }

    /**
     * Return a new WaypointFilter-instance for the given WaypointFilter-name.
     *
     * @param name
     * @return
     */
    public WaypointFilter newWaypointFilter(String name) {
        WaypointFilter waypointFilter = null;
        Class<? extends WaypointFilter> waypointFilterClass = mWaypointFilterClasses.get(name);

        if (waypointFilterClass != null) {
            try {
                waypointFilter = waypointFilterClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(WaypointFilterFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return waypointFilter;
    }
}
