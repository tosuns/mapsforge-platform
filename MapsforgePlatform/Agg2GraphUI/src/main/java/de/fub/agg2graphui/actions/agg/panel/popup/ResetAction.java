/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions.agg.panel.popup;

import de.fub.agg2graphui.AggContentPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "AggrefationBuilder",
id = "de.fub.agg2graphui.actions.agg.panel.popup.ResetAction")
@ActionRegistration(
    displayName = "#CTL_ResetAion")
@Messages("CTL_ResetAion=Reset")
public final class ResetAction implements ActionListener {

    private final AggContentPanel context;

    public ResetAction(AggContentPanel context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }
}
