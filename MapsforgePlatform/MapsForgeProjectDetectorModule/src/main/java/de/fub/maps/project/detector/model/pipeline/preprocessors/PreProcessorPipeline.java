/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.pipeline.preprocessors;

import de.fub.maps.project.api.process.ProcessPipeline;
import de.fub.maps.project.detector.model.Detector;

/**
 *
 * @author Serdar
 */
public class PreProcessorPipeline extends ProcessPipeline<FilterProcess> {

    private final Detector detector;

    public PreProcessorPipeline(Detector detector) {
        assert detector != null;
        this.detector = detector;
    }
}
