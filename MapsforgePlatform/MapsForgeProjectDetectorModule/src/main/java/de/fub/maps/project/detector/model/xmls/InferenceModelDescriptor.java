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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlRootElement(name = "inferencemodel", namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema")
@XmlType(name = "inferencemodel")
@XmlAccessorType(XmlAccessType.FIELD)
public class InferenceModelDescriptor extends Descriptor {

    @XmlElement(name = "features")
    private Features features = new Features();
    @XmlElement(name = "inferenceModelProcessHandlers")
    private ProcessHandlers inferenceModelProcessHandlers = new ProcessHandlers();
    @XmlElement(name = "propertysection")
    private PropertySection propertysection = new PropertySection();

    public InferenceModelDescriptor() {
    }

    public InferenceModelDescriptor(String name, String description, String javaType) {
        super(javaType, name, description);
    }

    public Features getFeatures() {
        return features;
    }

    public ProcessHandlers getInferenceModelProcessHandlers() {
        return inferenceModelProcessHandlers;
    }

    public PropertySection getPropertySection() {
        return propertysection;
    }

    @Override
    public String toString() {
        return "InferenceModelDescriptor{" + "javaType=" + getJavaType() + ", name=" + getName() + ", description=" + getDescription() + '}';
    }
}
