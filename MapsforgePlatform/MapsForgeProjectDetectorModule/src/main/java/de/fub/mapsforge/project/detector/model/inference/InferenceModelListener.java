/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import java.util.EventListener;

/**
 *
 * @author Serdar
 */
public interface InferenceModelListener extends EventListener {

    public void startedTraining();

    public void finishedTraining();

    public void startedCrossValidation();

    public void finishedCrossValidation();

    public void startedClustering();

    public void finishedClustering();
}
