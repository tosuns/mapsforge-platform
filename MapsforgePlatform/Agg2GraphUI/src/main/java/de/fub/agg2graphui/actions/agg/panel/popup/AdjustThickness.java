/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions.agg.panel.popup;

import de.fub.agg2graphui.AggContentPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Build",
id = "de.fub.agg2graphui.actions.agg.panel.popup.AdjustThickness")
@ActionRegistration(
    displayName = "#CTL_AdjustThickness")
@Messages({"CTL_AdjustThickness=Adjust Stroke Thickness",
    "CLT_AdjustThickness_Message=Thickness:"})
public final class AdjustThickness implements ActionListener {

    private final AggContentPanel context;

    public AdjustThickness(AggContentPanel context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(Bundle.CLT_AdjustThickness_Message(), Bundle.CTL_AdjustThickness());
        Object notify = DialogDisplayer.getDefault().notify(nd);
        if (NotifyDescriptor.OK_OPTION == notify) {
            try {
                context.getLayerManager().setThicknessFactor(Float.parseFloat(nd.getInputText()));
            } catch (Exception ex) {
                
                //TODO
            }
        }
    }
}
