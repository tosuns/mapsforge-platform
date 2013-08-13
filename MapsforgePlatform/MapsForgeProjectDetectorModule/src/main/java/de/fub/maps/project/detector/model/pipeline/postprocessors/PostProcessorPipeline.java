/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.pipeline.postprocessors;

import de.fub.maps.project.api.process.ProcessPipeline;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.pipeline.postprocessors.tasks.Task;

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
