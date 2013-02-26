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
public class InferenceModelDescriptor extends Property {

    @XmlElement(name = "features")
    private Features features = new Features();

    public InferenceModelDescriptor() {
    }

    public InferenceModelDescriptor(String name, String description, String javaType) {
        super(javaType, name, description);
    }

    public Features getFeatures() {
        return features;
    }
}
