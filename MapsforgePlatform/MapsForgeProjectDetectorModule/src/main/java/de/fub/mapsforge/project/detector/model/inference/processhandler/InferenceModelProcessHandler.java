/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;

/**
 *
 * @author Serdar
 */
public abstract class InferenceModelProcessHandler {

    private final AbstractInferenceModel inferenceModel;

    public InferenceModelProcessHandler(AbstractInferenceModel inferenceModel) {
        this.inferenceModel = inferenceModel;
    }

    protected AbstractInferenceModel getInferenceModel() {
        return inferenceModel;
    }

    public void start() {
        handle();
    }

    protected abstract void handle();
}
