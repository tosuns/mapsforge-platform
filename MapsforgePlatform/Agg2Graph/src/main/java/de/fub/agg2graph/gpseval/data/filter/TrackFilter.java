package de.fub.agg2graph.gpseval.data.filter;

import de.fub.agg2graph.gpseval.data.file.TrackFile;
import de.fub.agg2graph.gpseval.utils.Parameterizable;

/**
 * A TrackFilter is used to limit the GPS-tracks.
 */
public abstract class TrackFilter extends Parameterizable {

    private String mIdentifier;

    /**
     * Initialize the filter.
     */
    public abstract void init();

    /**
     * Apply the filter for the given TrackFile and class name.
     *
     * @param trackFile
     * @param className
     * @return True, if the TrackFile passes the filter, false otherwise.
     */
    public abstract boolean filter(TrackFile trackFile, String className);

    /**
     * Get the identifier of an TrackFilter-class based on its class-name.
     *
     * @param trackFilterClass
     * @return
     */
    public static String getTrackFilterIdentifier(Class<? extends TrackFilter> trackFilterClass) {
        String className = trackFilterClass.getSimpleName();
        int endIndex = className.lastIndexOf("TrackFilter");
        return (endIndex < 1) ? className : className.substring(0, endIndex);
    }

    /**
     * Get the identifier of this TrackFilter based on its class name.
     *
     * @return
     */
    public String getIdentifier() {
        if (mIdentifier == null) {
            mIdentifier = getTrackFilterIdentifier(this.getClass());
        }
        return mIdentifier;
    }
}
