/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.ui.CrossValidationPanel;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
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

    private CrossValidationPanel evaluationPanel = null;
    private static final String CROSSVALIDATION_FOLDS_COUNT = "trainings.set.crossvalidation.fold";
    private static final Logger LOG = Logger.getLogger(CrossValidationProcessHandler.class.getName());

    public CrossValidationProcessHandler() {
        super(null);
    }

    public CrossValidationProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    @Override
    protected void handle() {
        ArrayList<Attribute> attributeList = getInferenceModel().getAttributeList();
        Instances trainingSet = new Instances("Classes", attributeList, 0);
        trainingSet.setClassIndex(0);

        HashMap<String, HashSet<TrackSegment>> dataset = getInferenceModel().getInput().getTrainingsSet();

        for (Entry<String, HashSet<TrackSegment>> entry : dataset.entrySet()) {
            for (TrackSegment trackSegment : entry.getValue()) {
                Instance instance = getInstance(entry.getKey(), trackSegment);
                trainingSet.add(instance);

            }
        }

        assert trainingSet.numInstances() > 0 : "Training set is empty and has no instances"; //NO18N

        evaluateAttributes(trainingSet);
    }

    private void evaluateAttributes(Instances trainingSet) {
        try {
            AttributeSelectedClassifier metaClassifier = new AttributeSelectedClassifier();
            Classifier base = getInferenceModel().getClassifier();
            CfsSubsetEval eval = new CfsSubsetEval();
            GreedyStepwise search = new GreedyStepwise();
            search.setSearchBackwards(true);
            search.setGenerateRanking(true);
            metaClassifier.setClassifier(base);
            metaClassifier.setEvaluator(eval);
            metaClassifier.setSearch(search);

            Evaluation metaEvaluation = new Evaluation(trainingSet);
            int crossValidationFoldsCount = getCrossValidationFoldsCount();
            crossValidationFoldsCount = crossValidationFoldsCount > trainingSet.size() ? trainingSet.size() : crossValidationFoldsCount;
            metaEvaluation.crossValidateModel(metaClassifier, trainingSet, crossValidationFoldsCount, new Random(1));
            eval.buildEvaluator(trainingSet);
            search.search(eval, trainingSet);
            updateVisualRepresentation(metaEvaluation, eval, search);
            LOG.info(metaEvaluation.toSummaryString());

        } catch (Exception ex) {
            throw new InferenceModelClassifyException(ex.getMessage(), ex);
        }
    }

    protected void updateVisualRepresentation(final Evaluation evaluation, final CfsSubsetEval eval, final GreedyStepwise search) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getEvaluationPanel().updatePanel(evaluation, eval, search);
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

    private CrossValidationPanel getEvaluationPanel() {
        if (evaluationPanel == null) {
            evaluationPanel = new CrossValidationPanel();
            evaluationPanel.getCrossValidationTitle().setText(Bundle.LBL_Detector_crossvalidation_Title());
        }
        return evaluationPanel;
    }
}
