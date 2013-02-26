/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.api.process;

import de.fub.mapforgeproject.api.process.Process;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author Serdar
 */
public interface Process<I, O> extends Runnable {

    public static final DataFlavor PROCESS_FLAVOR = new DataFlavor(Process.class, "Process");

    public String getName();

    public String getDescription();

    public void setInput(I input);

    public O getResult();

    public ProcessState getProcessState();

    public void addProcessListener(ProcessPipeline.ProcessListener listener);

    public void removeProcessListener(ProcessPipeline.ProcessListener listener);
}
