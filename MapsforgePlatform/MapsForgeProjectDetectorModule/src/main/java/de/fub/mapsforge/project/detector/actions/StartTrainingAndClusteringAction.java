/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.StartTrainingAndClusteringAction")
@ActionRegistration(
        displayName = "#CTL_StartTrainingAndClusteringAction")
@ActionReference(path = "Projects/org-mapsforge-project/Detector/Start/Actions", position = 2000)
@Messages("CTL_StartTrainingAndClusteringAction=Training & Clustering")
public final class StartTrainingAndClusteringAction implements ActionListener {

    private final Detector detector;

    public StartTrainingAndClusteringAction(Detector detector) {
        this.detector = detector;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
        detector.getInferenceModel().setInferenceMode(InferenceMode.ALL_MODE);
        detector.start();
    }
}
