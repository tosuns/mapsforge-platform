/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlers;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import java.io.IOException;
import java.util.List;
import org.openide.util.Exceptions;
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

    protected Instance getInstance(String className, TrackSegment dataset) {
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

    @Override
    public ProcessHandlerDescriptor getDescriptor() {
        if (descriptor == null) {
            AbstractInferenceModel inferenceModel = getInferenceModel();
            if (inferenceModel != null) {
                InferenceModelDescriptor inferenceModelDescriptor = inferenceModel.getInferenceModelDescriptor();
                ProcessHandlers inferenceModelProcessHandlers = inferenceModelDescriptor.getInferenceModelProcessHandlers();
                List<ProcessHandlerDescriptor> processHandlerList = inferenceModelProcessHandlers.getProcessHandlerList();
                for (ProcessHandlerDescriptor desc : processHandlerList) {
                    if (desc.getJavaType().equals(getClass().getName())) {
                        descriptor = desc;
                        break;
                    }
                }
            } else {
                try {
                    descriptor = DetectorUtils.getXmlDescriptor(ProcessHandlerDescriptor.class, getClass());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return descriptor;
    }
}
