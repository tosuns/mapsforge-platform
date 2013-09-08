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

import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Serdar
 * @param <K>
 * @param <V>
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MapEntry<K, V> {

    private K key;
    private V value;

    public MapEntry() {
    }

    public MapEntry(Map.Entry<K, V> e) {
        key = e.getKey();
        value = e.getValue();
    }

    @XmlAttribute(name = "key", required = true)
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @XmlAttribute(name = "value", required = true)
    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
