/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.postprocessors;

import de.fub.mapsforge.project.detector.model.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;

/**
 *
 * @author Serdar
 */
public abstract class Task extends AbstractDetectorProcess<InferenceModelResultDataSet, Void> {

    private InferenceModelResultDataSet resultDataSet;

    public Task() {
        super(null);
    }

    public Task(Detector detector) {
        super(detector);
    }

    @Override
    public void setInput(InferenceModelResultDataSet input) {
        this.resultDataSet = input;
    }

    protected InferenceModelResultDataSet getResultDataSet() {
        return resultDataSet;
    }

    @Override
    public Void getResult() {
        return null;
    }

    @Override
    public boolean cancel() {
        return false;
    }
}
