/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.gpxmodule.xml.gpx.Gpx;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Serdar
 */
public class InferenceModelInputDataSet {

    // map for the trainings dataset. the key ist the name of the transport mode and the value is a list of gpx files
    private final HashMap<String, HashSet<Gpx>> trainingsSet = new HashMap<String, HashSet<Gpx>>();
    // set of gpx files
    private final HashSet<Gpx> inferenceSet = new HashSet<Gpx>();

    public InferenceModelInputDataSet() {
    }

    public InferenceModelInputDataSet(Map<String, HashSet<Gpx>> trainingsSet, Set<Gpx> inferenceSet) {
        this.trainingsSet.putAll(trainingsSet);
        this.inferenceSet.addAll(inferenceSet);
    }

    private HashMap<String, HashSet<Gpx>> getTrainingsSet(String transportModeName) {
        if (!trainingsSet.containsKey(transportModeName)) {
            trainingsSet.put(transportModeName, new HashSet<Gpx>());
        }
        return trainingsSet;
    }

    public void putTrainingData(String transportModeName, Gpx gpxFile) {
        getTrainingsSet(transportModeName).get(transportModeName).add(gpxFile);
    }

    public void putTrainingsData(String transportModeName, Collection<Gpx> gpxFiles) {
        getTrainingsSet(transportModeName).get(transportModeName).addAll(gpxFiles);
    }

    public void clearTrainingsData(String transportModeName) {
        getTrainingsSet(transportModeName).clear();
    }

    public void clearAllTrainingsData() {
        trainingsSet.clear();
    }

    public void putInferenceData(Gpx gpxFile) {
        inferenceSet.add(gpxFile);
    }

    public void putInferenceData(Collection<Gpx> gpxfiles) {
        inferenceSet.addAll(gpxfiles);
    }

    public void clearAllInferenceData() {
        inferenceSet.clear();
    }

    public HashMap<String, HashSet<Gpx>> getTrainingsSet() {
        return trainingsSet;
    }

    public HashSet<Gpx> getInferenceSet() {
        return inferenceSet;
    }
}
