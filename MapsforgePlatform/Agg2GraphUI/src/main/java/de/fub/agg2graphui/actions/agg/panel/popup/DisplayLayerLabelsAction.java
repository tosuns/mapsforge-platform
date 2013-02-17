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
id = "de.fub.agg2graphui.actions.agg.panel.popup.DisplayLayerLabelsAction")
@ActionRegistration(
    displayName = "#CTL_DisplayLayerLabelsAction")
@Messages("CTL_DisplayLayerLabelsAction=Labels On/Off")
public final class DisplayLayerLabelsAction implements ActionListener {

    private final AggContentPanel context;

    public DisplayLayerLabelsAction(AggContentPanel context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.setLabelsVisible(!context.isLabelsVisible());                
    }
}
