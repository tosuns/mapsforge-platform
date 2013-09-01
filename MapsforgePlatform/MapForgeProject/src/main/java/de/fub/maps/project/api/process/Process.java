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

import java.awt.datatransfer.DataFlavor;

/**
 * This interface represent a process unit. The methods provide meta data and
 * access to this process unit. This interface represents more or less a
 * function.
 *
 * @author Serdar
 * @param <I>
 * @param <O>
 */
public interface Process<I, O> extends Runnable, Comparable<Process<?, ?>> {

    public static final DataFlavor PROCESS_FLAVOR = new DataFlavor(Process.class, "Process");

    /**
     * The name of this process unit.
     *
     * @return String, null not permitted.
     */
    public String getName();

    /**
     * The description of this process unit.
     *
     * @return String, null not permitted.
     */
    public String getDescription();

    /**
     * Sets the input data, which will be processed during the excecution of
     * this process unit.
     *
     * @param input null not permitted.
     */
    public void setInput(I input);

    /**
     * Access method to the result of this process unit.
     *
     * @return The result instance.
     */
    public O getResult();

    /**
     * Access to the current process state of this process unit.
     *
     * @return ProcessState, null not permitted.
     */
    public ProcessState getProcessState();

    /**
     * Adds a process listener to this process unit to be informed about the
     * current process state of this process unit.
     *
     * @param listener ProcessListener instance, null not permitted.
     */
    public void addProcessListener(ProcessPipeline.ProcessListener listener);

    /**
     * Removes a process listener from this process unit.
     *
     * @param listener ProcessListener, which will be removed from this process
     * unit, null not permitted.
     */
    public void removeProcessListener(ProcessPipeline.ProcessListener listener);
}
