/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 */
@XmlType(name = "propertyset")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PropertySet implements ChangeListener {

    private List<Property> properties;
    private String name;
    private String description;
    private final transient ChangeSupport pcs = new ChangeSupport(this);

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
        pcs.fireChange();
    }

    @XmlAttribute(name = "description", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        pcs.fireChange();
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
        if (properties != null) {
            for (Property property : properties) {
                property.addChangeListener(PropertySet.this);
            }
        }
        pcs.fireChange();
    }

    public void addChangeListener(ChangeListener listener) {
        pcs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        pcs.removeChangeListener(listener);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        pcs.fireChange();
    }

    @Override
    public String toString() {
        return "PropertySet{" + "properties=" + properties + ", name=" + name + ", description=" + description + '}';
    }
}
