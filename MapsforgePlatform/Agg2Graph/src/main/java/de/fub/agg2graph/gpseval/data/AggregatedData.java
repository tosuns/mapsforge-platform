package de.fub.agg2graph.gpseval.data;

import de.fub.agg2graph.gpseval.features.Feature;

import java.util.HashMap;
import java.util.Map;

/**
 * An AggregatedData-instance contains aggregated data for a single GPS-track.
 * <p>These data is stored on a per-feature-basis. For example you may have a
 * feature "avgSpeed", which value was calculated based on each waypoint of a
 * GPS-track.</p>
 */
public class AggregatedData {

    private Map<String, Double> mData = new HashMap<>();

    /**
     * Add data for a feature-id.
     *
     * <p>Mostly you would use
     * {@link de.fub.agg2graph.gpseval.data.AggregatedData#addData(Feature feature) addData}
     * to add new feature-data.</p>
     *
     * @param featureId
     * @param val
     */
    public void addData(String featureId, double val) {
        mData.put(featureId, val);
    }

    /**
     * Add the data given by the Feature-instance.
     *
     * <p>The Feature's result-value will be added to the
     * AggregatedData-instance (for the Feature's identifier).</p>
     *
     * @param feature The Feature-instance to get data from
     */
    public void addData(Feature feature) {
        mData.put(feature.getIdentifier(), feature.getResult());
    }

    /**
     * Returns the data for a given Feature-id.
     *
     * @param featureId 
     * @return
     */
    public double getData(String featureId) {
        return mData.get(featureId);
    }
}
