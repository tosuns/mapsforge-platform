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
@XmlType(name = "propertysection")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertySection {

    @XmlElement(name = "section")
    private List<Section> sectionList = new ArrayList<Section>();

    public PropertySection() {
    }

    public List<Section> getSectionList() {
        return sectionList;
    }
}
