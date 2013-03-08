/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.factories.nodes.InferenceModelNode;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.InferenceModelSettingsAction")
@ActionRegistration(
        displayName = "#CTL_InferenceModelSettingsAction")
@ActionReference(id =
        @ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.actions.InferenceModelSettingsAction"),
        path = InferenceModelNode.ACTION_PATH, position = 10000)
@Messages("CTL_InferenceModelSettingsAction=Settings")
public final class InferenceModelSettingsAction implements ActionListener {

    private final AbstractInferenceModel context;

    public InferenceModelSettingsAction(AbstractInferenceModel context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        JComponent settingsView = context.getSettingsView();
        if (settingsView != null) {
            DialogDescriptor dd = new DialogDescriptor(settingsView, Bundle.CTL_InferenceModelSettingsAction());
            Object notify = DialogDisplayer.getDefault().notify(dd);
            if (DialogDescriptor.OK_OPTION.equals(notify)) {
            }
        }
    }
}
