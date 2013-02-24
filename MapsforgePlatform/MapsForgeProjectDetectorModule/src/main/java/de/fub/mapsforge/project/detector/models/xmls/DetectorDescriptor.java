/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

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
@XmlType(propOrder = {"name", "description", "inferencemodel", "datasets", "preprocessors", "postprocessors"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DetectorDescriptor {

    @XmlAttribute(name = "name", required = true)
    private String name = null;
    @XmlAttribute(name = "description")
    private String description = null;
    @XmlElement(name = "inferencemodel", required = true)
    private InferenceModelDescriptor inferencemodel;
    @XmlElement(name = "datasets")
    private DataSets datasets = new DataSets();
    @XmlElement(name = "preprocessors")
    private PreProcessors preprocessors = new PreProcessors();
    @XmlElement(name = "postprocessors")
    private PostProcessors postprocessors = new PostProcessors();

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

    @Override
    public String toString() {
        return "DetectorDescriptor{" + "name=" + name + ", description=" + description + ", inferenceModel=" + inferencemodel + '}';
    }
}
