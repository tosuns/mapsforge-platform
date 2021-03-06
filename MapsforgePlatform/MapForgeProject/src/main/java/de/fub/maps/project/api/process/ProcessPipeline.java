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
package de.fub.maps.project.api.process;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract class of an ProcessPipeline, which holds process unit of type
 * <code>Process</code>.
 *
 * @author Serdar
 * @param <T>
 */
public abstract class ProcessPipeline<T extends Process<?, ?>> {

    protected final ArrayList<T> pipeline = new ArrayList<T>();
    private final HashSet<PipelineListener> listenerSet = new HashSet<PipelineListener>();
    private final Object EVENT_MUTEX = new Object();

    public Collection<T> getProcesses() {
        return pipeline;
    }

    public int size() {
        return pipeline.size();
    }

    public boolean isEmpty() {
        return pipeline.isEmpty();
    }

    public int indexOf(Object o) {
        return pipeline.indexOf(o);
    }

    public T get(int index) {
        return pipeline.get(index);
    }

    public T set(int index, T element) {
        return pipeline.set(index, element);
    }

    public boolean add(T e) {
        return pipeline.add(e);
    }

    public void add(int index, T element) {
        pipeline.add(index, element);
    }

    public Process<?, ?> remove(int index) {
        return pipeline.remove(index);
    }

    public boolean remove(T o) {
        return pipeline.remove(o);
    }

    public void clear() {
        pipeline.clear();
    }

    public boolean addAll(Collection<? extends T> c) {
        return pipeline.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return pipeline.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return pipeline.removeAll(c);
    }

    public Iterator<T> iterator() {
        return pipeline.iterator();
    }

    protected void firePipelineEvent(PipelineEvents event) {
        synchronized (EVENT_MUTEX) {
            for (PipelineListener listener : listenerSet) {
                switch (event) {
                    case CANCELED:
                        listener.stoped(PipelineListener.Result.CANCELED);
                        break;
                    case CHANCHED:
                        listener.pipelineChanged();
                        break;
                    case FINISHED:
                        listener.stoped(PipelineListener.Result.FINISHED);
                        break;
                    case STARTED:
                        listener.started();
                        break;
                    case WILL_START:
                        break;
                    case ERROR:
                        listener.stoped(PipelineListener.Result.ERROR);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void addPipelineListener(PipelineListener listener) {
        synchronized (EVENT_MUTEX) {
            listenerSet.add(listener);
        }
    }

    public void removePipelineListener(PipelineListener listener) {
        synchronized (EVENT_MUTEX) {
            listenerSet.remove(listener);
        }
    }

    public interface PipelineListener extends EventListener {

        public enum Result {

            FINISHED,
            CANCELED,
            ERROR;
        }

        public void willStart(List<Process> propcesses);

        public void started();

        public void stoped(Result result);

        public void pipelineChanged();
    }

    public interface ProcessListener extends EventListener {

        public void started();

        public void changed(ProcessEvent event);

        public void canceled();

        public void finished();
    }

    public static class ProcessEvent<T extends Process<?, ?>> {

        private T source;
        private String processMessage;
        private int percentfinished;

        public ProcessEvent(T source, String processMessage, int percentfinished) {
            this.source = source;
            this.processMessage = processMessage;
            this.percentfinished = percentfinished;
        }

        public T getSource() {
            return source;
        }

        public String getProcessMessage() {
            return processMessage;
        }

        public int getPercentfinished() {
            return percentfinished;
        }

        @Override
        public String toString() {
            return MessageFormat.format("ProcessEvent[source={0}, processMessage={1}, percentfinished={2}]", source, processMessage, percentfinished);
        }
    }

    protected enum PipelineEvents {

        WILL_START, STARTED, FINISHED, CANCELED, CHANCHED, ERROR;
    }
}
