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
package de.fub.maps.project.aggregator.xml;

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
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PropertySection {

    private String name;
    private String description;
    private String id;
    private List<PropertySet> propertySet;

    public PropertySection() {
    }

    public PropertySection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @XmlAttribute(name = "id", required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "propertyset")
    public List<PropertySet> getPropertySet() {
        if (propertySet == null) {
            propertySet = new ArrayList<PropertySet>();
        }
        return propertySet;
    }

    public void setPropertySet(List<PropertySet> propertySets) {
        this.propertySet = propertySets;
    }

    @Override
    public String toString() {
        return "PropertySection{" + "name=" + name + ", description=" + description + ", id=" + id + '}';
    }
}
