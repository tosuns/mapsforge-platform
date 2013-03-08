/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "inferenceMode")
@XmlEnum(String.class)
public enum InferenceMode {

    @XmlEnumValue("trainingsMode")
    TRAININGS_MODE,
    @XmlEnumValue("crossvalidationMode")
    CROSS_VALIDATION_MODE,
    @XmlEnumValue("inferenceMode")
    INFERENCE_MODE,
    ALL_MODE;
}
