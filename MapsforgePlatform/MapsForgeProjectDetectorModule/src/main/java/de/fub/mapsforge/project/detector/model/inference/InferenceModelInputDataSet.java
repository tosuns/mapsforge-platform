/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.agg2graph.structs.GPSTrack;
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
    private final HashMap<String, HashSet<GPSTrack>> trainingsSet = new HashMap<String, HashSet<GPSTrack>>();
    // set of gpx files
    private final HashSet<GPSTrack> inferenceSet = new HashSet<GPSTrack>();

    public InferenceModelInputDataSet() {
    }

    public InferenceModelInputDataSet(Map<String, HashSet<GPSTrack>> trainingsSet, Set<GPSTrack> inferenceSet) {
        this.trainingsSet.putAll(trainingsSet);
        this.inferenceSet.addAll(inferenceSet);
    }

    private HashMap<String, HashSet<GPSTrack>> getTrainingsSet(String transportModeName) {
        if (!trainingsSet.containsKey(transportModeName)) {
            trainingsSet.put(transportModeName, new HashSet<GPSTrack>());
        }
        return trainingsSet;
    }

    public void putTrainingData(String transportModeName, GPSTrack gpxFile) {
        getTrainingsSet(transportModeName).get(transportModeName).add(gpxFile);
    }

    public void putTrainingsData(String transportModeName, Collection<GPSTrack> gpxFiles) {
        getTrainingsSet(transportModeName).get(transportModeName).addAll(gpxFiles);
    }

    public void clearTrainingsData(String transportModeName) {
        getTrainingsSet(transportModeName).clear();
    }

    public void clearAllTrainingsData() {
        trainingsSet.clear();
    }

    public void putInferenceData(GPSTrack gpxFile) {
        inferenceSet.add(gpxFile);
    }

    public void putInferenceData(Collection<GPSTrack> gpxfiles) {
        inferenceSet.addAll(gpxfiles);
    }

    public void clearAllInferenceData() {
        inferenceSet.clear();
    }

    public HashMap<String, HashSet<GPSTrack>> getTrainingsSet() {
        return trainingsSet;
    }

    public HashSet<GPSTrack> getInferenceSet() {
        return inferenceSet;
    }
}
