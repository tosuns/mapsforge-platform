/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 */
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Property {

    private String name;
    private String value;
    private String javaType;
    private final transient ChangeSupport pcs = new ChangeSupport(this);

    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        pcs.fireChange();
    }

    @XmlAttribute(name = "value", required = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        pcs.fireChange();
    }

    @XmlAttribute(name = "type", required = true)
    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
        pcs.fireChange();
    }

    public void addChangeListener(ChangeListener listener) {
        pcs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        pcs.removeChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Property{" + "name=" + name + ", value=" + value + '}';
    }
}
