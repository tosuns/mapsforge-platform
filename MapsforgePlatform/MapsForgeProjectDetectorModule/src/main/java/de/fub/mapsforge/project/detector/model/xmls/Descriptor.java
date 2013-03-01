/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "descriptor", propOrder = {"javaType", "name", "description"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Descriptor {

    @XmlAttribute(name = "javaType", required = true)
    private String javaType;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;

    public Descriptor() {
    }

    public Descriptor(String javaType, String name, String description) {
        this.javaType = javaType;
        this.name = name;
        this.description = description;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Descriptor{" + "javaType=" + javaType + ", name=" + name + ", description=" + description + '}';
    }
}
