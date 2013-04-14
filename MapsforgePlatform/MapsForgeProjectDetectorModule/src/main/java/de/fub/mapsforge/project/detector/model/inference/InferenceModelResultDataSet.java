/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.gpxmodule.xml.Gpx;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Serdar
 */
public class InferenceModelResultDataSet {

    private final Map<String, List<Gpx>> gpsTrackMap = new HashMap<String, List<Gpx>>();

    public int size() {
        return gpsTrackMap.size();
    }

    public boolean isEmpty() {
        return gpsTrackMap.isEmpty();
    }

    public boolean containsKey(String key) {
        return gpsTrackMap.containsKey(key);
    }

    public boolean containsValue(List<Gpx> value) {
        return gpsTrackMap.containsValue(value);
    }

    public List<Gpx> get(String key) {
        return gpsTrackMap.get(key);
    }

    public List<Gpx> put(String key, List<Gpx> value) {
        return gpsTrackMap.put(key, value);
    }

    public List<Gpx> remove(String key) {
        return gpsTrackMap.remove(key);
    }

    public void clear() {
        gpsTrackMap.clear();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(gpsTrackMap.keySet());
    }

    public Collection<List<Gpx>> values() {
        return Collections.unmodifiableCollection(gpsTrackMap.values());
    }

    public Set<Map.Entry<String, List<Gpx>>> entrySet() {
        return Collections.unmodifiableSet(gpsTrackMap.entrySet());
    }
}
