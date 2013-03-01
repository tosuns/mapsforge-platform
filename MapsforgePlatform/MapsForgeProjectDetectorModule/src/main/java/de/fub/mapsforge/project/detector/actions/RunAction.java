/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.model.Detector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "MapsForge",
        id = "de.fub.mapsforge.project.detector.actions.RunAction")
@ActionRegistration(
        displayName = "#CTL_RunAction")
@ActionReference(id =
        @ActionID(
        category = "MapsForge",
        id = "de.fub.mapsforge.project.detector.actions.RunAction"),
        path = "Loaders/text/detector+xml/Actions", position = 250, separatorAfter = 275)
@Messages("CTL_RunAction=Run")
public final class RunAction implements ActionListener {

    private final Detector context;

    public RunAction(Detector context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.setDetectorState(ProcessState.ERROR);
    }
}
