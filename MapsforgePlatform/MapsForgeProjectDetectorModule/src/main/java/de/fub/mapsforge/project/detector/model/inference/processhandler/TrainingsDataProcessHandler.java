/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.model.inference.ui.EvaluationPanel;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JComponent;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("LBL_Detector_trainingsPanel_Title=Training")
@ServiceProvider(service = InferenceModelProcessHandler.class)
public class TrainingsDataProcessHandler extends InferenceModelProcessHandler {

    private static final String TRAININGS_SET_RATIO = "trainings.set.ratio";
    private EvaluationPanel evaluationPanel = null;
    private ProcessHandlerDescriptor descriptor = null;

    public TrainingsDataProcessHandler() {
        super(null);
    }

    public TrainingsDataProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    @Override
    protected void handle() {
        ArrayList<Attribute> attributeList = getInferenceModel().getAttributeList();
        Instances trainingSet = new Instances("Classes", attributeList, 0);
        trainingSet.setClassIndex(0);

        Instances testingSet = new Instances("Classes", attributeList, 0);
        testingSet.setClassIndex(0);

        HashMap<String, HashSet<GPSTrack>> dataset = getInferenceModel().getInput().getTrainingsSet();

        for (Entry<String, HashSet<GPSTrack>> entry : dataset.entrySet()) {

            int trainingsSetSize = (int) Math.ceil(entry.getValue().size() * getTrainingsSetRatioParameter());

            for (int index = 0; index < trainingsSetSize; index++) {
                Instance instance = getInstance(entry.getKey(), new ArrayList<GPSTrack>(entry.getValue()));

                if (index < trainingsSetSize) {
                    trainingSet.add(instance);
                } else {
                    testingSet.add(instance);
                }

            }

        }

        assert trainingSet.numInstances() > 0 : "Training set is empty and has no instances"; //NO18N
        assert testingSet.numInstances() > 0 : "Testing set is empty and has no instances"; //NO18N

        evaluate(trainingSet, testingSet);

    }

    private void evaluate(Instances trainingSet, Instances testingSet) {
        Classifier classifier = getInferenceModel().getClassifier();
        try {
            classifier.buildClassifier(trainingSet);
            Evaluation evaluation = new Evaluation(testingSet);
            evaluation.evaluateModel(classifier, testingSet);
        } catch (Exception ex) {
            throw new InferenceModelClassifyException(ex.getMessage(), ex);
        }
    }

    private void updateVisualRepresentation(Evaluation evaluation) {
        getEvaluationPanel().updatePanel(evaluation);
    }

    private Instance getInstance(String className, List<GPSTrack> dataset) {
        Instance instance = new DenseInstance(getInferenceModel().getAttributeList().size());

        for (FeatureProcess feature : getInferenceModel().getFeatureList()) {
            feature.setInput(dataset);
            feature.run();
            String featureName = feature.getName();
            Attribute attribute = getInferenceModel().getAttributeMap().get(featureName);
            Double result = feature.getResult();
            instance.setValue(attribute, result);
        }

        return instance;
    }

    private double getTrainingsSetRatioParameter() {
        for (Property property : getDescriptor().getProperties().getPropertyList()) {
            if (TRAININGS_SET_RATIO.equals(property.getId())) {
                return Double.parseDouble(property.getValue());
            }
        }
        return .75;
    }

    private ProcessHandlerDescriptor getDescriptor() {
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

    @Override
    public JComponent getVisualRepresentation() {
        return getEvaluationPanel();
    }

    private EvaluationPanel getEvaluationPanel() {
        if (evaluationPanel == null) {
            evaluationPanel = new EvaluationPanel();
            evaluationPanel.getTitle().setText(Bundle.LBL_Detector_trainingsPanel_Title());
        }
        return evaluationPanel;
    }

    public static class InferenceModelClassifyException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public InferenceModelClassifyException() {
        }

        public InferenceModelClassifyException(String message) {
            super(message);
        }

        public InferenceModelClassifyException(String message, Throwable cause) {
            super(message, cause);
        }

        public InferenceModelClassifyException(Throwable cause) {
            super(cause);
        }

        public InferenceModelClassifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
