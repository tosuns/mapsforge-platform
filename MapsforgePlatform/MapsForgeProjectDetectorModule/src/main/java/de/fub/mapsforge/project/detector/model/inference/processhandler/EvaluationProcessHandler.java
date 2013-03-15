/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import java.util.List;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 *
 * @author Serdar
 */
public abstract class EvaluationProcessHandler extends InferenceModelProcessHandler {

    private ProcessHandlerDescriptor descriptor = null;

    public EvaluationProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    protected abstract void updateVisualRepresentation(Evaluation evaluation);

    protected Instance getInstance(String className, List<Gpx> dataset) {
        Instance instance = new DenseInstance(getInferenceModel().getAttributeList().size());

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

    protected ProcessHandlerDescriptor getDescriptor() {
        if (descriptor == null) {
            AbstractInferenceModel inferenceModel = getInferenceModel();
            for (ProcessHandlerDescriptor desc : inferenceModel.getInferenceModelDescriptor().getInferenceModelProcessHandlers().getProcessHandlerList()) {
                if (desc.getJavaType().equals(getClass().getName())) {
                    descriptor = desc;
                    break;
                }
            }
        }
        return descriptor;
    }
}
