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
import org.openide.cookies.OpenCookie;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.StartTrainingAction")
@ActionRegistration(
        displayName = "#CTL_StartTrainingAction")
@ActionReference(path = "Projects/org-mapsforge-project/Detector/Start/Actions", position = 0)
@Messages("CTL_StartTrainingAction=Training")
public final class StartTrainingAction implements ActionListener {

    private final Detector detector;

    public StartTrainingAction(Detector detector) {
        this.detector = detector;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        OpenCookie openCookie = detector.getDataObject().getLookup().lookup(OpenCookie.class);
        if (openCookie != null) {
            openCookie.open();
        }
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                detector.getInferenceModel().setInferenceMode(InferenceMode.TRAININGS_MODE);
                detector.start();
            }
        });
    }
}
