/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "process")
@XmlType(name = "process")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessDescriptor {

    private String javaType;
    private String displayName;
    private String description;
    private Properties properties = new Properties();

    public ProcessDescriptor() {
    }

    public ProcessDescriptor(String javatype, String displayName, String description) {
        this.javaType = javatype;
        this.displayName = displayName;
        this.description = description;
    }

    public ProcessDescriptor(AbstractAggregationProcess<?, ?> process) {
        this(process.getClass().getName(), process.getName(), process.getDescription());
    }

    @XmlAttribute(name = "class")
    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javatype) {
        this.javaType = javatype;
    }

    @XmlAttribute(name = "name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @XmlAttribute(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "properties", required = true)
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Process{" + "javatype=" + javaType + ", displayName=" + displayName + ", description=" + description + '}';
    }
}
