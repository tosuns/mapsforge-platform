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
@XmlType(name = "properties")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Properties {

    private List<PropertySection> sections;

    public Properties() {
    }

    public Properties(List<PropertySection> sections) {
        this.sections = sections;
    }

    @XmlElement(name = "section")
    public List<PropertySection> getSections() {
        if (sections == null) {
            sections = new ArrayList<PropertySection>();
        }
        return sections;
    }

    public void setSections(List<PropertySection> sections) {
        this.sections = sections;
    }
}
