/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.postprocessors;

import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapsforge.project.detector.model.Detector;

/**
 *
 * @author Serdar
 */
public class PostProcessorPipeline extends ProcessPipeline<Task> {

    private final Detector detector;

    public PostProcessorPipeline(Detector detector) {
        assert detector != null;
        this.detector = detector;
    }
}
