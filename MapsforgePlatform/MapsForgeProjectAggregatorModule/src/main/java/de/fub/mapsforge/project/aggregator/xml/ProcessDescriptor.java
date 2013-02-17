/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "process")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessDescriptor implements ChangeListener {

    @XmlTransient
    public static final String PROP_NAME_DISPLAY_NAME = "displayName";
    @XmlTransient
    public static final String PROP_NAME_DESCRIPTION = "description";
    @XmlTransient
    public static final String PROP_NAME_JAVATYPE = "javatype";
    @XmlTransient
    public static final String PROP_NAME_PROPERTIES = "properties";
    private String javatype;
    private String displayName;
    private String description;
    private Properties properties = new Properties();
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ProcessDescriptor() {
    }

    public ProcessDescriptor(String javatype, String displayName, String description) {
        this.javatype = javatype;
        this.displayName = displayName;
        this.description = description;
    }

    public ProcessDescriptor(AbstractAggregationProcess<?, ?> process) {
        this(process.getClass().getName(), process.getName(), process.getDescription());
    }

    @XmlAttribute(name = "class")
    public String getJavatype() {
        return javatype;
    }

    public void setJavatype(String javatype) {
        Object oldValue = this.javatype;
        this.javatype = javatype;
        pcs.firePropertyChange(PROP_NAME_JAVATYPE, oldValue, this.javatype);
    }

    @XmlAttribute(name = "name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        Object oldValue = this.displayName;
        this.displayName = displayName;
        pcs.firePropertyChange(PROP_NAME_DISPLAY_NAME, oldValue, this.displayName);
    }

    @XmlAttribute(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        Object oldValue = this.displayName;
        this.description = description;
        pcs.firePropertyChange(PROP_NAME_DESCRIPTION, oldValue, this.description);
    }

    @XmlElement(name = "properties", required = true)
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        Object oldValue = this.properties;
        this.properties = properties;
        if (properties != null) {
            properties.addChangeListener(ProcessDescriptor.this);
        }
        pcs.firePropertyChange(PROP_NAME_PROPERTIES, oldValue, this.properties);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Process{" + "javatype=" + javatype + ", displayName=" + displayName + ", description=" + description + '}';
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        pcs.firePropertyChange(PROP_NAME_PROPERTIES, null, getProperties());
    }
}
