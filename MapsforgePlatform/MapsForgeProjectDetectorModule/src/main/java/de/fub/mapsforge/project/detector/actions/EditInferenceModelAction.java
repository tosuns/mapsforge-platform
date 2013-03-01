/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.model.Detector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.EditInferenceModelAction")
@ActionRegistration(
        displayName = "#CTL_EditInferenceModelAction")
@ActionReference(id =
        @ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.EditInferenceModelAction"),
        path = "Loaders/text/detector+xml/Actions",
        position = 280)
@Messages("CTL_EditInferenceModelAction=Edit Inference Model...")
public final class EditInferenceModelAction implements ActionListener {

    private final Detector context;

    public EditInferenceModelAction(Detector context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }
}
