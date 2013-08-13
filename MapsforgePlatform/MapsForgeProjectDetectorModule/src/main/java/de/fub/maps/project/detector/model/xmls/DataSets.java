/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "datasets", propOrder = {"trainingset", "inferenceset"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSets {

    @XmlElement(name = "inferenceset", required = true)
    private InferenceSet inferenceset = new InferenceSet();
    @XmlElement(name = "trainingset", required = true)
    private TrainingSet trainingset = new TrainingSet();

    public DataSets() {
    }

    public InferenceSet getInferenceSet() {
        return inferenceset;
    }

    public TrainingSet getTrainingSet() {
        return trainingset;
    }
}
