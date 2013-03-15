/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelInputDataSet;
import de.fub.mapsforge.project.detector.model.inference.ui.EvaluationPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JComponent;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("LBL_Detector_clustering_Title=Clustering")
@ServiceProvider(service = InferenceModelProcessHandler.class)
public class InferenceDataProcessHandler extends InferenceModelProcessHandler {

    private EvaluationPanel evaluationPanel = null;

    public InferenceDataProcessHandler() {
        super(null);
    }

    public InferenceDataProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    @Override
    protected void handle() {
    }

    public HashSet<Gpx> getInferenceDataSet() {
        InferenceModelInputDataSet input = getInferenceModel().getInput();
        HashSet<Gpx> dataset = input.getInferenceSet();
        return dataset;
    }

    @Override
    public JComponent getVisualRepresentation() {
        if (evaluationPanel == null) {
            evaluationPanel = new EvaluationPanel();
            evaluationPanel.getTitle().setText(Bundle.LBL_Detector_clustering_Title());
        }
        return evaluationPanel;
    }
}
