/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.xmls;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "section")
@XmlAccessorType(XmlAccessType.FIELD)
public class Section {

    @XmlElement(name = "property")
    private List<Property> propertyList = new ArrayList<Property>();
    @XmlAttribute(name = "id", required = true)
    private String id;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlElement(name = "description")
    private String description;

    public Section() {
    }

    public Section(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
