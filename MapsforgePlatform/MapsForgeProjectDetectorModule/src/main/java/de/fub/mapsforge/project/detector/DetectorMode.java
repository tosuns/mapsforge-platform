/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 *
 * @author Serdar
 */
@XmlEnum
public enum DetectorMode {
    @XmlEnumValue(value = "training")
    TRAINING, @XmlEnumValue(value = "inference")
    INFERENCE, @XmlEnumValue(value = "both")
    BOTH
}
