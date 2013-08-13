/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.factories;

import de.fub.maps.project.aggregator.factories.nodes.ProcessFilterNode;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class ProcessNodeFactory extends ChildFactory<AbstractAggregationProcess<?, ?>> {

    private final Aggregator aggregator;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public ProcessNodeFactory(Aggregator aggregator) {
        assert aggregator != null;
        this.aggregator = aggregator;
        modelSynchronizerClient = aggregator.create(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }

    @Override
    protected boolean createKeys(List<AbstractAggregationProcess<?, ?>> toPopulate) {
        toPopulate.addAll(aggregator.getPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(AbstractAggregationProcess<?, ?> process) {
        return new ProcessFilterNode(process.getNodeDelegate());
    }
}
