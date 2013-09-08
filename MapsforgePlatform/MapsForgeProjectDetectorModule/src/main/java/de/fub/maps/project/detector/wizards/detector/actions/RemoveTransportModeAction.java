/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.wizards.detector.actions;

import de.fub.maps.project.detector.wizards.detector.TrainingSetSelectionVisualPanel;
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
        id = "de.fub.maps.project.detector.wizards.detector.actions.RemoveTransportModeAction")
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
