/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapsforge.project.aggregator.pipeline.ProcessPipeline.ProcessEvent;
import de.fub.mapsforge.project.aggregator.pipeline.ProcessPipeline.ProcessListener;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public abstract class AbstractAggregationProcess<I, O> implements Process<I, O>, PropertyChangeListener {

    public static final String PROP_NAME_PROCESS_STATE = "process.state";
    public static final String PROP_NAME_PROCESS_DESCRIPTOR = "process.descriptor";
    protected State processState = State.OK;
    protected Aggregator aggregator;
    protected ProcessDescriptor descriptor = new ProcessDescriptor();
    protected ArrayList<AbstractLayer<?>> layers = new ArrayList<AbstractLayer<?>>();
    protected final Set<ProcessListener> processListenerSet = new HashSet<ProcessListener>();
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final Object MUTEX = new Object();
    private Node nodeDelegate;

    public AbstractAggregationProcess(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public Aggregator getAggContainer() {
        return aggregator;
    }

    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new ProcessNode(AbstractAggregationProcess.this);
        }
        return nodeDelegate;
    }

    public ProcessDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public State getProcessState() {
        return processState;
    }

    @Override
    public void run() {
        try {
            setProcessState(State.RUNNING);
            start();
            setProcessState(State.OK);
        } catch (Throwable ex) {
            setProcessState(State.ERROR);
            throw new ProcessRuntimeException(ex);
        }
    }

    protected void setProcessState(State state) {
        Object oldValue = processState;
        processState = state;
        pcs.firePropertyChange(PROP_NAME_PROCESS_STATE, oldValue, processState);
    }

    public void setDescriptor(ProcessDescriptor descriptor) {
        Object oldValue = this.descriptor;
        this.descriptor = descriptor;
        if (descriptor != null) {
            descriptor.addPropertyChangeListener(AbstractAggregationProcess.this);
        }
        pcs.firePropertyChange(PROP_NAME_PROCESS_DESCRIPTOR, oldValue, this.descriptor);
    }

    protected void fireProcessEvent(ProcessEvent event) {
        synchronized (MUTEX) {
            for (ProcessListener listener : processListenerSet) {
                listener.changed(event);
            }
        }
    }

    @Override
    public void addProcessListener(ProcessListener listener) {
        synchronized (MUTEX) {
            processListenerSet.add(listener);
        }
    }

    @Override
    public void removeProcessListener(ProcessListener listener) {
        synchronized (MUTEX) {
            processListenerSet.remove(listener);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public List<AbstractLayer<?>> getLayers() {
        return layers;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    protected abstract void start();

    public abstract Image getIcon();

    public abstract JComponent getSettingsView();

    public static class ProcessRuntimeException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public ProcessRuntimeException() {
        }

        public ProcessRuntimeException(String message) {
            super(message);
        }

        public ProcessRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

        public ProcessRuntimeException(Throwable cause) {
            super(cause);
        }

        public ProcessRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
