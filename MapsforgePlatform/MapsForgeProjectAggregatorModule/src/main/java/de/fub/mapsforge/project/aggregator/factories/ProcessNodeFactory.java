/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.models.Aggregator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class ProcessNodeFactory extends ChildFactory<AbstractAggregationProcess<?, ?>> {

    private final Aggregator aggregator;

    public ProcessNodeFactory(Aggregator aggregator) {
        assert aggregator != null;
        this.aggregator = aggregator;
    }

    @Override
    protected boolean createKeys(List<AbstractAggregationProcess<?, ?>> toPopulate) {
        toPopulate.addAll(aggregator.getPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(AbstractAggregationProcess<?, ?> orocess) {
        return orocess.getNodeDelegate();
    }
}
