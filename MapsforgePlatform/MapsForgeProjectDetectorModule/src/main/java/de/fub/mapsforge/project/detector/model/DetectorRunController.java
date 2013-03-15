/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.detector.DetectorMode;
import de.fub.mapsforge.project.detector.model.converter.DataConverter;
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
    "CLT_Trainings_Process_Running=Running Training...",
    "CLT_Inference_Process_Running=Running Clustering..."
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
        final ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Trainings_Process_Running());
        final ProgressHandle filterHandle = ProgressHandleFactory.createHandle("Applying Preprocessors...");
        try {
            handle.start();
            Map<String, List<Gpx>> trainingsMap = getTrainingsSet();

            if (this.currentProfile.getPreprocess().isActive()
                    && (this.currentProfile.getPreprocess().getMode() == DetectorMode.TRAINING
                    || this.currentProfile.getPreprocess().getMode() == DetectorMode.BOTH)) {
                Set<Entry<String, List<Gpx>>> entrySet = trainingsMap.entrySet();
                filterHandle.start(entrySet.size());
                int index = 0;
                for (Entry<String, List<Gpx>> entry : entrySet) {
                    List<Gpx> tracks = entry.getValue();
                    // TOCO could lead to an infinity loop or to a concurrent modification exception!
                    trainingsMap.put(entry.getKey(), applyPreProcessors(tracks));
                    filterHandle.progress(index++);
                }
                filterHandle.finish();
            }

            // convert GPSTracks to fileObjects
            InferenceModelInputDataSet inferenceModelInputDataSet = new InferenceModelInputDataSet();
            for (Entry<String, List<Gpx>> entry : trainingsMap.entrySet()) {
                inferenceModelInputDataSet.putTrainingsData(entry.getKey(), entry.getValue());
            }

            detector.getInferenceModel().setInput(inferenceModelInputDataSet);

            // start training & crossvalidation
            detector.getInferenceModel().setInferenceMode(InferenceMode.TRAININGS_MODE);
            detector.getInferenceModel().run();
        } finally {
            filterHandle.finish();
            handle.finish();
        }
    }

    /**
     *
     */
    private void startInference() {
        final ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Trainings_Process_Running());
        try {
            handle.start();

            List<Gpx> inferenceSet = getInferenceSet();

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
                    }
                }
            }
        } finally {
            handle.finish();
        }
    }

    /**
     *
     * @param dataset
     * @return
     */
    private List<Gpx> applyPreProcessors(List<Gpx> dataset) {
        List<Gpx> gpsTracks = new ArrayList<Gpx>();
        List<Gpx> tracks = dataset;
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
    private Map<String, List<Gpx>> getTrainingsSet() {
        HashMap<String, List<Gpx>> trainingsSet = new HashMap<String, List<Gpx>>();
        for (TransportMode transportMode : detector.getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList()) {
            if (!trainingsSet.containsKey(transportMode.getName())) {
                trainingsSet.put(transportMode.getName(), new ArrayList<Gpx>());
            }
            for (DataSet dataSet : transportMode.getDataset()) {
                FileObject datasetFileObject = DetectorUtils.findFileObject(detector.getDataObject().getPrimaryFile(), dataSet.getUrl());
                if (datasetFileObject != null && datasetFileObject.isValid()) {
                    try {
                        List<Gpx> gpsTrackList = convertFileObject(datasetFileObject);
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
    private List<Gpx> getInferenceSet() {
        List<Gpx> inferenceSet = new ArrayList<Gpx>();

        for (DataSet dataset : detector.getDetectorDescriptor().getDatasets().getInferenceSet().getDatasetList()) {
            FileObject datasetFileObject = DetectorUtils.findFileObject(detector.getDataObject().getPrimaryFile(), dataset.getUrl());
            if (datasetFileObject != null && datasetFileObject.isValid()) {
                try {
                    List<Gpx> dataList = convertFileObject(datasetFileObject);
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
    private List<Gpx> convertFileObject(FileObject fileObject) throws DataConverter.DataConverterException {
        List<Gpx> gpsTrackList = new ArrayList<Gpx>();
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
