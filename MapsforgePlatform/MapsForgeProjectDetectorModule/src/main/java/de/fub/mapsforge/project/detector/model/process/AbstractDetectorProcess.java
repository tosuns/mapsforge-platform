/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.process;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;

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

    protected abstract ProcessDescriptor createProcessDescriptor();
}