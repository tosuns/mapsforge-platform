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
@XmlType(name = "inferenceModelProcessHandlers")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessHandlers {

    @XmlElement(name = "processHandler")
    private List<ProcessHandlerDescriptor> processHandlerList = new ArrayList<ProcessHandlerDescriptor>();

    public ProcessHandlers() {
    }

    public List<ProcessHandlerDescriptor> getProcessHandlerList() {
        return processHandlerList;
    }
}
