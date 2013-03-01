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
        id = "de.fub.mapsforge.project.aggregator.actions.EditPreprocessorsAction")
@ActionRegistration(
        displayName = "#CTL_EditPreprocessorsAction")
@ActionReference(id =
        @ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.aggregator.actions.EditPreprocessorsAction"),
        path = "Loaders/text/detector+xml/Actions",
        position = 285)
@Messages("CTL_EditPreprocessorsAction=Edit Preprocessors...")
public final class EditPreprocessorsAction implements ActionListener {

    private final Detector context;

    public EditPreprocessorsAction(Detector context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }
}
