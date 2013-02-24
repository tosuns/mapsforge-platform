/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

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
@XmlType(name = "features")
@XmlAccessorType(XmlAccessType.FIELD)
public class Features {

    @XmlElement(name = "feature")
    private List<ProcessUnit> featureList = new ArrayList<ProcessUnit>();

    public Features() {
    }

    public List<ProcessUnit> getFeatureList() {
        return featureList;
    }
}
