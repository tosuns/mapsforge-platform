/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapforgeproject.api.process.AbstractProcess;

/**
 *
 * @author Serdar
 */
public abstract class AbstractInferenceModel extends AbstractProcess<Object, Map<String, File> {

    private final Detector detector;

    public AbstractInferenceModel(Detector detector) {
        assert detector != null;
        this.detector = detector;
    }
}
