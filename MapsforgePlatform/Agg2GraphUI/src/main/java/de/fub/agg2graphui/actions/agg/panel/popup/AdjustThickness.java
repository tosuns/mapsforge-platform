/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
