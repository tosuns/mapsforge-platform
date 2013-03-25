/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import java.io.IOException;
import java.util.ArrayList;
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
                    processDescriptor = DetectorUtils.getXmlDescriptor(ProcessDescriptor.class, getClass());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                ArrayList<ProcessDescriptor> arrayList = new ArrayList<ProcessDescriptor>(getDetector().getDetectorDescriptor().getPreprocessors().getPreprocessorList());
                arrayList.addAll(getDetector().getDetectorDescriptor().getPostprocessors().getPostprocessorList());
                for (ProcessDescriptor filterDescriptor : arrayList) {
                    if (filterDescriptor != null
                            && getClass().getName().equals(filterDescriptor.getJavaType())) {
                        processDescriptor = filterDescriptor;
                        break;
                    }
                }
            }
        }
        return processDescriptor;
    }
}
