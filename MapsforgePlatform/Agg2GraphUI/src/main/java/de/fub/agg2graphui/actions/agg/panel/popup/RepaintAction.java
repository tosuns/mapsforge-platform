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
    category = "Build",
id = "de.fub.agg2graphui.actions.agg.panel.popup.RepaintAction")
@ActionRegistration(
    displayName = "#CTL_RepaintAction")
@Messages("CTL_RepaintAction=Repaint")
public final class RepaintAction implements ActionListener {

    private final AggContentPanel context;

    public RepaintAction(AggContentPanel context) {
        assert context != null : "context must not be null!";
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.repaint();
    }
}
