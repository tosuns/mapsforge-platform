/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author Serdar
 */
public interface Process<I, O> extends Runnable {

    public enum State {

        OK,
        RUNNING,
        ERROR;
    }
    public static final DataFlavor PROCESS_FLAVOR = new DataFlavor(Process.class, "Process");

    public String getName();

    public String getDescription();

    public void setInput(I input);

    public O getResult();

    public State getProcessState();

    public void addProcessListener(ProcessPipeline.ProcessListener listener);

    public void removeProcessListener(ProcessPipeline.ProcessListener listener);
}
