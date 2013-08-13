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
@XmlType(name = "section")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PropertySection {

    private String name;
    private String description;
    private String id;
    private List<PropertySet> propertySet;

    public PropertySection() {
    }

    public PropertySection(String name, String description) {
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

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "propertyset")
    public List<PropertySet> getPropertySet() {
        if (propertySet == null) {
            propertySet = new ArrayList<PropertySet>();
        }
        return propertySet;
    }

    public void setPropertySet(List<PropertySet> propertySets) {
        this.propertySet = propertySets;
    }

    @Override
    public String toString() {
        return "PropertySection{" + "name=" + name + ", description=" + description + ", id=" + id + '}';
    }
}
