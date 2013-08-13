/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference;

import de.fub.maps.project.detector.model.gpx.TrackSegment;
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
    private final HashMap<String, HashSet<TrackSegment>> trainingsSet = new HashMap<String, HashSet<TrackSegment>>();
    // set of gpx files
    private final HashSet<TrackSegment> inferenceSet = new HashSet<TrackSegment>();

    public InferenceModelInputDataSet() {
    }

    public InferenceModelInputDataSet(Map<String, HashSet<TrackSegment>> trainingsSet, Set<TrackSegment> inferenceSet) {
        this.trainingsSet.putAll(trainingsSet);
        this.inferenceSet.addAll(inferenceSet);
    }

    private HashMap<String, HashSet<TrackSegment>> getTrainingsSet(String transportModeName) {
        if (!trainingsSet.containsKey(transportModeName)) {
            trainingsSet.put(transportModeName, new HashSet<TrackSegment>());
        }
        return trainingsSet;
    }

    public void putTrainingData(String transportModeName, TrackSegment gpxFile) {
        getTrainingsSet(transportModeName).get(transportModeName).add(gpxFile);
    }

    public void putTrainingsData(String transportModeName, Collection<TrackSegment> gpxFiles) {
        getTrainingsSet(transportModeName).get(transportModeName).addAll(gpxFiles);
    }

    public void clearTrainingsData(String transportModeName) {
        getTrainingsSet(transportModeName).clear();
    }

    public void clearAllTrainingsData() {
        trainingsSet.clear();
    }

    public void putInferenceData(TrackSegment gpxFile) {
        inferenceSet.add(gpxFile);
    }

    public void putInferenceData(Collection<TrackSegment> gpxfiles) {
        inferenceSet.addAll(gpxfiles);
    }

    public void clearAllInferenceData() {
        inferenceSet.clear();
    }

    public HashMap<String, HashSet<TrackSegment>> getTrainingsSet() {
        return trainingsSet;
    }

    public HashSet<TrackSegment> getInferenceSet() {
        return inferenceSet;
    }
}
