/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.utilsmodule.xml.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Serdar
 * @param <K>
 * @param <V>
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
