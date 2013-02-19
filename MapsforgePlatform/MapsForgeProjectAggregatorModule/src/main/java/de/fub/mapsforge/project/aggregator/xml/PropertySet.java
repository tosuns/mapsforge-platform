/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "propertyset")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PropertySet {

    private List<Property> properties;
    private String name;
    private String description;

    public PropertySet() {
    }

    public PropertySet(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public PropertySet(List<Property> properties, String name, String description) {
        this.properties = properties;
        this.name = name;
        this.description = description;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "description", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "property")
    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "PropertySet{" + "properties=" + properties + ", name=" + name + ", description=" + description + '}';
    }
}
