/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapforgeproject.api.process.AbstractProcess;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.models.Aggregator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;

/**
 *
 * @author Serdar
 */
public abstract class AbstractAggregationProcess<I, O> extends AbstractProcess<I, O> implements Cancellable {

    public static final String PROP_NAME_PROCESS_DESCRIPTOR = "process.descriptor";
    private Aggregator aggregator;
    private ProcessDescriptor descriptor;
    private ArrayList<AbstractLayer<?>> layers = new ArrayList<AbstractLayer<?>>();
    private Node nodeDelegate;
    protected AtomicBoolean canceled = new AtomicBoolean(false);

    public AbstractAggregationProcess(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new AggregationProcessNode(AbstractAggregationProcess.this);
        }
        return nodeDelegate;
    }

    @Override
    public void run() {
        canceled.set(false);
        super.run();
    }

    public ProcessDescriptor getDescriptor() {
        if (descriptor == null) {
            if (getAggregator() == null) {
                descriptor = createProcessDescriptor();
            } else {
                for (ProcessDescriptor processDescriptor : getAggregator().getDescriptor().getPipeline().getList()) {
                    if (processDescriptor != null
                            && getClass().getName().equals(processDescriptor.getJavaType())) {
                        descriptor = processDescriptor;
                        break;
                    }
                }
            }
        }
        return descriptor;
    }

    public List<AbstractLayer<?>> getLayers() {
        return layers;
    }

    @Override
    protected void fireProcessStartedEvent() {
        canceled.set(false);
        super.fireProcessStartedEvent();
    }

    @Override
    protected void fireProcessCanceledEvent() {
        canceled.set(true);
        super.fireProcessCanceledEvent();
    }

    protected abstract ProcessDescriptor createProcessDescriptor();
}
