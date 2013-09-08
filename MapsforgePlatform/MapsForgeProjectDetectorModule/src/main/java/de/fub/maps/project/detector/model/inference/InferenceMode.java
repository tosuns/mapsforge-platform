/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model.inference;

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

    @XmlEnumValue("crossvalidationMode")
    CROSS_VALIDATION_MODE("Crossvalidation"),
    @XmlEnumValue("trainingsMode")
    TRAININGS_MODE("Training"),
    @XmlEnumValue("inferenceMode")
    INFERENCE_MODE("Inference"),
    ALL_MODE("All Mode");
    private String displayName;

    private InferenceMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public static InferenceMode fromValue(String name) {
        InferenceMode mode = null;
        if ("crossvalidationMode".equals(name) || "Crossvalidation".equals(name)) {
            mode = InferenceMode.CROSS_VALIDATION_MODE;
        } else if ("trainingsMode".equals(name) || "Training".equals(name)) {
            mode = InferenceMode.TRAININGS_MODE;
        } else if ("inferenceMode".equals(name) || "Inference".equals(name)) {
            mode = InferenceMode.INFERENCE_MODE;
        } else {
            throw new IllegalArgumentException(name);
        }
        return mode;
    }
}
