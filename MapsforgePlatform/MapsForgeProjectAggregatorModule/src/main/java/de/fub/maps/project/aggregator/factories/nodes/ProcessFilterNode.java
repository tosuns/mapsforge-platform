/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.factories.nodes;

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.utilsmodule.node.property.ProcessPropertyWrapper;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Serdar
 */
public class ProcessFilterNode extends FilterNode implements ChangeListener {

    private ModelSynchronizer.ModelSynchronizerClient client;
    private Sheet sheet = null;

    public ProcessFilterNode(Node original) {
        super(original);
        AbstractAggregationProcess process = original.getLookup().lookup(AbstractAggregationProcess.class);
        if (process != null && process.getAggregator() != null) {
            client = process.getAggregator().create(ProcessFilterNode.this);
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public PropertySet[] getPropertySets() {

        sheet = Sheet.createDefault();
        PropertySet[] sets = super.getPropertySets();
        for (PropertySet propertySet : sets) {
            Sheet.Set sheetSet = Sheet.createPropertiesSet();
            sheetSet.setName(propertySet.getName());
            sheetSet.setDisplayName(propertySet.getDisplayName());
            sheetSet.setShortDescription(propertySet.getShortDescription());
            sheet.put(sheetSet);
            for (Property property : propertySet.getProperties()) {
                sheetSet.put(new ProcessPropertyWrapper(client, property));
            }
        }
        return sheet.toArray();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
