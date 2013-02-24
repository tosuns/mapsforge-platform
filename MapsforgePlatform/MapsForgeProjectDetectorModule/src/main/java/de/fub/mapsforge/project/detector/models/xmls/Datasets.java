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
@XmlType(name = "datasets", propOrder = {"trainingsset", "inferenceset"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSets {

    @XmlElement(name = "trainingsset")
    private TrainingsSet trainingsset = new TrainingsSet();
    @XmlElement(name = "inferenceset")
    private InferenceSet inferenceset = new InferenceSet();

    public DataSets() {
    }

    public InferenceSet getInferenceSet() {
        return inferenceset;
    }

    public TrainingsSet getTrainingsSet() {
        return trainingsset;
    }
}
