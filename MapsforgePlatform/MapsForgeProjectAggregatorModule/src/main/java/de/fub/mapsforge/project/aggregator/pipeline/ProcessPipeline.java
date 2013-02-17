/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Serdar
 */
public abstract class ProcessPipeline<T extends Process<?, ?>> {

    protected final ArrayList<T> pipeline = new ArrayList<T>();

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

    public interface PipelineListener {

        public enum Result {

            FINISHED,
            CANCELED,
            ERROR;
        }

        public void willStart(List<Process> propcesses);

        public void started();

        public void stopted(Result result);

        public void pipelineChanged();
    }

    public interface ProcessListener extends EventListener {

        public void changed(ProcessEvent event);
    }

    public static class ProcessEvent {

        private AbstractAggregationProcess<?, ?> source;
        private String processMessage;
        private int percentfinished;

        public ProcessEvent(AbstractAggregationProcess<?, ?> source, String processMessage, int percentfinished) {
            this.source = source;
            this.processMessage = processMessage;
            this.percentfinished = percentfinished;
        }

        public AbstractAggregationProcess<?, ?> getSource() {
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
            return "ProcessEvent{" + "source=" + source + ", processMessage=" + processMessage + ", percentfinished=" + percentfinished + '}';
        }
    }
}
