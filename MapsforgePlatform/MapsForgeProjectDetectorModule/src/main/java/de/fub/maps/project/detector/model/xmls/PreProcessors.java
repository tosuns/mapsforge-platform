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
@XmlType(name = "preprocessors", namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema")
@XmlAccessorType(XmlAccessType.FIELD)
public class PreProcessors {

    @XmlElement(name = "filter")
    private List<ProcessDescriptor> preprocessorList = new ArrayList<ProcessDescriptor>();

    public PreProcessors() {
    }

    public List<ProcessDescriptor> getPreprocessorList() {
        return preprocessorList;
    }
}
