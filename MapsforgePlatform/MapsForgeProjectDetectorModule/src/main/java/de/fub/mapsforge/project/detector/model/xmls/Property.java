/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property extends Descriptor {

    @XmlAttribute(name = "value", required = true)
    private String value;
    @XmlAttribute(name = "id", required = true)
    private String id;

    public Property() {
    }

    public Property(String id, String javaType, String name, String description, String value) {
        super(javaType, name, description);
        assert id != null;
        this.value = value;
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Property{" + "javaType=" + getJavaType() + ", name=" + getName() + ", description=" + getDescription() + ", value=" + value + '}';
    }
}
