/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import de.fub.utilsmodule.beans.PropertyDescriptor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Property implements PropertyDescriptor {

    private String name;
    private String value;
    private String javaType;
    private String description;

    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute(name = "name", required = true)
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "value", required = true)
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @XmlAttribute(name = "type", required = true)
    @Override
    public String getJavaType() {
        return javaType;
    }

    @Override
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    @Override
    public String toString() {
        return "Property{" + "name=" + name + ", value=" + value + '}';
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
