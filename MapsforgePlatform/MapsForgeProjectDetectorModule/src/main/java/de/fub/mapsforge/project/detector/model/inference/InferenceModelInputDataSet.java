/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Serdar
 */
public class InferenceModelInputDataSet {

    // map for the trainings dataset. the key ist the name of the transport mode and the value is a list of gpx files
    private final HashMap<String, HashSet<FileObject>> trainingsSet = new HashMap<String, HashSet<FileObject>>();
    // set of gpx files
    private final HashSet<FileObject> inferenceSet = new HashSet<FileObject>();

    public InferenceModelInputDataSet() {
    }

    public InferenceModelInputDataSet(Map<String, HashSet<FileObject>> trainingsSet, Set<FileObject> inferenceSet) {
        this.trainingsSet.putAll(trainingsSet);
        this.inferenceSet.addAll(inferenceSet);
    }

    private HashMap<String, HashSet<FileObject>> getTrainingsSet(String transportModeName) {
        if (!trainingsSet.containsKey(transportModeName)) {
            trainingsSet.put(transportModeName, new HashSet<FileObject>());
        }
        return trainingsSet;
    }

    public void putTrainingData(String transportModeName, FileObject gpxFile) {
        getTrainingsSet(transportModeName).get(transportModeName).add(gpxFile);
    }

    public void putTrainingsData(String transportModeName, Collection<FileObject> gpxFiles) {
        getTrainingsSet(transportModeName).get(transportModeName).addAll(gpxFiles);
    }

    public void clearTrainingsData(String transportModeName) {
        getTrainingsSet(transportModeName).clear();
    }

    public void clearAllTrainingsData() {
        trainingsSet.clear();
    }

    public void putInferenceData(FileObject gpxFile) {
        inferenceSet.add(gpxFile);
    }

    public void putInferenceData(Collection<FileObject> gpxfiles) {
        inferenceSet.addAll(gpxfiles);
    }

    public void clearAllInferenceData() {
        inferenceSet.clear();
    }

    public HashMap<String, HashSet<FileObject>> getTrainingsSet() {
        return trainingsSet;
    }

    public HashSet<FileObject> getInferenceSet() {
        return inferenceSet;
    }
}
