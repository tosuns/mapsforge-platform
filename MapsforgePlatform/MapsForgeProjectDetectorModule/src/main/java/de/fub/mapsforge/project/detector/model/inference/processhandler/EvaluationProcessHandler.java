/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
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
