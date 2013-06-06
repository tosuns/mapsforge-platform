/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.ui.EvaluationPanel;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "LBL_Detector_trainingsPanel_Title=Training",
    "CLT_TrainingsDataProcessHandler_Name=Trainings ProcessHandler",
    "CLT_TrainingsDataProcessHandler_Description=No description available",
    "CLT_TrainingsDataProcessHandler_Property_Ratio_Name=Training set size",
    "CLT_TrainingsDataProcessHandler_Property_Ratio_Description=This property indicates the ratio (between 0.01 and 1) the trainings set will be divides for the actual training and for successive the test."
})
@ServiceProvider(service = InferenceModelProcessHandler.class)
public class TrainingsDataProcessHandler extends EvaluationProcessHandler {

    private static final String TRAININGS_SET_RATIO = "trainings.set.ratio";
    private EvaluationPanel evaluationPanel = null;

    public TrainingsDataProcessHandler() {
        super(null);
    }

    public TrainingsDataProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    @Override
    protected void handle() {
        final ProgressHandle handle = ProgressHandleFactory.createHandle("Trainings");
        try {
            handle.start();
            Collection<Attribute> attributeCollection = getInferenceModel().getAttributes();
            ArrayList<Attribute> arrayList = new ArrayList<Attribute>(attributeCollection);
            Instances trainingSet = new Instances("Classes", arrayList, 0);
            trainingSet.setClassIndex(0);

            Instances testingSet = new Instances("Classes", arrayList, 0);
            testingSet.setClassIndex(0);

            HashMap<String, HashSet<TrackSegment>> dataset = getInferenceModel().getInput().getTrainingsSet();


            int datasetCount = 0;
            for (HashSet<TrackSegment> list : dataset.values()) {
                for (TrackSegment trackSegment : list) {
                    datasetCount += trackSegment.getWayPointList().size();
                }
            }
            handle.switchToDeterminate(datasetCount);
            int trackCount = 0;
            for (Entry<String, HashSet<TrackSegment>> entry : dataset.entrySet()) {

                int trainingsSetSize = (int) Math.ceil(entry.getValue().size() * getTrainingsSetRatioParameter());
                int index = 0;
                for (TrackSegment trackSegment : entry.getValue()) {
                    Instance instance = getInstance(entry.getKey(), trackSegment);

                    if (index < trainingsSetSize) {
                        trainingSet.add(instance);
                    } else {
                        testingSet.add(instance);
                    }
                    handle.progress(trackCount++);
                    index++;
                }
            }

            assert trainingSet.numInstances() > 0 : "Training set is empty and has no instances"; //NO18N
            assert testingSet.numInstances() > 0 : "Testing set is empty and has no instances"; //NO18N
            handle.switchToIndeterminate();
            evaluate(trainingSet, testingSet);
        } finally {
            handle.finish();
        }
    }

    private void evaluate(Instances trainingSet, Instances testingSet) {
        Classifier classifier = getInferenceModel().getClassifier();
        try {
            classifier.buildClassifier(trainingSet);
            Evaluation evaluation = new Evaluation(testingSet);
            evaluation.evaluateModel(classifier, testingSet);
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

    private double getTrainingsSetRatioParameter() {
        for (Property property : getDescriptor().getProperties().getPropertyList()) {
            if (TRAININGS_SET_RATIO.equals(property.getId())) {
                return Double.parseDouble(property.getValue());
            }
        }
        return .75;
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

    @Override
    protected ProcessHandlerDescriptor createDefaultDescriptor() {
        ProcessHandlerDescriptor descriptor = new ProcessHandlerDescriptor();
        descriptor.setJavaType(TrainingsDataProcessHandler.class.getName());
        descriptor.setName(Bundle.CLT_TrainingsDataProcessHandler_Name());
        descriptor.setDescription(Bundle.CLT_TrainingsDataProcessHandler_Description());

        Property property = new Property();
        property.setId(TRAININGS_SET_RATIO);
        property.setJavaType(Double.class.getName());
        property.setValue("0.75");
        property.setName(Bundle.CLT_TrainingsDataProcessHandler_Property_Ratio_Name());
        property.setDescription(Bundle.CLT_TrainingsDataProcessHandler_Property_Ratio_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
    }
}
