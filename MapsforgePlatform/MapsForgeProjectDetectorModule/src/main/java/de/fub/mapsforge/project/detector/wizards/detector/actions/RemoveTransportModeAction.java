/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.wizards.detector.actions;

import de.fub.mapsforge.project.detector.wizards.detector.TrainingSetSelectionVisualPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.wizards.detector.actions.RemoveTransportModeAction")
@ActionRegistration(
        displayName = "#CTL_RemoveTransportModeAction")
@ActionReference(path = TrainingSetSelectionVisualPanel.TransportModeFilterNode.ACTION_PATH, position = 5000)
@Messages("CTL_RemoveTransportModeAction=Remove")
public final class RemoveTransportModeAction implements ActionListener {

    private final TrainingSetSelectionVisualPanel.TransportModeFilterNode context;

    public RemoveTransportModeAction(TrainingSetSelectionVisualPanel.TransportModeFilterNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation("This Transport mode will be removed. Do you want to proceed ?");
        Object notify = DialogDisplayer.getDefault().notify(nd);
        if (NotifyDescriptor.Confirmation.OK_OPTION.equals(notify)) {
            context.getTransportNodeNameList().remove(context.getTransportMode().getName());
        }
    }
}
