/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "inferencemodel", namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema")
@XmlType(name = "inferencemodel")
@XmlAccessorType(XmlAccessType.FIELD)
public class InferenceModelDescriptor extends Descriptor {

    @XmlElement(name = "features")
    private Features features = new Features();
    @XmlElement(name = "inferenceModelProcessHandlers")
    private ProcessHandlers inferenceModelProcessHandlers = new ProcessHandlers();
    @XmlElement(name = "propertysection")
    private PropertySection propertysection = new PropertySection();

    public InferenceModelDescriptor() {
    }

    public InferenceModelDescriptor(String name, String description, String javaType) {
        super(javaType, name, description);
    }

    public Features getFeatures() {
        return features;
    }

    public ProcessHandlers getInferenceModelProcessHandlers() {
        return inferenceModelProcessHandlers;
    }

    public PropertySection getPropertySection() {
        return propertysection;
    }

    @Override
    public String toString() {
        return "InferenceModelDescriptor{" + "javaType=" + getJavaType() + ", name=" + getName() + ", description=" + getDescription() + '}';
    }
}
