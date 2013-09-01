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
