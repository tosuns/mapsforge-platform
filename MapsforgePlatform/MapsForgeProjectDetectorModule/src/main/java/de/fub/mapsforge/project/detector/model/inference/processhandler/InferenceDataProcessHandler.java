/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.ui.EvaluationPanel;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
