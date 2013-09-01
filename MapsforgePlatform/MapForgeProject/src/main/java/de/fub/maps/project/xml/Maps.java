/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "mapsforge")
@XmlType(name = "mapsforge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Maps {

    @XmlTransient
    public static final String PROP_NAME_NAME = "maps.forge.name";
    @XmlTransient
    public static final String PROP_NAME_VERSION = "maps.forge.version";
    @XmlTransient
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

    public Maps() {
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
