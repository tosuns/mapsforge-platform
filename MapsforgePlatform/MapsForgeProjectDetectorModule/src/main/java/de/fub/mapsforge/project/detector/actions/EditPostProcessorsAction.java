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
        id = "de.fub.mapsforge.project.detector.actions.EditPostProcessorsAction")
@ActionRegistration(
        displayName = "#CTL_EditPostProcessorsAction")
@ActionReference(id =
        @ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.EditPostProcessorsAction"),
        path = "Loaders/text/detector+xml/Actions",
        position = 290,
        separatorAfter = 295)
@Messages("CTL_EditPostProcessorsAction=Edit Postprocessors...")
public final class EditPostProcessorsAction implements ActionListener {

    private final Detector context;

    public EditPostProcessorsAction(Detector context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }
}
