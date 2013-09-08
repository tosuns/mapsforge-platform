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
@XmlRootElement(name = "detector", namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema")
@XmlType(propOrder = {"name", "description", "inferencemodel", "datasets", "preprocessors", "postprocessors", "propertysection", "profiles"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DetectorDescriptor {

    @XmlAttribute(name = "name", required = true)
    private String name = null;
    @XmlAttribute(name = "description")
    private String description = null;
    @XmlElement(name = "inferencemodel", required = true)
    private InferenceModelDescriptor inferencemodel;
    @XmlElement(name = "datasets", required = true)
    private DataSets datasets = new DataSets();
    @XmlElement(name = "preprocessors", required = true)
    private PreProcessors preprocessors = new PreProcessors();
    @XmlElement(name = "postprocessors", required = true)
    private PostProcessors postprocessors = new PostProcessors();
    @XmlElement(name = "propertysection")
    private PropertySection propertysection = new PropertySection();
    @XmlElement(name = "profiles")
    private Profiles profiles = new Profiles();

    public DetectorDescriptor() {
    }

    public DetectorDescriptor(String name, String description, InferenceModelDescriptor inferenceModel) {
        this.name = name;
        this.description = description;
        this.inferencemodel = inferenceModel;
    }

    /**
     * Returns the name of this detector descriptor.
     *
     * @return always a String
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a descripton test, which describes this detector descriptor.
     *
     * @return a String, if there is one, otherwise null.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InferenceModelDescriptor getInferenceModel() {
        return inferencemodel;
    }

    public void setInferenceModel(InferenceModelDescriptor inferenceModel) {
        this.inferencemodel = inferenceModel;
    }

    public DataSets getDatasets() {
        return datasets;
    }

    public PreProcessors getPreprocessors() {
        return preprocessors;
    }

    public PostProcessors getPostprocessors() {
        return postprocessors;
    }

    public PropertySection getPropertySection() {
        return propertysection;
    }

    public Profiles getProfiles() {
        return profiles;
    }

    @Override
    public String toString() {
        return "DetectorDescriptor{" + "name=" + name + ", description=" + description + ", inferenceModel=" + inferencemodel + '}';
    }
}
