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
@XmlType(name = "postprocessors")
@XmlAccessorType(XmlAccessType.FIELD)
public class PostProcessors {

    @XmlElement(name = "task")
    private List<ProcessUnit> postprocessorList = new ArrayList<ProcessUnit>();

    public PostProcessors() {
    }

    public List<ProcessUnit> getPostprocessorList() {
        return postprocessorList;
    }
}
