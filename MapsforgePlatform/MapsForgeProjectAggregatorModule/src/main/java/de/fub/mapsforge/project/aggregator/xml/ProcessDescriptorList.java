/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

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
@XmlType(name = "pipeline")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessDescriptorList {

    private List<ProcessDescriptor> list = new ArrayList<ProcessDescriptor>();

    @XmlElement(name = "process")
    public List<ProcessDescriptor> getList() {
        return list;
    }

    public void setList(List<ProcessDescriptor> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Processes{" + "List=" + list + '}';
    }
}
