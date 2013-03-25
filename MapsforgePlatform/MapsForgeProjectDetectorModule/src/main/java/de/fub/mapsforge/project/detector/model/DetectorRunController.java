/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.DetectorMode;
import de.fub.mapsforge.project.detector.model.converter.DataConverter;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelInputDataSet;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.Task;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import de.fub.mapsforge.project.detector.model.xmls.Profile;
import de.fub.mapsforge.project.detector.model.xmls.TransportMode;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
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
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Trainings_Process_Running=Training",
    "CLT_Inference_Process_Running=Clustering"
})
class DetectorRunController {

    private final Detector detector;
    private final Profile currentProfile;
    private final ArrayList<DataConverter> converterList;
    private static final Logger LOG = Logger.getLogger(DetectorRunController.class.getName());

    public DetectorRunController(Detector detector) {
        assert detector != null;
        this.detector = detector;
        this.currentProfile = detector.getCurrentActiveProfile();
        this.converterList = new ArrayList<DataConverter>(Lookup.getDefault().lookupResult(DataConverter.class).allInstances());
    }

    /**
     *
     */
    void start() {
        final ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), ""), new CancellableImpl());
        try {
            handle.start();
            detector.setDetectorState(ProcessState.RUNNING);

            switch (detector.getInferenceModel().getInferenceMode()) {
                case TRAININGS_MODE:
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Trainings_Process_Running()));
                    startTraining();
                    break;
                case INFERENCE_MODE:
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Trainings_Process_Running()));
                    startInference();
                    break;
                case ALL_MODE:
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Trainings_Process_Running()));
                    startTraining();
                    handle.setDisplayName(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), Bundle.CLT_Trainings_Process_Running()));
                    startInference();
                    break;
            }
            detector.setDetectorState(ProcessState.INACTIVE);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            detector.setDetectorState(ProcessState.ERROR);
        } finally {
            handle.finish();
        }
    }

    /**
     *
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
                    // TOCO could lead to an infinity loop or to a concurrent modification exception!
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
     *
     */
    private void startInference() {

        try {

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
        } finally {
        }
    }

    /**
     *
     * @param dataset
     * @return
     */
    private List<TrackSegment> applyPreProcessors(List<TrackSegment> dataset) {
        List<TrackSegment> gpsTracks = new ArrayList<TrackSegment>();
        List<TrackSegment> tracks = dataset;
        for (FilterProcess filterProcess : detector.getPreProcessorPipeline().getProcesses()) {
            filterProcess.setInput(tracks);
            filterProcess.run();
            tracks = filterProcess.getResult();
        }

        gpsTracks.addAll(tracks);
        return gpsTracks;
    }

    /**
     *
     * @return
     */
    private Map<String, List<TrackSegment>> getTrainingsSet() {
        HashMap<String, List<TrackSegment>> trainingsSet = new HashMap<String, List<TrackSegment>>();
        for (TransportMode transportMode : detector.getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList()) {
            if (!trainingsSet.containsKey(transportMode.getName())) {
                trainingsSet.put(transportMode.getName(), new ArrayList<TrackSegment>());
            }
            for (DataSet dataSet : transportMode.getDataset()) {
                FileObject datasetFileObject = DetectorUtils.findFileObject(detector.getDataObject().getPrimaryFile(), dataSet.getUrl());
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
        return trainingsSet;
    }

    /**
     *
     * @return
     */
    private List<TrackSegment> getInferenceSet() {
        List<TrackSegment> inferenceSet = new ArrayList<TrackSegment>();

        for (DataSet dataset : detector.getDetectorDescriptor().getDatasets().getInferenceSet().getDatasetList()) {
            FileObject datasetFileObject = DetectorUtils.findFileObject(detector.getDataObject().getPrimaryFile(), dataset.getUrl());
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

    /**
     *
     */
    private static class CancellableImpl implements Cancellable {

        public CancellableImpl() {
        }

        @Override
        public boolean cancel() {
            // TODO cancel process.
            return false;

        }
    }
}
