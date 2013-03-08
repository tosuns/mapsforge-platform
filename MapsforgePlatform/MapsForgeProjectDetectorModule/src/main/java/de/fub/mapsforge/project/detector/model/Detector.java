/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.detector.filetype.DetectorDataObject;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.PostProcessorPipeline;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.PreProcessorPipeline;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.Task;
import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.PreProcessors;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class Detector extends ModelSynchronizer {

    public static final String PROP_NAME_DETECTOR_STATE = "detector.state";
    private static final Logger LOG = Logger.getLogger(Detector.class.getName());
    private final DetectorDataObject dataObject;
    private final PreProcessorPipeline preProcessorPipeline = new PreProcessorPipeline(this);
    private final PostProcessorPipeline postProcessorPipeline = new PostProcessorPipeline(this);
    private final Object MUTEX_PROCESS_RUNNING = new Object();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private AbstractInferenceModel inferenceModel;
    private ProcessState detectorState = ProcessState.INACTIVE;
    private ModelSynchronizerClient dataObjectModelSynchronizerClient;

    public Detector(DetectorDataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        init();
    }

    private void init() {
        // a dummy client to differenciate File change from the file system
        dataObjectModelSynchronizerClient = super.create(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // do nothing
            }
        });

        this.dataObject.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                reinit();
                dataObjectModelSynchronizerClient.modelChanged();
            }
        });
        reinit();
    }

    // TODO initialized this instance with the help of the detector descriptor.
    private void reinit() {
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {
            InferenceModelDescriptor inferenceModelDescriptor = detectorDescriptor.getInferenceModel();
            if (inferenceModelDescriptor != null) {
                inferenceModel = DetectorUtils.createInferenceModel(inferenceModelDescriptor, Detector.this);
            }

            PreProcessors preprocessors = detectorDescriptor.getPreprocessors();
            if (preprocessors != null) {
                FilterProcess filter = null;
                for (ProcessDescriptor processDescriptor : preprocessors.getPreprocessorList()) {
                    filter = DetectorUtils.createInstance(FilterProcess.class, processDescriptor.getJavaType());
                    if (filter != null) {
                        getPreProcessorPipeline().add(filter);
                    }
                }
            }
//            PostProcessors postprocessors = detectorDescriptor.getPostprocessors();
//            if (postprocessors != null) {
//                Task task = null;
//                for (ProcessDescriptor processDescriptor : postprocessors.getPostprocessorList()) {
//                    task = DetectorUtils.createInstance(Task.class, processDescriptor.getJavaType());
//                    if (task != null) {
//                        getPostProcessorPipeline().add(task);
//                    }
//                }
//            }
        }
    }

    @NbBundle.Messages({
        "# {0} - detectorName",
        "# {1} - processName",
        "CLT_Running_Process={0}: Running {1}..."})
    public void start() {
        synchronized (MUTEX_PROCESS_RUNNING) {
            final ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Running_Process(getDetectorDescriptor().getName(), ""), new CancellableImpl());
            try {
            } finally {
                handle.finish();
            }
        }
    }

    public PreProcessorPipeline getPreProcessorPipeline() {
        return preProcessorPipeline;
    }

    public PostProcessorPipeline getPostProcessorPipeline() {
        return postProcessorPipeline;
    }

    public AbstractInferenceModel getInferenceModel() {
        return inferenceModel;
    }

    public synchronized ProcessState getDetectorState() {
        return detectorState;
    }

    public synchronized void setDetectorState(ProcessState detectorState) {
        Object oldValue = this.detectorState;
        this.detectorState = detectorState;
        pcs.firePropertyChange(PROP_NAME_DETECTOR_STATE, oldValue, detectorState);
    }

    public DetectorDataObject getDataObject() {
        return dataObject;
    }

    public DetectorDescriptor getDetectorDescriptor() {
        try {
            return dataObject.getDetectorDescriptor();
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public void notifyModified() {
        dataObject.modifySourceEditor();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public List<StatisticProvider> getStatistics() {
        List<StatisticProvider> statisticProviders = new ArrayList<StatisticProvider>();
        for (FilterProcess<?, ?> process : getPreProcessorPipeline().getProcesses()) {
            if (process instanceof StatisticProvider) {
                statisticProviders.add(((StatisticProvider) process));
            }
        }
        for (Task<?, ?> process : getPostProcessorPipeline().getProcesses()) {
            if (process instanceof StatisticProvider) {
                statisticProviders.add(((StatisticProvider) process));
            }
        }
        return statisticProviders;
    }

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
