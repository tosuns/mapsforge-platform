/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.DetectorMode;
import de.fub.mapsforge.project.detector.model.converter.DataConverter;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelInputDataSet;
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
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
class DetectorRunController {

    private final Detector detector;
    private final Profile currentProfile;
    private final ArrayList<DataConverter> converterList;

    public DetectorRunController(Detector detector) {
        assert detector != null;
        this.detector = detector;
        this.currentProfile = detector.getCurrentActiveProfile();
        this.converterList = new ArrayList<DataConverter>(Lookup.getDefault().lookupResult(DataConverter.class).allInstances());
    }

    void start() {
        final ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Running_Process(detector.getDetectorDescriptor().getName(), ""), new CancellableImpl());
        try {
            detector.setDetectorState(ProcessState.RUNNING);

            switch (detector.getInferenceModel().getInferenceMode()) {
                case TRAININGS_MODE:
                    startTraining();
                    break;
                case INFERENCE_MODE:
                    startInference();
                    break;
                case ALL_MODE:
                    startTraining();
                    startInference();
                    break;
            }

        } catch (Exception ex) {
            detector.setDetectorState(ProcessState.ERROR);
        } finally {
            handle.finish();
            detector.setDetectorState(ProcessState.INACTIVE);
        }
    }

    @SuppressWarnings("unchecked")
    private void startTraining() {// 1. Get datasets (trainingsset and inference set
        // 2. depending on the parameter value, whether
        // preprocessors should be applied to the current data set
        // apply (or not) preprocessors
        // 3. fout possible cases to run this detector are given.
        // 3.1 traninigs mode.
        Map<String, List<GPSTrack>> trainingsMap = getTrainingsSet();

        if ((this.currentProfile.getPreprocess().getMode() == DetectorMode.TRAINING
                || this.currentProfile.getPreprocess().getMode() == DetectorMode.BOTH)
                && this.currentProfile.getPreprocess().isActive()) {

            List<GPSTrack> gpsTracks = new ArrayList<GPSTrack>();

            for (Entry<String, List<GPSTrack>> entry : trainingsMap.entrySet()) {

                List<GPSTrack> tracks = entry.getValue();
                for (FilterProcess filterProcess : detector.getPreProcessorPipeline().getProcesses()) {
                    filterProcess.setInput(tracks);
                    filterProcess.run();
                    tracks = filterProcess.getResult();
                }

                gpsTracks.addAll(tracks);
                // TOCO could lead to an infinity loop or to a concurrent modification exception!
                trainingsMap.put(entry.getKey(), gpsTracks);
            }
        }

        // convert GPSTracks to fileObjects
        InferenceModelInputDataSet inferenceModelInputDataSet = new InferenceModelInputDataSet();
        for (Entry<String, List<GPSTrack>> entry : trainingsMap.entrySet()) {
            inferenceModelInputDataSet.putTrainingsData(entry.getKey(), entry.getValue());
        }

        detector.getInferenceModel().setInput(inferenceModelInputDataSet);
        detector.getInferenceModel().setInferenceMode(InferenceMode.TRAININGS_MODE);
        detector.getInferenceModel().run();

    }

    private void startInference() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Map<String, List<GPSTrack>> getTrainingsSet() {
        HashMap<String, List<GPSTrack>> trainingsSet = new HashMap<String, List<GPSTrack>>();
        for (TransportMode transportMode : detector.getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList()) {
            if (!trainingsSet.containsKey(transportMode.getName())) {
                trainingsSet.put(transportMode.getName(), new ArrayList<GPSTrack>());
            }
            for (DataSet dataSet : transportMode.getDataset()) {
                FileObject datasetFileObject = DetectorUtils.findFileObject(detector.getDataObject().getPrimaryFile(), dataSet.getUrl());
                if (datasetFileObject != null) {
                    try {
                        List<GPSTrack> gpsTrackList = convertFileObject(datasetFileObject);
                        trainingsSet.get(transportMode.getName()).addAll(gpsTrackList);
                    } catch (DataConverter.DataConverterException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        }
        return trainingsSet;
    }

    private List<GPSTrack> convertFileObject(FileObject fileObject) throws DataConverter.DataConverterException {
        List<GPSTrack> gpsTrackList = new ArrayList<GPSTrack>();
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
