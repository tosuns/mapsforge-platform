/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.xmls;

import de.fub.utilsmodule.beans.PropertyDescriptor;
import java.text.MessageFormat;
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
public class Property extends Descriptor implements PropertyDescriptor {

    @XmlAttribute(name = "value", required = false)
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

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Property{javaType={0}, name={1}, description={2}, value={3}{4}", getJavaType(), getName(), getDescription(), value, '}');
    }
}
