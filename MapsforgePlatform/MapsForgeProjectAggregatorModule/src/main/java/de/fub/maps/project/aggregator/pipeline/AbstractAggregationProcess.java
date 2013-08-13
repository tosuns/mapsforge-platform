/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline;

import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.maps.project.api.process.AbstractProcess;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.models.Aggregator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public abstract class AbstractAggregationProcess<I, O> extends AbstractProcess<I, O> implements Cancellable {

    public static final String PROP_NAME_PROCESS_DESCRIPTOR = "process.descriptor";
    private static final Object MUTEX_PROCESS_CREATOR = new Object();
    private Aggregator aggregator;
    private ProcessDescriptor descriptor;
    private ArrayList<AbstractLayer<?>> layers = new ArrayList<AbstractLayer<?>>();
    private Node nodeDelegate;
    protected AtomicBoolean canceled = new AtomicBoolean(false);

    public Aggregator getAggregator() {
        return aggregator;
    }

    protected void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
        descriptor = null;
        nodeDelegate = null;
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

    public ProcessDescriptor getProcessDescriptor() {
        if (descriptor == null) {
            if (getAggregator() != null) {
                for (ProcessDescriptor processDescriptor : getAggregator().getAggregatorDescriptor().getPipeline().getList()) {
                    if (processDescriptor != null
                            && getClass().getName().equals(processDescriptor.getJavaType())) {
                        descriptor = processDescriptor;
                        break;
                    }
                }
            }
            if (descriptor == null) {
                descriptor = createProcessDescriptor();
            }
        }
        return descriptor;
    }

    public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        this.descriptor = processDescriptor;
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

    public static List<AbstractAggregationProcess<?, ?>> findAll() {
        Set<Class<? extends AbstractAggregationProcess>> allClasses = Lookup.getDefault().lookupResult(AbstractAggregationProcess.class).allClasses();
        List<AbstractAggregationProcess<?, ?>> list = new ArrayList<AbstractAggregationProcess<?, ?>>(allClasses.size());

        for (Class<? extends AbstractAggregationProcess> clazz : allClasses) {
            try {
                AbstractAggregationProcess<?, ?> process = clazz.newInstance();
                list.add(process);
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return list;
    }

    public static AbstractAggregationProcess<?, ?> find(String qualifiedname) throws AbstractAggregationProcessNotFoundException {
        return AbstractAggregationProcess.find(qualifiedname, null);
    }

    public static AbstractAggregationProcess<?, ?> find(String qualifiedName, Aggregator aggregator) throws AbstractAggregationProcessNotFoundException {
        synchronized (MUTEX_PROCESS_CREATOR) {
            AbstractAggregationProcess<?, ?> process = null;
            Set<Class<? extends AbstractAggregationProcess>> allClasses = Lookup.getDefault().lookupResult(AbstractAggregationProcess.class).allClasses();

            for (Class<? extends AbstractAggregationProcess> clazz : allClasses) {
                if (clazz.getName().equals(qualifiedName)) {
                    try {
                        process = clazz.newInstance();
                        process.setAggregator(aggregator);
                    } catch (Exception ex) {
                        throw new AbstractAggregationProcessNotFoundException(ex);
                    }
                }
            }
            return process;
        }
    }

    public static class AbstractAggregationProcessNotFoundException extends Exception {

        private static final long serialVersionUID = 1L;

        public AbstractAggregationProcessNotFoundException() {
        }

        public AbstractAggregationProcessNotFoundException(String message) {
            super(message);
        }

        public AbstractAggregationProcessNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public AbstractAggregationProcessNotFoundException(Throwable cause) {
            super(cause);
        }
    }
}
