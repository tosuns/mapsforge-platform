/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;

/**
 *
 * @author Serdar
 */
public abstract class AbstractDetectorProcess<I, O> extends DetectorProcess<I, O> {

    private ProcessDescriptor processDescriptor = null;

    public AbstractDetectorProcess() {
        this(null);
    }

    public AbstractDetectorProcess(Detector detector) {
        super(detector);
    }

    public ProcessDescriptor getProcessDescriptor() {
        if (processDescriptor == null) {
            processDescriptor = createProcessDescriptor();
        }
        return processDescriptor;
    }

    protected abstract ProcessDescriptor createProcessDescriptor();
}
