/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.xml.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Serdar
 */

public class MapType<K, V> {

    private List<MapEntry<K, V>> entry = new ArrayList<MapEntry<K, V>>();

    public MapType() {
    }

    public MapType(Map<K, V> map) {
        for (Map.Entry<K, V> e : map.entrySet()) {
            entry.add(new MapEntry<K, V>(e));
        }
    }

    public List<MapEntry<K, V>> getEntry() {
        return entry;
    }

    public void setEntry(List<MapEntry<K, V>> entry) {
        this.entry = entry;
    }
}
