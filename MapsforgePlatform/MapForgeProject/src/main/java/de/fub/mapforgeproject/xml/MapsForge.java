/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "mapsforge")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapsForge {

    public static final String PROP_NAME_NAME = "maps.forge.name";
    public static final String PROP_NAME_VERSION = "maps.forge.version";
    private static final String DEFAULT_VERSION = "1.0";
    @XmlAttribute(required = true, name = "version")
    private String version;
    @XmlAttribute(required = false, name = "name")
    private String name;
    @XmlElement(name = "folders", required = true)
    private ProjectFolders folders;
    @XmlElement(name = "properties", required = true)
    private Properties properties;
    protected final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public MapsForge() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Object oldValue = this.name;
        this.name = name;
        pcs.firePropertyChange(PROP_NAME_NAME, oldValue, this.name);
    }

    public String getVersion() {
        return version == null ? DEFAULT_VERSION : version;
    }

    public void setVersion(String version) {
        Object oldValue = this.version;
        this.version = version;
        pcs.firePropertyChange(PROP_NAME_VERSION, oldValue, this.version);
    }

    public ProjectFolders getProjectFolders() {
        return folders;
    }

    public Properties getProperties() {
        return properties;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "MapsForge{" + "version=" + version + ", name=" + name + ", properties=" + properties + '}';
    }
}
