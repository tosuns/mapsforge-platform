/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.postprocessors;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.AbstractDetectorProcess;

/**
 *
 * @author Serdar
 */
public abstract class Task<I, O> extends AbstractDetectorProcess<I, O> {

    public Task(Detector detector) {
        super(detector);
    }
}
