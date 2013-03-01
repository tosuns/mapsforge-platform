/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapsforge.project.models.Aggregator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class AggregatorProcessPipeline extends ProcessPipeline<AbstractAggregationProcess<?, ?>> implements ProcessPipeline.ProcessListener {

    private static final Logger LOG = Logger.getLogger(AggregatorProcessPipeline.class.getName());
    private final Aggregator aggregator;
    private final AtomicBoolean canceled = new AtomicBoolean(false);
    private final Object MUTEX_PROCESS_RUNNING = new Object();
    private AbstractAggregationProcess process;
    private int unit;
    private List<AbstractAggregationProcess<?, ?>> workingProcesses = new ArrayList<AbstractAggregationProcess<?, ?>>();
    private ProgressHandle handler;

    public AggregatorProcessPipeline(Aggregator aggregator1) {
        super();
        assert aggregator1 != null;
        this.aggregator = aggregator1;

    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    @NbBundle.Messages({"# {0} - processName", "# {1} - aggregatorName", "CLT_Proceeding_Process={1}: Running {0}..."})
    @SuppressWarnings({"unchecked"})
    public void start(final List<AbstractAggregationProcess<?, ?>> processes) {
        synchronized (MUTEX_PROCESS_RUNNING) {
            canceled.set(false);
            workingProcesses.clear();
            workingProcesses.addAll(processes);
            handler = ProgressHandleFactory.createHandle(Bundle.CLT_Proceeding_Process("", aggregator.getDescriptor().getName()), new CancellableImpl());
            try {
                firePipelineEvent(PipelineEvents.WILL_START);

                handler.start(100);
                int i = 0;
                AbstractAggregationProcess lastProcess = null;
                long lastTime = System.currentTimeMillis();

                for (int j = 0; j < workingProcesses.size(); j++) {

                    unit = j;
                    process = workingProcesses.get(j);

                    if (canceled.get()) {
                        firePipelineEvent(PipelineEvents.CANCELED);
                        break;
                    }

                    handler.setDisplayName(Bundle.CLT_Proceeding_Process(process.getName(), getAggregator().getDescriptor().getName()));
                    if (j == 0) {
                        int indexOf = indexOf(process);
                        if (indexOf > 0) {
                            lastProcess = get(indexOf - 1);
                        }
                    }
                    if (lastProcess != null) {
                        Object result = lastProcess.getResult();
                        process.setInput(result);
                    }
                    process.addProcessListener(AggregatorProcessPipeline.this);
                    lastTime = System.currentTimeMillis();
                    process.run();
                    long now = System.currentTimeMillis();
                    process.removeProcessListener(AggregatorProcessPipeline.this);
                    LOG.log(Level.INFO, "Process {0} took {1} time.", new Object[]{process.getName(), (now - lastTime)});
                    lastProcess = process;
                    lastTime = now;
                }
                firePipelineEvent(PipelineEvents.FINISHED);
            } catch (Throwable ex) {
                Exceptions.printStackTrace(ex);
                firePipelineEvent(PipelineEvents.ERROR);
            } finally {
                handler.finish();
            }
        }
    }

    @Override
    public void changed(ProcessPipeline.ProcessEvent event) {
        if (workingProcesses != null && handler != null) {
            double progress = (100d / workingProcesses.size());
            progress = (progress / 100 * event.getPercentfinished()) + (100 / workingProcesses.size() * unit);
            LOG.log(Level.INFO, "progress {0}", progress);
            handler.progress(event.getProcessMessage(), (int) progress);
        }
    }

    @Override
    public void started() {
        // do nothing
    }

    @Override
    public void canceled() {
        canceled.set(true);
    }

    @Override
    public void finished() {
        //  do nothing
    }

    private class CancellableImpl implements Cancellable {

        public CancellableImpl() {
        }

        @Override
        public boolean cancel() {
            boolean result = false;
            if (process != null) {
                result = process.cancel();
            }
            canceled.set(true);
            result = canceled.get();
            return result;
        }
    }
}
