/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.aggregator.pipeline;

import de.fub.maps.project.api.process.ProcessPipeline;
import de.fub.maps.project.models.Aggregator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
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
 * This class represents the pipeline of an aggregator. It contains the process
 * units of the parent Aggregator and handles the chained execution of the
 * contained process units.
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

    /**
     * Starts the chained execution of the specified process units.
     *
     * @param processes A list of process units, which will be exceuted.
     */
    @NbBundle.Messages({
        "# {0} - processName",
        "# {1} - aggregatorName",
        "CLT_Proceeding_Process={1}: Running {0}..."})
    @SuppressWarnings({"unchecked"})
    public void start(final List<AbstractAggregationProcess<?, ?>> processes) {
        synchronized (MUTEX_PROCESS_RUNNING) {
            canceled.set(false);
            workingProcesses.clear();
            workingProcesses.addAll(processes);
            handler = ProgressHandleFactory.createHandle(Bundle.CLT_Proceeding_Process("", aggregator.getAggregatorDescriptor().getName()), new CancellableImpl());
            try {
                firePipelineEvent(PipelineEvents.WILL_START);

                handler.start(100);
                firePipelineEvent(PipelineEvents.STARTED);
//                int i = 0;
                AbstractAggregationProcess lastProcess = null;
                long lastTime = System.currentTimeMillis();

                for (int j = 0; j < workingProcesses.size(); j++) {

                    unit = j;
                    process = workingProcesses.get(j);

                    if (canceled.get()) {
                        firePipelineEvent(PipelineEvents.CANCELED);
                        break;
                    }

                    handler.setDisplayName(Bundle.CLT_Proceeding_Process(process.getName(), getAggregator().getAggregatorDescriptor().getName()));
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
                    LOG.log(Level.FINE, "Process {0} took {1} time.", new Object[]{process.getName(), (now - lastTime)});
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
            LOG.log(Level.FINE, "progress {0}", progress);
            handler.progress(event.getProcessMessage(), (int) progress);
        }
    }

    /**
     * Adds the specified process unit to this pipeline
     *
     * @param proc
     * @return
     * @throws
     * de.fub.maps.project.aggregator.pipeline.AggregatorProcessPipeline.PipelineException
     */
    @Override
    public boolean add(AbstractAggregationProcess<?, ?> proc) throws PipelineException {
        checkIsValidProcess(size() - 1, proc);
        return super.add(proc);
    }

    /**
     * Adds the provided list of process units to this pipeline.
     *
     * @param index
     * @param proc
     */
    @Override
    public void add(int index, AbstractAggregationProcess<?, ?> proc) {
        checkIsValidProcess(index, proc);
        super.add(index, proc);
    }

    @Override
    public boolean addAll(Collection<? extends AbstractAggregationProcess<?, ?>> collection) {
        boolean result = true;
        for (AbstractAggregationProcess<?, ?> proc : collection) {
            add(proc);
        }
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends AbstractAggregationProcess<?, ?>> collection) {
        boolean result = true;
        for (AbstractAggregationProcess<?, ?> proc : collection) {
            add(index, proc);
            index++;
        }
        return result;
    }

    /**
     * Checks whether the provided process unit violates the pipeline invariant.
     *
     * @param prevProcessIndex
     * @param process
     * @throws
     * de.fub.maps.project.aggregator.pipeline.AggregatorProcessPipeline.PipelineException
     */
    public void checkIsValidProcess(int prevProcessIndex, AbstractAggregationProcess<?, ?> process) throws PipelineException {
        if (prevProcessIndex > -1) {
            ArrayList<AbstractAggregationProcess<?, ?>> arrayList = new ArrayList<AbstractAggregationProcess<?, ?>>(getProcesses());
            AbstractAggregationProcess<?, ?> previousProcess = arrayList.get(prevProcessIndex);
            Class<? extends AbstractAggregationProcess> previousProcessClass = previousProcess.getClass();
            Class<? extends AbstractAggregationProcess> currentProcessClass = process.getClass();
            compareGenericTypes(currentProcessClass, previousProcessClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void compareGenericTypes(Type currentProcessClass, Type previousProcessClass) throws PipelineException {
        Type previousProcessSuperclass = previousProcessClass instanceof Class ? ((Class) previousProcessClass).getGenericSuperclass() : previousProcessClass.getClass().getGenericSuperclass();
        Type currentProcessSuperclass = currentProcessClass instanceof Class ? ((Class) currentProcessClass).getGenericSuperclass() : currentProcessClass.getClass().getGenericSuperclass();

        // in case both types are of type class then check whether they are
        //
        LOG.fine(MessageFormat.format("prev : {0} , current: {1}", previousProcessSuperclass, currentProcessSuperclass));
        if (previousProcessSuperclass instanceof Class
                && currentProcessSuperclass instanceof Class
                && previousProcessSuperclass.equals(currentProcessSuperclass)) {
        } else if (previousProcessSuperclass instanceof ParameterizedType
                && currentProcessSuperclass instanceof ParameterizedType) {
            Type[] outputTypeArray = ((ParameterizedType) previousProcessSuperclass).getActualTypeArguments();
            Type[] inputTypeArray = ((ParameterizedType) currentProcessSuperclass).getActualTypeArguments();
            // if both array have the length 2
            // check there types
            if (outputTypeArray.length == inputTypeArray.length
                    && outputTypeArray.length == 2) {
                Type outputType = outputTypeArray[1];
                Type inputType = inputTypeArray[0];
                if (outputType.getClass().equals(inputType.getClass())) {
                    compareGenericTypes(inputType, outputType);
                } else {
                    throw new PipelineException(MessageFormat.format("input type {0} and output type {1} don't match!", inputType, outputType));
                }
            } else if (outputTypeArray.length == inputTypeArray.length
                    && outputTypeArray.length == 0) {
                // recursive anchor. Both classes have no type parameters
            } else {
                // both type array lengths are not 2. Error state
                throw new PipelineException(MessageFormat.format("output generic interfaces type {0} and input generice interfaces type {1}  length don't macth !", outputTypeArray, inputTypeArray));
            }
        } else {
            throw new PipelineException(MessageFormat.format("Current case not supported type1 :{0} type2 : {1}", previousProcessSuperclass, currentProcessSuperclass));
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

    public static class PipelineException extends IllegalArgumentException {

        private static final long serialVersionUID = 1L;

        public PipelineException() {
        }

        public PipelineException(String s) {
            super(s);
        }

        public PipelineException(String message, Throwable cause) {
            super(message, cause);
        }

        public PipelineException(Throwable cause) {
            super(cause);
        }
    }
}
