/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "folder")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProjectFolder {

    public static final String PROP_NAME_PATH = "project.folder.path";
    public static final String PROP_NAME_NAME = "roject.folder.name";
    private String parh;
    private String name;
    protected final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ProjectFolder() {
    }

    public ProjectFolder(String name, String parh) {
        this.parh = parh;
        this.name = name;
    }

    public ProjectFolder(Map.Entry<String, String> e) {
        this(e.getKey(), e.getValue());
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        Object oldValue = this.name;
        this.name = name;
        pcs.firePropertyChange(PROP_NAME_NAME, oldValue, this.name);
    }

    @XmlAttribute(name = "path", required = true)
    public String getPath() {
        return parh;
    }

    public void setPath(String parh) {
        Object oldValue = this.parh;
        this.parh = parh;
        pcs.firePropertyChange(PROP_NAME_PATH, oldValue, this.parh);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "ProjectFolder{" + "parh=" + parh + ", name=" + name + '}';
    }
}
