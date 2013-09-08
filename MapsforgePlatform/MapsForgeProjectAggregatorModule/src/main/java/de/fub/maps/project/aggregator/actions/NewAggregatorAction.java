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
package de.fub.maps.project.aggregator.actions;

import de.fub.maps.project.aggregator.actions.wizards.aggregator.AggregatorWizardAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Aggregator",
        id = "de.fub.maps.project.aggregator.actions.NewAggregatorAction")
@ActionRegistration(
        iconBase = "de/fub/maps/project/aggregator/filetype/aggregationBuilderIcon.png",
        displayName = "#CTL_NewAggregatorAction")
//@ActionReference(path = "Menu/Aggregator", position = 0)
@ActionReferences({
    @ActionReference(id
            = @ActionID(
                    category = "Aggregator",
                    id = "de.fub.maps.project.aggregator.actions.NewAggregatorAction"),
            path = "Projects/org-maps-project/Aggregator/Actions", position = 0)
})
@Messages("CTL_NewAggregatorAction=New Aggregator")
public final class NewAggregatorAction implements ActionListener {

    private final DataObject dataObject;

    public NewAggregatorAction(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AggregatorWizardAction(dataObject).actionPerformed(e);
    }
}
