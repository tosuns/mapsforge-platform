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
@XmlType(name = "postprocessors")
@XmlAccessorType(XmlAccessType.FIELD)
public class PostProcessors {

    @XmlElement(name = "task")
    private List<ProcessDescriptor> postprocessorList = new ArrayList<ProcessDescriptor>();

    public PostProcessors() {
    }

    public List<ProcessDescriptor> getPostprocessorList() {
        return postprocessorList;
    }
}
