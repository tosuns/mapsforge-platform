/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    @ActionReference(id =
            @ActionID(
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
