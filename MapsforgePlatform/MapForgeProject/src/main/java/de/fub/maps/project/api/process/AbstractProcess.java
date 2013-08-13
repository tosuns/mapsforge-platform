/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.api.process;

import de.fub.maps.project.api.process.Process;
import de.fub.maps.project.api.process.ProcessPipeline.ProcessListener;
import de.fub.maps.project.api.statistics.StatisticProvider;
import de.fub.maps.project.api.statistics.StatisticProvider.StatisticSection;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public abstract class AbstractProcess<I, O> implements Process<I, O>, PropertyChangeListener {

    public static final String PROP_NAME_PROCESS_STATE = "process.state";
    private ProcessNode nodeDelegate = null;
    private long processStartTime;
    private long processFinishTime;
    private ProcessState processState = ProcessState.INACTIVE;
    protected final Set<ProcessPipeline.ProcessListener> processListenerSet = new HashSet<ProcessPipeline.ProcessListener>();
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final Object EVENT_MUTEX = new Object();
    protected final Object RUN_MUTEX = new Object();

    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = new ProcessNode(this);
        }
        return nodeDelegate;
    }

    @Override
    public void run() {
        if (getProcessState() != ProcessState.SETTING_ERROR) {
            synchronized (RUN_MUTEX) {
                try {
                    fireProcessStartedEvent();
                    processStartTime = System.currentTimeMillis();
                    setProcessState(ProcessState.RUNNING);
                    start();
                    setProcessState(ProcessState.INACTIVE);
                    processFinishTime = System.currentTimeMillis();
                    fireProcessFinishedEvent();
                } catch (Throwable ex) {
                    setProcessState(ProcessState.ERROR);
                    throw new ProcessRuntimeException(ex);
                }
            }
        }
    }

    public void setProcessState(ProcessState processState) {
        Object oldValue = this.processState;
        this.processState = processState;
        pcs.firePropertyChange(PROP_NAME_PROCESS_STATE, oldValue, this.processState);
    }

    @Override
    public ProcessState getProcessState() {
        return processState;
    }

    protected void fireProcessProgressEvent(ProcessPipeline.ProcessEvent event) {
        synchronized (EVENT_MUTEX) {
            for (ProcessListener listener : processListenerSet) {
                listener.changed(event);
            }
        }
    }

    protected void fireProcessStartedEvent() {
        synchronized (EVENT_MUTEX) {
            for (ProcessListener listener : processListenerSet) {
                listener.started();
            }
        }
    }

    protected void fireProcessFinishedEvent() {
        synchronized (EVENT_MUTEX) {
            for (ProcessListener listener : processListenerSet) {
                listener.finished();
            }
        }
    }

    protected void fireProcessCanceledEvent() {
        synchronized (EVENT_MUTEX) {
            for (ProcessListener listener : processListenerSet) {
                listener.canceled();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    @Override
    public void addProcessListener(ProcessPipeline.ProcessListener listener) {
        synchronized (EVENT_MUTEX) {
            processListenerSet.add(listener);
        }
    }

    @Override
    public void removeProcessListener(ProcessPipeline.ProcessListener listener) {
        synchronized (EVENT_MUTEX) {
            processListenerSet.remove(listener);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @NbBundle.Messages({
        "# {0} - processName",
        "CLT_Section_Name={0} Performance",
        "# {0} - processName",
        "CLT_Section_Description=Displays performance data for the {0} process.",
        "CLT_Process_Started_Label=Process Started",
        "# {0} - processName",
        "CLT_Process_Started_Description=The last start time of this {0} process.",
        "CLT_Process_Finished_Label=Process Finished",
        "# {0} - processName",
        "CLT_Process_Finished_Description=The last finish time of this {0} process.",
        "CLT_Process_Duration_Label=Duration Time (ms)",
        "# {0} - processName",
        "CLT_Process_Duration_Description=The duration time that this {0} process took in milliseconds."
    })
    protected StatisticProvider.StatisticSection getPerformanceData() {
        StatisticSection section = new StatisticProvider.StatisticSection(Bundle.CLT_Section_Name(getName()), Bundle.CLT_Section_Description(getName()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:MM:ss:SSS");
        section.getStatisticsItemList().add(new StatisticProvider.StatisticItem(Bundle.CLT_Process_Started_Label(), simpleDateFormat.format(new Date(processStartTime)), Bundle.CLT_Process_Started_Description(getName())));
        section.getStatisticsItemList().add(new StatisticProvider.StatisticItem(Bundle.CLT_Process_Finished_Label(), simpleDateFormat.format(new Date(processFinishTime)), Bundle.CLT_Process_Finished_Description(getName())));
        section.getStatisticsItemList().add(new StatisticProvider.StatisticItem(Bundle.CLT_Process_Duration_Label(), String.valueOf(processFinishTime - processStartTime), Bundle.CLT_Process_Duration_Description(getName())));
        return section;
    }

    @Override
    public int compareTo(Process<?, ?> process) {
        return getName().compareTo(process.getName());
    }

    protected abstract void start();

    public abstract Image getIcon();

    public abstract JComponent getSettingsView();
}
