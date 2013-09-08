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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "properties")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Properties {

    private List<PropertySection> sections;

    public Properties() {
    }

    public Properties(List<PropertySection> sections) {
        this.sections = sections;
    }

    @XmlElement(name = "section")
    public List<PropertySection> getSections() {
        if (sections == null) {
            sections = new ArrayList<PropertySection>();
        }
        return sections;
    }

    public void setSections(List<PropertySection> sections) {
        this.sections = sections;
    }
}
