/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.ui.EvaluationPanel;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.JComponent;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("LBL_Detector_crossvalidation_Title=Crossvalidation")
@ServiceProvider(service = InferenceModelProcessHandler.class)
public class CrossValidationProcessHandler extends EvaluationProcessHandler {

    private EvaluationPanel evaluationPanel = null;
    private static final String CROSSVALIDATION_FOLDS_COUNT = "trainings.set.crossvalidation.fold";

    public CrossValidationProcessHandler() {
        super(null);
    }

    public CrossValidationProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    @Override
    protected void handle() {
        ArrayList<Attribute> attributeList = getInferenceModel().getAttributeList();
        Instances trainingSet = new Instances("Classes", attributeList, 9);
        trainingSet.setClassIndex(0);

        HashMap<String, HashSet<Gpx>> dataset = getInferenceModel().getInput().getTrainingsSet();

        for (Entry<String, HashSet<Gpx>> entry : dataset.entrySet()) {
            Instance instance = getInstance(entry.getKey(), new ArrayList<Gpx>(entry.getValue()));
            trainingSet.add(instance);
        }

        assert trainingSet.numInstances() > 0 : "Training set is empty and has no instances"; //NO18N

        evaluate(trainingSet);
    }

    private void evaluate(Instances trainingSet) {
        try {
            Evaluation evaluation = new Evaluation(trainingSet);
            int crossValidationFoldsCount = getCrossValidationFoldsCount();
            crossValidationFoldsCount = crossValidationFoldsCount > trainingSet.size() ? trainingSet.size() : crossValidationFoldsCount;
            evaluation.crossValidateModel(getInferenceModel().getClassifier(), trainingSet, crossValidationFoldsCount, new Random(1));
            updateVisualRepresentation(evaluation);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected void updateVisualRepresentation(Evaluation evaluation) {
        getEvaluationPanel().updatePanel(evaluation);
    }

    private int getCrossValidationFoldsCount() {
        for (Property property : getDescriptor().getProperties().getPropertyList()) {
            if (CROSSVALIDATION_FOLDS_COUNT.equals(property.getId())) {
                return Integer.parseInt(property.getValue());
            }
        }
        return 10;
    }

    @Override
    public JComponent getVisualRepresentation() {
        return getEvaluationPanel();
    }

    private EvaluationPanel getEvaluationPanel() {
        if (evaluationPanel == null) {
            evaluationPanel = new EvaluationPanel();
            evaluationPanel.getTitle().setText(Bundle.LBL_Detector_crossvalidation_Title());
        }
        return evaluationPanel;
    }
}
