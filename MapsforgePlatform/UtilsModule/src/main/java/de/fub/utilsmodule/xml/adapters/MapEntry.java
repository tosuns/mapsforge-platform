/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.xml.adapters;

import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Serdar
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
