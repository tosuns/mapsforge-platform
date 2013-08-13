/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.api.process;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author Serdar
 */
public interface Process<I, O> extends Runnable, Comparable<Process<?, ?>> {

    public static final DataFlavor PROCESS_FLAVOR = new DataFlavor(Process.class, "Process");

    public String getName();

    public String getDescription();

    public void setInput(I input);

    public O getResult();

    public ProcessState getProcessState();

    public void addProcessListener(ProcessPipeline.ProcessListener listener);

    public void removeProcessListener(ProcessPipeline.ProcessListener listener);
}
