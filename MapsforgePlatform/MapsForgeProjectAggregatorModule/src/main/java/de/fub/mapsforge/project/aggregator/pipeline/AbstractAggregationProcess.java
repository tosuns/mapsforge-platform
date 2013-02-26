/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapforgeproject.api.process.AbstractProcess;
import de.fub.mapsforge.project.aggregator.xml.AggregatorDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptorList;
import de.fub.mapsforge.project.models.Aggregator;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public abstract class AbstractAggregationProcess<I, O> extends AbstractProcess<I, O> {

    public static final String PROP_NAME_PROCESS_DESCRIPTOR = "process.descriptor";
    protected Aggregator aggregator;
    protected ProcessDescriptor descriptor;
    protected ArrayList<AbstractLayer<?>> layers = new ArrayList<AbstractLayer<?>>();
    private Node nodeDelegate;

    public AbstractAggregationProcess(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public Aggregator getAggContainer() {
        return aggregator;
    }

    @Override
    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new AggregationProcessNode(AbstractAggregationProcess.this);
        }
        return nodeDelegate;
    }

    public ProcessDescriptor getDescriptor() {
        if (descriptor == null) {
            if (aggregator != null) {
                AggregatorDescriptor aggregatorDescriptor = aggregator.getDescriptor();
                if (aggregatorDescriptor != null) {
                    ProcessDescriptorList pipeline = aggregatorDescriptor.getPipeline();
                    for (ProcessDescriptor processDescriptor : pipeline.getList()) {
                        if (getClass().getName().equals(processDescriptor.getJavaType())) {
                            descriptor = processDescriptor;
                            break;
                        }
                    }
                }
            } else {
                descriptor = createProcessDescriptor();
            }
        }
        return descriptor;
    }

    public void setDescriptor(ProcessDescriptor descriptor) {
        Object oldValue = this.descriptor;
        this.descriptor = descriptor;
        pcs.firePropertyChange(PROP_NAME_PROCESS_DESCRIPTOR, oldValue, this.descriptor);
    }

    public List<AbstractLayer<?>> getLayers() {
        return layers;
    }

    protected abstract ProcessDescriptor createProcessDescriptor();
}
