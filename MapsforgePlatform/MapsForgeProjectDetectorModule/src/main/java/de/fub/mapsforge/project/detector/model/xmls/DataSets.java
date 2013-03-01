/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

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

    @XmlElement(name = "inferenceset", required = true)
    private InferenceSet inferenceset = new InferenceSet();
    @XmlElement(name = "trainingsset", required = true)
    private TrainingsSet trainingsset = new TrainingsSet();

    public DataSets() {
    }

    public InferenceSet getInferenceSet() {
        return inferenceset;
    }

    public TrainingsSet getTrainingsSet() {
        return trainingsset;
    }
}
