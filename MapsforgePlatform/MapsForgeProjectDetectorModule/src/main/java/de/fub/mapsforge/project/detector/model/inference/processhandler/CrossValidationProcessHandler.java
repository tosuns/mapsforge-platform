/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.inference.ui.EvaluationPanel;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
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
@NbBundle.Messages({
    "LBL_Detector_crossvalidation_Title=Crossvalidation",
    "CLT_CrossvalidationProcessHandle_Name=Crossvalidation ProcessHandler",
    "CLT_CrossvalidationProcessHandle_Description=No description available",
    "CLT_CrossvalidationProcessHandle_Property_CrossVaidation_Folds_Name=Crossvalidation Folds",
    "CLT_CrossvalidationProcessHandle_Property_CrossVaidation_Folds_Description=This property indicates the amoung of folds for the crossvalidation evaluation"
})
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
        Collection<Attribute> attributeList = getInferenceModel().getAttributes();
        Instances trainingSet = new Instances("Classes", new ArrayList<Attribute>(attributeList), 9);
        trainingSet.setClassIndex(0);

        HashMap<String, HashSet<TrackSegment>> dataset = getInferenceModel().getInput().getTrainingsSet();

        for (Entry<String, HashSet<TrackSegment>> entry : dataset.entrySet()) {
            for (TrackSegment trackSegment : entry.getValue()) {
                Instance instance = getInstance(entry.getKey(), trackSegment);
                trainingSet.add(instance);
            }
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
            throw new InferenceModelClassifyException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void updateVisualRepresentation(final Evaluation evaluation) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getEvaluationPanel().updatePanel(evaluation);
            }
        });
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

    @Override
    protected ProcessHandlerDescriptor createDefaultDescriptor() {
        ProcessHandlerDescriptor descriptor = new ProcessHandlerDescriptor();
        descriptor.setJavaType(CrossValidationProcessHandler.class.getName());
        descriptor.setName(Bundle.CLT_CrossvalidationProcessHandle_Name());
        descriptor.setDescription(Bundle.CLT_CrossvalidationProcessHandle_Description());
        descriptor.setInferenceMode(InferenceMode.CROSS_VALIDATION_MODE);

        Property property = new Property();
        property.setId(CROSSVALIDATION_FOLDS_COUNT);
        property.setJavaType(Integer.class.getName());
        property.setValue("10");
        property.setName(Bundle.CLT_CrossvalidationProcessHandle_Property_CrossVaidation_Folds_Name());
        property.setDescription(Bundle.CLT_CrossvalidationProcessHandle_Property_CrossVaidation_Folds_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
