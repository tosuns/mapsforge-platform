/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * Factory method find all via
     * <code>@ServiceProvider</code> annotated AbstractDetectorProcess.
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
