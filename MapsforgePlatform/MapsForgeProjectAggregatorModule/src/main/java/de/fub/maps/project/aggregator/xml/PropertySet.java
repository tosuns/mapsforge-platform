/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.xml;

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
    private String id;

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

    @XmlAttribute(name = "id", required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description", required = true)
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
        return "PropertySet{" + "name=" + name + ", description=" + description + ", id=" + id + '}';
    }
}
