/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

    public static final String PROP_NAME_NAME = "property.name";
    public static final String PROP_NAME_VALUE = "propert.value";
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "value", required = true)
    private String value;
    protected final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Object oldValue = this.name;
        this.name = name;
        pcs.firePropertyChange(PROP_NAME_NAME, oldValue, this.name);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        Object oldValue = this.value;
        this.value = value;
        pcs.firePropertyChange(PROP_NAME_VALUE, oldValue, this.value);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Property{" + "name=" + name + ", value=" + value + '}';
    }
}
