/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapforgeproject.api.process.ProcessPipeline;

/**
 *
 * @author Serdar
 */
public class PostProcessorPipeline extends ProcessPipeline<Task<?, ?>> {

    private final Detector detector;

    PostProcessorPipeline(Detector detector) {
        assert detector != null;
        this.detector = detector;
    }
}
