/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.actions;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.maps.project.detector.utils.DetectorUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.actions.StartTrainingAction")
@ActionRegistration(
        displayName = "#CTL_StartTrainingAction")
@ActionReferences({
    @ActionReference(path = "Projects/org-maps-project/Detector/Start/Actions", position = 0),
    @ActionReference(
            path = "Projects/org-maps-project/Detector/Toolbar/Start/Popup/Actions", position = 100)
})
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
        DetectorUtils.getDefaultRequestProcessor().post(new Runnable() {
            @Override
            public void run() {
                detector.getInferenceModel().setInferenceMode(InferenceMode.TRAININGS_MODE);
                detector.start();
            }
        });
    }
}
