/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors;

import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapsforge.project.detector.model.Detector;

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
