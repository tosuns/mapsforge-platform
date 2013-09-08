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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "processunit", namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema")
@XmlType(name = "processunit", propOrder = {"javaType", "name", "description", "properties"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessDescriptor {

    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description", required = false)
    private String description;
    @XmlAttribute(name = "javaType", required = true)
    private String javaType;
    @XmlElement(name = "properties", required = false)
    private Properties properties = new Properties();

    public ProcessDescriptor() {
    }

    public ProcessDescriptor(String name, String description, String javaType) {
        this.name = name;
        this.description = description;
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

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "ProcessUnit{" + "name=" + name + ", description=" + description + ", javaType=" + javaType + '}';
    }
}
