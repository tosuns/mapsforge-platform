/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.process;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public abstract class AbstractDetectorProcess<I, O> extends DetectorProcess<I, O> {

    private ProcessDescriptor processDescriptor = null;

    public AbstractDetectorProcess() {
    }

    @Override
    protected void setDetector(Detector detector) {
        super.setDetector(detector);
        processDescriptor = null;
    }

    public ProcessDescriptor getProcessDescriptor() {
        if (processDescriptor == null) {
            processDescriptor = createProcessDescriptor();
        }
        return processDescriptor;
    }

    protected void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        this.processDescriptor = processDescriptor;
    }

    protected abstract ProcessDescriptor createProcessDescriptor();

    @SuppressWarnings("unchecked")
    protected static synchronized <T> Collection<T> findAll(Class<T> clazz) {
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
