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

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
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
@XmlRootElement(name = "process", namespace = "http://inf.fu-berlin.de/mapsforge/aggregation/schema")
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
