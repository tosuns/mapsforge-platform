/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "inferencemodel", propOrder = {"javaType", "name", "description", "features"})
@XmlAccessorType(XmlAccessType.FIELD)
public class InferenceModelDescriptor {

    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlAttribute(name = "javaType", required = true)
    private String javaType;
    @XmlElement(name = "features")
    private Features features = new Features();

    public InferenceModelDescriptor() {
    }

    public InferenceModelDescriptor(String name, String description, String javaType) {
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

    public Features getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return "InferenceModelDescriptor{" + "name=" + name + ", description=" + description + ", javaType=" + javaType + '}';
    }
}
