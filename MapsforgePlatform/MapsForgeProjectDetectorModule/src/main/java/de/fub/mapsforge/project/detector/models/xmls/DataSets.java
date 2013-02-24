/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "datasets")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSets {

    @XmlElement
    private InferenceSet inferenceSet = new InferenceSet();
    @XmlElement
    private TrainingsSet trainingsSet = new TrainingsSet();

    public DataSets() {
    }

    public InferenceSet getInferenceSet() {
        return inferenceSet;
    }

    public TrainingsSet getTrainingsSet() {
        return trainingsSet;
    }
}
