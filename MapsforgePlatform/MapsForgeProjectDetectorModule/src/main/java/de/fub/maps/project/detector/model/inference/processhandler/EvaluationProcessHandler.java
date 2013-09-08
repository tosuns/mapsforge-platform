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
package de.fub.maps.project.detector.model.inference.processhandler;

import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.maps.project.detector.model.inference.features.FeatureProcess;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 *
 * @author Serdar
 */
public abstract class EvaluationProcessHandler extends InferenceModelProcessHandler {

    public EvaluationProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    protected abstract void updateVisualRepresentation(Evaluation evaluation);

    protected Instance getInstance(String className, TrackSegment dataset) {
        Instance instance = new DenseInstance(getInferenceModel().getAttributes().size());

        for (FeatureProcess feature : getInferenceModel().getFeatureList()) {
            feature.setInput(dataset);
            feature.run();
            String featureName = feature.getName();
            Attribute attribute = getInferenceModel().getAttributeMap().get(featureName);
            Double result = feature.getResult();
            instance.setValue(attribute, result);
        }

        instance.setValue(getInferenceModel().getAttributeMap().get(AbstractInferenceModel.CLASSES_ATTRIBUTE_NAME), className);
        return instance;
    }
}
