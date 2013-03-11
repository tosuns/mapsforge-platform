/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.wizards.detector.actions;

import de.fub.mapsforge.project.detector.wizards.detector.TrainingSetSelectionVisualPanel.RootNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.project.detector.wizards.detector.actions.AddTransportNodeAction")
@ActionRegistration(
        displayName = "#CTL_AddTransportNodeAction")
@ActionReference(path = RootNode.ACTION_PATH)
@Messages({
    "CTL_AddTransportNodeAction=Add Transportmode ...",
    "CLT_Add_New_Transport_Mode_text=Add new transport mode name",
    "CLT_Add_New_Transport_Mode_Title=Transport mode"
})
public final class AddTransportNodeAction implements ActionListener, PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(AddTransportNodeAction.class.getName());
    private final RootNode context;

    public AddTransportNodeAction(RootNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(Bundle.CLT_Add_New_Transport_Mode_text(), Bundle.CLT_Add_New_Transport_Mode_Title());
        nd.addPropertyChangeListener(WeakListeners.propertyChange(AddTransportNodeAction.this, nd));
        Object result = DialogDisplayer.getDefault().notify(nd);
        if (NotifyDescriptor.InputLine.OK_OPTION.equals(result)) {
            context.getTransportModeNameList().add(nd.getInputText());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt != null) {
            LOG.info(evt.getPropertyName());
        }
    }
}
