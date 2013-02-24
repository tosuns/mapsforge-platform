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
@XmlType(name = "preprocessors")
@XmlAccessorType(XmlAccessType.FIELD)
public class PreProcessors {

    @XmlElement(name = "filter")
    private List<ProcessUnit> preprocessorList = new ArrayList<ProcessUnit>();

    public PreProcessors() {
    }

    public List<ProcessUnit> getPreprocessorList() {
        return preprocessorList;
    }
}
