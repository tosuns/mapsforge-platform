/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.actions;

import de.fub.mapsforge.project.detector.factories.nodes.inference.InferenceModelNode;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
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
    private static final Logger LOG = Logger.getLogger(InferenceModelSettingsAction.class.getName());

    public InferenceModelSettingsAction(AbstractInferenceModel context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        JComponent settingsView = context.getSettingsView();
        if (settingsView != null) {
            DialogDescriptor dd = new DialogDescriptor(settingsView, Bundle.CTL_InferenceModelSettingsAction(), true, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource().equals(DialogDescriptor.OK_OPTION)) {
                        LOG.info("OK");
                    } else if (e.getSource().equals(DialogDescriptor.CANCEL_OPTION)) {
                        LOG.info("CANCEL");
                    }
                }
            });
            Object notify = DialogDisplayer.getDefault().notify(dd);
            if (DialogDescriptor.OK_OPTION.equals(notify)) {
            }
        }
    }
}
