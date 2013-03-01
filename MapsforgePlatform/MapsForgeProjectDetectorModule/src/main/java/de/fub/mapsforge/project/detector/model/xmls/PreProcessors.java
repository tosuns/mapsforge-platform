/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

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
@XmlType(name = "preprocessors", namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema")
@XmlAccessorType(XmlAccessType.FIELD)
public class PreProcessors {

    @XmlElement(name = "filter")
    private List<ProcessDescriptor> preprocessorList = new ArrayList<ProcessDescriptor>();

    public PreProcessors() {
    }

    public List<ProcessDescriptor> getPreprocessorList() {
        return preprocessorList;
    }
}
