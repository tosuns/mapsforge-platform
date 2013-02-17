package de.fub.agg2graph.gpseval.data.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The TrackFilterFactory can be used to create TrackFilter-instances based on
 * the TrackFilter-name.
 */
public class TrackFilterFactory {

    private static TrackFilterFactory instance = new TrackFilterFactory();
    private Map<String, Class<? extends TrackFilter>> mTrackFilterClasses = new HashMap<>();

    private TrackFilterFactory() {
        registerBuiltInTrackFilters();
    }

    /**
     * Register the built-in TrackFilter-classes.
     */
    private void registerBuiltInTrackFilters() {
        registerTrackFilterClass(LimitPerClassTrackFilter.class);
    }

    /**
     * Returns the TrackFilterFactory-instance.
     *
     * @return
     */
    public static TrackFilterFactory getFactory() {
        return instance;
    }

    /**
     * Register a TrackFilter-class with the given name.
     *
     * @param name
     * @param trackFilterClass
     */
    public void registerTrackFilterClass(String name, Class<? extends TrackFilter> trackFilterClass) {
        mTrackFilterClasses.put(name, trackFilterClass);
    }

    /**
     * Register a TrackFilter-class. The TrackFilter's identifier will be used
     * as name.
     *
     * @param trackFilterClass
     */
    public void registerTrackFilterClass(Class<? extends TrackFilter> trackFilterClass) {
        mTrackFilterClasses.put(TrackFilter.getTrackFilterIdentifier(trackFilterClass), trackFilterClass);
    }

    /**
     * Get the TrackFilter-class for the given name.
     *
     * @param name
     * @return
     */
    public Class<? extends TrackFilter> getTrackFilterClass(String name) {
        return mTrackFilterClasses.get(name);
    }

    /**
     * Return a new TrackFilter-instance for the given TrackFilter-name.
     *
     * @param name
     * @return
     */
    public TrackFilter newTrackFilter(String name) {
        TrackFilter trackFilter = null;
        Class<? extends TrackFilter> trackFilterClass = mTrackFilterClasses.get(name);

        if (trackFilterClass != null) {
            try {
                trackFilter = trackFilterClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(TrackFilterFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return trackFilter;
    }
}
