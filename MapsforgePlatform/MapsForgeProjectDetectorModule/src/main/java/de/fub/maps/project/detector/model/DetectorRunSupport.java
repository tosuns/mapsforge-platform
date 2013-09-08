/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model;

import de.fub.maps.project.api.process.ProcessState;
import de.fub.maps.project.detector.DetectorMode;
import de.fub.maps.project.detector.model.converter.DataConverter;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.maps.project.detector.model.inference.InferenceModelInputDataSet;
import de.fub.maps.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.maps.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.xmls.DataSet;
import de.fub.maps.project.detector.model.xmls.Profile;
import de.fub.maps.project.detector.model.xmls.TransportMode;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * The controller class of a Detector. This class provides the logic for the
 * process execution of a Detector.
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Trainings_Process_Running=Training",
    "CLT_Inference_Process_Running=Clustering"
})
class DetectorRunSupport {

    private final Detector detector;
    private Profile currentProfile;
    private final ArrayList<DataConverter> converterList;
    private static final Logger LOG = Logger.getLogger(DetectorRunSupport.class.getName());

    public DetectorRunSupport(Detector detector) {
        assert detector != null;
        this.detector = detector;
        this.currentProfile = detector.getCurrentActiveProfile();
        this.converterList = new ArrayList<DataConverter>(Lookup.getDefault().lookupResult(DataConverter.class).allInstances());
    }

    /**
     * Starts the the classification process.
     */
    void start() {
        this.currentProfile = detector.getCurrentActiveProfile();
        final ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), ""));
        try {
            detector.getInferenceModel().getResult().clear();
            handle.start();
            detector.setDetectorState(ProcessState.RUNNING);

            switch (detector.getInferenceModel().getInferenceMode()) {
                case TRAININGS_MODE:
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Trainings_Process_Running()));
                    startTraining();
                    break;
                case INFERENCE_MODE:
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Inference_Process_Running()));
                    startInference();
                    break;
                case ALL_MODE:
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Trainings_Process_Running()));
                    startTraining();
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Inference_Process_Running()));
                    startInference();
                    break;
                default:
                    break;
            }
            detector.setDetectorState(ProcessState.INACTIVE);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            detector.setDetectorState(ProcessState.ERROR);
        } finally {
            // clean up
            detector.getInferenceModel().getResult().clear();
            detector.getInferenceModel().getInput().clearAllInferenceData();
            detector.getInferenceModel().getInput().clearAllTrainingsData();
            detector.getInferenceSet().clear();
            detector.getTrainingsSet().clear();
            handle.finish();
        }
    }

    /**
     * Starts the trainigns process.
     */
    private void startTraining() {
        Map<String, List<TrackSegment>> trainingsMap = getTrainingsSet();

        if (this.currentProfile.getPreprocess().isActive()
                && (this.currentProfile.getPreprocess().getMode() == DetectorMode.TRAINING
                || this.currentProfile.getPreprocess().getMode() == DetectorMode.BOTH)) {
            final ProgressHandle filterHandle = ProgressHandleFactory.createHandle("Applying Preprocessors...");
            Set<Entry<String, List<TrackSegment>>> entrySet = trainingsMap.entrySet();
            filterHandle.start(entrySet.size());
            int index = 0;
            try {
                for (Entry<String, List<TrackSegment>> entry : entrySet) {
                    List<TrackSegment> tracks = entry.getValue();
                    // TODO could lead to an infinity loop or to a concurrent modification exception!
                    trainingsMap.put(entry.getKey(), applyPreProcessors(tracks));
                    filterHandle.progress(index++);
                }
            } finally {
                filterHandle.finish();
            }
        }

        // convert GPSTracks to fileObjects
        InferenceModelInputDataSet inferenceModelInputDataSet = new InferenceModelInputDataSet();
        for (Entry<String, List<TrackSegment>> entry : trainingsMap.entrySet()) {
            inferenceModelInputDataSet.putTrainingsData(entry.getKey(), entry.getValue());
        }

        detector.getInferenceModel().setInput(inferenceModelInputDataSet);

        // start training & crossvalidation
        detector.getInferenceModel().setInferenceMode(InferenceMode.TRAININGS_MODE);
        detector.getInferenceModel().run();

    }

    /**
     * Starts the inference process.
     */
    private void startInference() {

        List<TrackSegment> inferenceSet = getInferenceSet();

        // apply pre precossers
        if (this.currentProfile.getPreprocess().isActive()
                && (this.currentProfile.getPreprocess().getMode() == DetectorMode.INFERENCE
                || this.currentProfile.getPreprocess().getMode() == DetectorMode.BOTH)) {

            inferenceSet = applyPreProcessors(inferenceSet);
        }

        InferenceModelInputDataSet inferenceModelInputDataSet = detector.getInferenceModel().getInput();

        if (inferenceModelInputDataSet == null) {
            inferenceModelInputDataSet = new InferenceModelInputDataSet();
        }

        inferenceModelInputDataSet.getInferenceSet().clear();
        inferenceModelInputDataSet.getInferenceSet().addAll(inferenceSet);

        detector.getInferenceModel().setInput(inferenceModelInputDataSet);

        // set state of inference model to inference
        detector.getInferenceModel().setInferenceMode(InferenceMode.INFERENCE_MODE);
        detector.getInferenceModel().run();

        // apply post processors
        if (this.currentProfile.getPostprocess().isActive()
                && (this.currentProfile.getPostprocess().getMode() == DetectorMode.INFERENCE
                || this.currentProfile.getPostprocess().getMode() == DetectorMode.BOTH)) {

            InferenceModelResultDataSet resultDataset = detector.getInferenceModel().getResult();

            if (resultDataset != null) {
                for (Task task : detector.getPostProcessorPipeline().getProcesses()) {
                    // maybe here is a concurrent execution possible
                    task.setInput(resultDataset);
                    task.run();
                }
            }
        }

    }

    /**
     * Applies all pre processors on the provided dataset.
     *
     * @param dataset a List of Tracksegments, null not allowed.
     * @return a list of Tracksegments.
     */
    private List<TrackSegment> applyPreProcessors(List<TrackSegment> dataset) {
        List<TrackSegment> gpsTracks = new ArrayList<TrackSegment>();
        List<TrackSegment> tracks = dataset;
        for (FilterProcess filterProcess : detector.getPreProcessorPipeline().getProcesses()) {
            if (filterProcess.getScope() == InferenceMode.ALL_MODE
                    || filterProcess.getScope() == detector.getInferenceModel().getInferenceMode()) {
                filterProcess.setInput(tracks);
                filterProcess.run();
                tracks = filterProcess.getResult();
            } else {
                LOG.log(Level.INFO, "Scope of filter {0} does not match to current inference mode {1}. Skiping filter!",
                        new Object[]{filterProcess.getName(), detector.getInferenceModel().getInferenceMode().toString()});
            }
        }

        gpsTracks.addAll(tracks);
        return gpsTracks;
    }

    /**
     * Returns the trainingset, which is provided via the DetectorDescriptor.
     *
     * @return a map with the Data for each class.
     */
    public Map<String, List<TrackSegment>> getTrainingsSet() {
        HashMap<String, List<TrackSegment>> trainingsSet = new HashMap<String, List<TrackSegment>>();
        for (TransportMode transportMode : detector.getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList()) {
            if (!trainingsSet.containsKey(transportMode.getName())) {
                trainingsSet.put(transportMode.getName(), new ArrayList<TrackSegment>());
            }
            for (DataSet dataSet : transportMode.getDataset()) {
                if (dataSet.getUrl() != null) {
                    File file = new File(dataSet.getUrl());
                    File normalizedFile = FileUtil.normalizeFile(file);
                    FileObject datasetFileObject = FileUtil.toFileObject(normalizedFile);
                    if (datasetFileObject != null && datasetFileObject.isValid()) {
                        try {
                            List<TrackSegment> gpsTrackList = convertFileObject(datasetFileObject);
                            trainingsSet.get(transportMode.getName()).addAll(gpsTrackList);
                        } catch (DataConverter.DataConverterException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }

        }
        return trainingsSet;
    }

    /**
     *
     * @return
     */
    public List<TrackSegment> getInferenceSet() {
        List<TrackSegment> inferenceSet = new ArrayList<TrackSegment>();

        for (DataSet dataset : detector.getDetectorDescriptor().getDatasets().getInferenceSet().getDatasetList()) {
            File file = new File(dataset.getUrl());
            File normalizeFile = FileUtil.normalizeFile(file);
            FileObject datasetFileObject = FileUtil.toFileObject(normalizeFile);
            if (datasetFileObject != null && datasetFileObject.isValid()) {
                try {
                    List<TrackSegment> dataList = convertFileObject(datasetFileObject);
                    inferenceSet.addAll(dataList);
                } catch (DataConverter.DataConverterException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        return inferenceSet;
    }

    /**
     *
     * @param fileObject
     * @return
     * @throws
     * de.fub.mapsforge.project.detector.model.converter.DataConverter.DataConverterException
     */
    private List<TrackSegment> convertFileObject(FileObject fileObject) throws DataConverter.DataConverterException {
        List<TrackSegment> gpsTrackList = new ArrayList<TrackSegment>();
        for (DataConverter converter : converterList) {
            if (converter.isFileTypeSupported(fileObject)) {
                gpsTrackList = converter.convert(fileObject);
                break;
            }
        }
        return gpsTrackList;
    }
}
