/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "processunit")
@XmlType(name = "processunit", propOrder = {"javaType", "name", "description", "properties"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessDescriptor {

    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description", required = false)
    private String description;
    @XmlAttribute(name = "javaType", required = true)
    private String javaType;
    @XmlElement(name = "properties", required = false)
    private Properties properties = new Properties();

    public ProcessDescriptor() {
    }

    public ProcessDescriptor(String name, String description, String javaType) {
        this.name = name;
        this.description = description;
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

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "ProcessUnit{" + "name=" + name + ", description=" + description + ", javaType=" + javaType + '}';
    }
}
