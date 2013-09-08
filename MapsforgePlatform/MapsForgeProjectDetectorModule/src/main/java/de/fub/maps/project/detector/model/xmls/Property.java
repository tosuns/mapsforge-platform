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

import de.fub.utilsmodule.beans.PropertyDescriptor;
import java.text.MessageFormat;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property extends Descriptor implements PropertyDescriptor {

    @XmlAttribute(name = "value", required = false)
    private String value;
    @XmlAttribute(name = "id", required = true)
    private String id;

    public Property() {
    }

    public Property(String id, String javaType, String name, String description, String value) {
        super(javaType, name, description);
        assert id != null;
        this.value = value;
        this.id = id;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Property{javaType={0}, name={1}, description={2}, value={3}{4}", getJavaType(), getName(), getDescription(), value, '}');
    }
}
