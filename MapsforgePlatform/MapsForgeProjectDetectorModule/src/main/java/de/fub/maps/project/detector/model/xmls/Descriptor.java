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
package de.fub.maps.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "descriptor", propOrder = {"javaType", "name", "description"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Descriptor {

    @XmlAttribute(name = "javaType", required = true)
    private String javaType;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlElement(name = "description")
    private String description;

    public Descriptor() {
    }

    public Descriptor(String javaType, String name, String description) {
        this.javaType = javaType;
        this.name = name;
        this.description = description;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
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

    @Override
    public String toString() {
        return "Descriptor{" + "javaType=" + javaType + ", name=" + name + ", description=" + description + '}';
    }
}
