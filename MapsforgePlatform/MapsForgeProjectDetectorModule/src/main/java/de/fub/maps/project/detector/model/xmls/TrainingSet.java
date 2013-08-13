/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.xmls;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "trainingset")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrainingSet {

    @XmlElement(name = "transportmode")
    private List<TransportMode> transportModeList = new ArrayList<TransportMode>();

    public TrainingSet() {
    }

    public List<TransportMode> getTransportModeList() {
        return transportModeList;
    }
}
