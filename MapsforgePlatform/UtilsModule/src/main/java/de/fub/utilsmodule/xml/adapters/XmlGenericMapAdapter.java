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

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Serdar
 * @param <K>
 * @param <V>
 */
public class XmlGenericMapAdapter<K, V> extends XmlAdapter<MapType<K, V>, Map<K, V>> {

    @Override
    public Map<K, V> unmarshal(MapType<K, V> v) throws Exception {
        HashMap<K, V> map = new HashMap<K, V>();

        for (MapEntry<K, V> mapEntryType : v.getEntry()) {
            map.put(mapEntryType.getKey(), mapEntryType.getValue());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapType marshal(Map<K, V> v) throws Exception {
        MapType<K, V> mapType = new MapType<K, V>();

        for (Map.Entry<K, V> entry : v.entrySet()) {
            MapEntry<K, V> mapEntryType = new MapEntry<K, V>();
            mapEntryType.setKey(entry.getKey());
            mapEntryType.setValue(entry.getValue());
            mapType.getEntry().add(mapEntryType);
        }
        return mapType;
    }
}
