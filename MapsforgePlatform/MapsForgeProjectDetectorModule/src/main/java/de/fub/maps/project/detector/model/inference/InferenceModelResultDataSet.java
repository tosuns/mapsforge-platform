/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference;

import de.fub.maps.project.detector.model.gpx.TrackSegment;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Serdar
 */
public class InferenceModelResultDataSet {

    private final HashMap<String, HashSet<TrackSegment>> gpsTrackMap = new HashMap<String, HashSet<TrackSegment>>();

    public int size() {
        return gpsTrackMap.size();
    }

    public boolean isEmpty() {
        return gpsTrackMap.isEmpty();
    }

    public boolean containsKey(String key) {
        return gpsTrackMap.containsKey(key);
    }

    public boolean containsValue(HashSet<TrackSegment> value) {
        return gpsTrackMap.containsValue(value);
    }

    public HashSet<TrackSegment> get(String key) {
        return gpsTrackMap.get(key);
    }

    public HashSet<TrackSegment> put(String key, HashSet<TrackSegment> value) {
        return gpsTrackMap.put(key, value);
    }

    public HashSet<TrackSegment> remove(String key) {
        return gpsTrackMap.remove(key);
    }

    public void clear() {
        gpsTrackMap.clear();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(gpsTrackMap.keySet());
    }

    public Collection<HashSet<TrackSegment>> values() {
        return gpsTrackMap.values();
    }

    public Set<Map.Entry<String, HashSet<TrackSegment>>> entrySet() {
        return Collections.unmodifiableSet(gpsTrackMap.entrySet());
    }
}
