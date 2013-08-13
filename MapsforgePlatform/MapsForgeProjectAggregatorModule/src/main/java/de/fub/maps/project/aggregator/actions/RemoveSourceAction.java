/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.actions;

import de.fub.maps.project.aggregator.xml.Source;
import de.fub.maps.project.models.AggregatorSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Aggregator",
        id = "de.fub.maps.project.aggregator.actions.RemoveSourceAction")
@ActionRegistration(
        displayName = "#CTL_RemoveSourceAction")
@ActionReference(path = "Projects/org-maps-project/Aggregator/Source/Actions", position = 0)
@Messages("CTL_RemoveSourceAction=Remove")
public final class RemoveSourceAction implements ActionListener {

    private final AggregatorSource context;

    public RemoveSourceAction(AggregatorSource context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        List<Source> datasources = context.getAggregator().getAggregatorDescriptor().getDatasources();
        datasources.remove(context.getSource());
        context.getAggregator().getDataObject().save();
    }
}
