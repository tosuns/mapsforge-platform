/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml.adapter;

import de.fub.mapforgeproject.xml.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Serdar
 */
public class PropertyType {

    private List<Property> property = new ArrayList<Property>();

    public PropertyType() {
    }

    public PropertyType(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            property.add(new Property(e.getKey(), e.getValue()));
        }
    }

    public List<Property> getProperty() {
        return property;
    }

    public void setProperty(List<Property> entry) {
        this.property = entry;
    }
}
