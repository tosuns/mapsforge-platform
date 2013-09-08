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

import de.fub.utilsmodule.beans.PropertyDescriptor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Property implements PropertyDescriptor {

    private String name;
    private String value;
    private String javaType;
    private String description;
    private String id;

    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute(name = "id", required = true)
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "name", required = true)
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "value", required = true)
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @XmlAttribute(name = "type", required = true)
    @Override
    public String getJavaType() {
        return javaType;
    }

    @Override
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    @Override
    public String toString() {
        return "Property{" + "name=" + name + ", value=" + value + ", javaType=" + javaType + ", description=" + description + ", id=" + id + '}';
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
