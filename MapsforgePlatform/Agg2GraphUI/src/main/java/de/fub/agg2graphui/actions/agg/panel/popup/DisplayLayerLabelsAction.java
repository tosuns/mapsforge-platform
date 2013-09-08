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
