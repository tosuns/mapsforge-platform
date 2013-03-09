/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.agg2graph.structs.GPSTrack;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Serdar
 */
public class InferenceModelResultDataSet {

    private final Map<String, GPSTrack> gpsTrackMap = new HashMap<String, GPSTrack>();

    public int size() {
        return gpsTrackMap.size();
    }

    public boolean isEmpty() {
        return gpsTrackMap.isEmpty();
    }

    public boolean containsKey(String key) {
        return gpsTrackMap.containsKey(key);
    }

    public boolean containsValue(GPSTrack value) {
        return gpsTrackMap.containsValue(value);
    }

    public GPSTrack get(String key) {
        return gpsTrackMap.get(key);
    }

    public GPSTrack put(String key, GPSTrack value) {
        return gpsTrackMap.put(key, value);
    }

    public GPSTrack remove(String key) {
        return gpsTrackMap.remove(key);
    }

    public void clear() {
        gpsTrackMap.clear();
    }

    public Set<String> keySet() {
        return gpsTrackMap.keySet();
    }

    public Collection<GPSTrack> values() {
        return gpsTrackMap.values();
    }

    public Set<Map.Entry<String, GPSTrack>> entrySet() {
        return gpsTrackMap.entrySet();
    }
}
