/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model.process;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * Extention of the DetectorProcess, which provides access to the
 * ProcessDescriptor.
 *
 * @author Serdar
 */
public abstract class AbstractDetectorProcess<I, O> extends DetectorProcess<I, O> {

    private ProcessDescriptor processDescriptor = null;

    public AbstractDetectorProcess() {
    }

    /**
     * @see {@inheritDoc DetectorProcess}
     * @param detector
     */
    @Override
    protected void setDetector(Detector detector) {
        super.setDetector(detector);
        processDescriptor = null;
    }

    /**
     * Returns the ProcessDescriptor
     *
     * @return
     */
    public ProcessDescriptor getProcessDescriptor() {
        if (processDescriptor == null) {
            processDescriptor = createProcessDescriptor();
        }
        return processDescriptor;
    }

    /**
     * Sets this Process' descriptor.
     *
     * @param processDescriptor
     */
    protected void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        this.processDescriptor = processDescriptor;
    }

    /**
     * All Subclasses provide via this method all ProcessDescriptor to persist
     * the configuration of this process within the DetectorDescriptor.
     *
     * @return a ProcessDescriptor
     */
    protected abstract ProcessDescriptor createProcessDescriptor();

    /**
     * Factory method find all via <code>@ServiceProvider</code> annotated
     * AbstractDetectorProcess.
     *
     * @param <T> extends AbstractDetectorProcess
     * @param clazz the concrete type of the to be instanciated
     * AbstractDetectorProcesses.
     * @return a list of AbstractDetectorProcess instances.
     */
    @SuppressWarnings("unchecked")
    protected static synchronized <T extends AbstractDetectorProcess> Collection<T> findAll(Class<T> clazz) {
        ArrayList<T> resultList = new ArrayList<T>(50);
        Collection<? extends T> allInstances = Lookup.getDefault().lookupResult(clazz).allInstances();
        for (T instance : allInstances) {
            try {
                resultList.add((T) instance.getClass().newInstance());
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return resultList;
    }
}
