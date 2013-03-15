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
import de.fub.mapsforge.project.detector.model.xmls.PostProcessors;
import de.fub.mapsforge.project.detector.model.xmls.PreProcessors;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Profile;
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
    private Profile currentActiveProfile = null;

    public Detector(DetectorDataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        init();
    }

    /**
     *
     */
    private void init() {
        // a client to delegate a file change listener
        // to all other clients to update their model
        dataObjectModelSynchronizerClient = super.create(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // for the editor
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

    /**
     *
     */
    private void reinit() {
        setDetectorState(ProcessState.INACTIVE);
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {

            // initialize the inference model
            InferenceModelDescriptor inferenceModelDescriptor = detectorDescriptor.getInferenceModel();
            if (inferenceModelDescriptor != null) {
                inferenceModel = DetectorUtils.createInferenceModel(inferenceModelDescriptor, Detector.this);
            }

            // initialize the preprocessors
            PreProcessors preprocessors = detectorDescriptor.getPreprocessors();
            if (preprocessors != null) {
                FilterProcess filter = null;
                getPreProcessorPipeline().clear();
                for (ProcessDescriptor processDescriptor : preprocessors.getPreprocessorList()) {
                    filter = DetectorUtils.createInstance(FilterProcess.class, processDescriptor.getJavaType());
                    if (filter != null) {
                        getPreProcessorPipeline().add(filter);
                    }
                }
            }

            // initialize the postprocessors
            PostProcessors postprocessors = detectorDescriptor.getPostprocessors();
            if (postprocessors != null) {
                Task task = null;
                getPostProcessorPipeline().clear();
                for (ProcessDescriptor processDescriptor : postprocessors.getPostprocessorList()) {
                    task = DetectorUtils.createInstance(Task.class, processDescriptor.getJavaType());
                    if (task != null) {
                        getPostProcessorPipeline().add(task);
                    }
                }
            }

            // determine active profile
            if (!detectorDescriptor.getProfiles().getProfileList().isEmpty()) {
                if (detectorDescriptor.getProfiles() == null) {
                    currentActiveProfile = detectorDescriptor.getProfiles().getProfileList().iterator().next();
                } else {

                    for (Profile profile : detectorDescriptor.getProfiles().getProfileList()) {
                        if (profile.getName() != null && detectorDescriptor.getProfiles().getActiveProfile().equals(profile.getName())) {
                            currentActiveProfile = profile;
                            break;
                        }
                    }
                    if (currentActiveProfile == null) {
                        currentActiveProfile = detectorDescriptor.getProfiles().getProfileList().iterator().next();
                    }
                }
            }
        } else {
            setDetectorState(ProcessState.ERROR);
        }
    }

    /**
     *
     */
    @NbBundle.Messages({
        "# {0} - detectorName",
        "# {1} - processName",
        "CLT_Running_Process={0}: Running {1}..."})
    public void start() {
        synchronized (MUTEX_PROCESS_RUNNING) {
            new DetectorRunController(Detector.this).start();
        }
    }

    /**
     *
     * @return
     */
    public Profile getCurrentActiveProfile() {
        return currentActiveProfile;
    }

    /**
     *
     * @param currentActiveProfile
     */
    public void setCurrentActiveProfile(Profile currentActiveProfile) {
        if (currentActiveProfile != null) {
            getDetectorDescriptor().getProfiles().setActiveProfile(currentActiveProfile.getName());
            this.currentActiveProfile = currentActiveProfile;
        }
    }

    /**
     *
     * @return
     */
    public PreProcessorPipeline getPreProcessorPipeline() {
        return preProcessorPipeline;
    }

    /**
     *
     * @return
     */
    public PostProcessorPipeline getPostProcessorPipeline() {
        return postProcessorPipeline;
    }

    /**
     *
     * @return
     */
    public AbstractInferenceModel getInferenceModel() {
        return inferenceModel;
    }

    /**
     *
     * @return
     */
    public synchronized ProcessState getDetectorState() {
        return detectorState;
    }

    /**
     *
     * @param detectorState
     */
    synchronized void setDetectorState(ProcessState detectorState) {
        Object oldValue = this.detectorState;
        this.detectorState = detectorState;
        pcs.firePropertyChange(PROP_NAME_DETECTOR_STATE, oldValue, detectorState);
    }

    /**
     *
     * @return
     */
    public DetectorDataObject getDataObject() {
        return dataObject;
    }

    /**
     *
     * @return
     */
    public DetectorDescriptor getDetectorDescriptor() {
        DetectorDescriptor detectorDescriptor = null;
        try {
            detectorDescriptor = dataObject.getDetectorDescriptor();
//            getDataObject().getNodeDelegate().setShortDescription(detectorDescriptor.getDescription());
        } catch (JAXBException ex) {
            setDetectorState(ProcessState.ERROR);
//            getDataObject().getNodeDelegate().setShortDescription(ex.getMessage());
        } catch (IOException ex) {
            setDetectorState(ProcessState.ERROR);
//            getDataObject().getNodeDelegate().setShortDescription(ex.getMessage());
        }
        return detectorDescriptor;
    }

    /**
     *
     */
    public void notifyModified() {
        dataObject.modifySourceEditor();
    }

    /**
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     *
     * @return
     */
    public List<StatisticProvider> getStatistics() {
        List<StatisticProvider> statisticProviders = new ArrayList<StatisticProvider>();
        for (FilterProcess process : getPreProcessorPipeline().getProcesses()) {
            if (process instanceof StatisticProvider) {
                statisticProviders.add(((StatisticProvider) process));
            }
        }
        for (Task process : getPostProcessorPipeline().getProcesses()) {
            if (process instanceof StatisticProvider) {
                statisticProviders.add(((StatisticProvider) process));
            }
        }
        return statisticProviders;
    }

    @Override
    public String toString() {
        return "Detector{ name=" + getDetectorDescriptor().getName() + ", description=" + getDetectorDescriptor().getDescription() + '}';
    }
}
