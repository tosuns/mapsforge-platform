package de.fub.agg2graph.gpseval;

import de.fub.agg2graph.gpseval.data.filter.TrackFilter;
import de.fub.agg2graph.gpseval.data.filter.WaypointFilter;
import de.fub.agg2graph.gpseval.features.Feature;

import java.util.List;
import java.util.Map;

/**
 * A Config provides all information that is necessary to run a TestCase.
 *
 * <p>This includes a mapping from class-names to folders, which contain the
 * GPS-data-files for the respective class. Moreover it includes filters (for
 * tracks and GPS-waypoints), the feature set and other parameters like the size
 * of the training set, which should be used.</p>
 *
 * @see de.fub.agg2graph.gpseval.TestCase
 */
public interface Config {

    /**
     * Get the mapping from class-names to folders, which contain the
     * GPS-data-files of the respective class.
     *
     * @return
     */
    public Map<String, List<String>> getClassesFolderMapping();

    /**
     * Get the feature set as a list of
     * {@link de.fub.agg2graph.gpseval.features.Feature Feature}-instances.
     *
     * @return
     */
    public List<Feature> getFeatures();

    /**
     * Get the filters for tracks as a list of
     * {@link de.fub.agg2graph.gpseval.data.filter.TrackFilter TrackFilter}-instances.
     *
     * @return
     */
    public List<TrackFilter> getTrackFilters();

    /**
     * Get the filters for GPS-waypoints as a list of
     * {@link de.fub.agg2graph.gpseval.data.filter.WaypointFilter WaypointFilter}-instances.
     *
     * @return
     */
    public List<WaypointFilter> getWaypointFilters();

    /**
     * Get the size which should be used for the training-set. This is a value
     * between 0 and 1 (exclusive).
     *
     * @return A value between 0 and 1.
     */
    public double getTrainingSetSize();

    /**
     * Get the number of folds used for cross validation.
     *
     * @return
     */
    public int getCrossValidationFolds();

    /**
     * Get the name of the config.
     *
     * @return
     */
    public String getName();
}
