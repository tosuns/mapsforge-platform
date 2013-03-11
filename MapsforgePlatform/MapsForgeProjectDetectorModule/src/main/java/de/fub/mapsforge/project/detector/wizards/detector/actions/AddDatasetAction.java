/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.wizards.detector.actions;

import de.fub.mapsforge.project.detector.wizards.detector.TrainingSetSelectionVisualPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.wizards.detector.actions.AddDatasetAction")
@ActionRegistration(
        displayName = "#CTL_AddDatasetAction")
@ActionReference(path = TrainingSetSelectionVisualPanel.TransportModeFilterNode.ACTION_PATH, position = 0)
@Messages("CTL_AddDatasetAction=Add Dataset ...")
public final class AddDatasetAction implements ActionListener {

    private final TrainingSetSelectionVisualPanel.TransportModeFilterNode context;

    public AddDatasetAction(TrainingSetSelectionVisualPanel.TransportModeFilterNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }
}
