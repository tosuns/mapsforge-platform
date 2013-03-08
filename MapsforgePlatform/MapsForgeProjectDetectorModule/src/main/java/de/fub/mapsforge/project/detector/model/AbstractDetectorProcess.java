/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public abstract class AbstractDetectorProcess<I, O> extends DetectorProcess<I, O> {

    private ProcessDescriptor processDescriptor = null;

    public AbstractDetectorProcess(Detector detector) {
        super(detector);
    }

    public ProcessDescriptor getProcessDescriptor() {
        if (processDescriptor == null) {
            if (getDetector() == null) {
                try {
                    processDescriptor = DetectorUtils.getProcessDescriptor(getClass());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    processDescriptor = new ProcessDescriptor(getName(), getDescription(), getClass().getName());

                }
            } else {
                for (ProcessDescriptor filterDescriptor : getDetector().getDetectorDescriptor().getPreprocessors().getPreprocessorList()) {
                    if (filterDescriptor != null
                            && DetectorProcess.class.getName().equals(filterDescriptor.getJavaType())
                            && getName().equals(filterDescriptor.getName())) {
                        processDescriptor = filterDescriptor;
                        break;
                    }
                }
            }
        }
        return processDescriptor;
    }
}
