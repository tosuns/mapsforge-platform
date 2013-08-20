/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model;

import de.fub.maps.project.api.process.ProcessState;
import de.fub.maps.project.api.statistics.StatisticProvider;
import de.fub.maps.project.detector.filetype.DetectorDataObject;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.maps.project.detector.model.pipeline.postprocessors.PostProcessorPipeline;
import de.fub.maps.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.maps.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.maps.project.detector.model.pipeline.preprocessors.PreProcessorPipeline;
import de.fub.maps.project.detector.model.process.DetectorProcess;
import de.fub.maps.project.detector.model.xmls.DetectorDescriptor;
import de.fub.maps.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.maps.project.detector.model.xmls.PostProcessors;
import de.fub.maps.project.detector.model.xmls.PreProcessors;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Profile;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
public class Detector extends ModelSynchronizer implements Lookup.Provider, TrainingsDataProvider, Cookie {

    public static final String PROP_NAME_DETECTOR_STATE = "detector.state";
    private static final Logger LOG = Logger.getLogger(Detector.class.getName());
    private DetectorDataObject dataObject;
    private final PreProcessorPipeline preProcessorPipeline = new PreProcessorPipeline(this);
    private final PostProcessorPipeline postProcessorPipeline = new PostProcessorPipeline(this);
    private final Object MUTEX_PROCESS_RUNNING = new Object();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private AbstractInferenceModel inferenceModel;
    private ProcessState detectorState = ProcessState.INACTIVE;
    private ModelSynchronizerClient dataObjectModelSynchronizerClient;
    private Profile currentActiveProfile = null;
    private DetectorRunSupport detectorRunSupport;
    private ProxyLookup lookup;

    public Detector(DetectorDataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        init();
    }

    /**
     *
     */
    private void init() {
        this.lookup = new ProxyLookup(Lookups.fixed(Detector.this, dataObject), dataObject.getLookup());
        this.dataObject.addChangeListener(new ChangeListenerImpl());
        reinit();
    }

    /**
     * Initialized the this Detector the underlying Descriptor.
     */
    private void reinit() {
        setDetectorState(ProcessState.INACTIVE);
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {

            // initialize the inference model
            initializeInferenceModel();

            // initialize the preprocessors
            initalizePreProcessors();

            // initialize the postprocessors
            initializePostProcessors();

            // determine active profile
            initializeProfile();
        } else {
            setDetectorState(ProcessState.ERROR);
        }
    }

    /**
     * Initializes the InferenceModel of this Detector.
     */
    private void initializeInferenceModel() {
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {

            // initialize the inference model
            InferenceModelDescriptor inferenceModelDescriptor = detectorDescriptor.getInferenceModel();
            if (inferenceModelDescriptor != null) {
                try {
                    inferenceModel = AbstractInferenceModel.find(inferenceModelDescriptor.getJavaType(), Detector.this);
                } catch (DetectorProcess.DetectorProcessNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    /**
     * initializes the pre processors, which are provided by the Descriptor, and
     * the pipeline.
     */
    private void initalizePreProcessors() {
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {
            // initialize the preprocessors
            PreProcessors preprocessors = detectorDescriptor.getPreprocessors();
            if (preprocessors != null) {
                FilterProcess filter = null;
                getPreProcessorPipeline().clear();
                for (ProcessDescriptor processDescriptor : preprocessors.getPreprocessorList()) {
                    try {
                        filter = FilterProcess.find(processDescriptor, Detector.this);
                        getPreProcessorPipeline().add(filter);
                    } catch (DetectorProcess.DetectorProcessNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    /**
     * Initializes the post processors and the pipeline.
     */
    private void initializePostProcessors() {
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {

            // initialize the postprocessors
            PostProcessors postprocessors = detectorDescriptor.getPostprocessors();
            if (postprocessors != null) {
                Task task = null;
                getPostProcessorPipeline().clear();
                for (ProcessDescriptor processDescriptor : postprocessors.getPostprocessorList()) {
                    try {
                        task = Task.find(processDescriptor, Detector.this);
                        getPostProcessorPipeline().add(task);
                    } catch (DetectorProcess.DetectorProcessNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    /**
     * Initializes the Profile.
     */
    private void initializeProfile() {
        DetectorDescriptor detectorDescriptor = getDetectorDescriptor();
        if (detectorDescriptor != null) {
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
        }
    }

    /**
     * Executes the Detector.
     */
    @NbBundle.Messages({
        "# {0} - detectorName",
        "# {1} - processName",
        "CLT_Running_Process={0}: Running {1}..."})
    public void start() {
        synchronized (MUTEX_PROCESS_RUNNING) {
            getDetectorRunSupport().start();
        }
    }

    @Override
    public Map<String, List<TrackSegment>> getData() {
        return getTrainingsSet();
    }

    /**
     * Returns the name of this Detector
     *
     * @return
     */
    @Override
    public String getName() {
        return Detector.this.getDataObject().getName();
    }

    /**
     * Convience method to access the underlying controller of the Detector.
     *
     * @return DetectorRunSupport.
     */
    private DetectorRunSupport getDetectorRunSupport() {
        synchronized (MUTEX_PROCESS_RUNNING) {
            if (detectorRunSupport == null) {
                detectorRunSupport = new DetectorRunSupport(Detector.this);
            }
            return detectorRunSupport;
        }
    }

    /**
     * Returns the Training dataset of this Detector, which will be used for the
     * AbstractInferenceModel to train the InferenceModel.
     *
     * @return Map
     */
    public Map<String, List<TrackSegment>> getTrainingsSet() {
        synchronized (MUTEX_PROCESS_RUNNING) {
            return getDetectorRunSupport().getTrainingsSet();
        }
    }

    /**
     * Returns the inference set of this Detector, which will be used by the
     * used AbstractInferenceModel to classify in transport modes.
     *
     * @return List
     */
    public List<TrackSegment> getInferenceSet() {
        return getDetectorRunSupport().getInferenceSet();
    }

    /**
     * Returns the current used profile
     *
     * @return A profile instance or null.
     */
    public Profile getCurrentActiveProfile() {
        return currentActiveProfile;
    }

    /**
     * Sets the new active profile, which will be used with the next execution
     * of this Detector.
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
     * Returns the PreProcess Pipeline, which contains all registed
     * FilterProcesses that will be used to filter the input data.
     *
     * @return PreProcessorPipeline.
     */
    public PreProcessorPipeline getPreProcessorPipeline() {
        return preProcessorPipeline;
    }

    /**
     * Returns the PostProcessor Pipeline, which contains all registed Task that
     * will be used after the classification process.
     *
     * @return postProcessorPipeline
     */
    public PostProcessorPipeline getPostProcessorPipeline() {
        return postProcessorPipeline;
    }

    /**
     * Returns the underlying AbstractInferenceModel implementation.
     *
     * @return an AbstractInferenceModel instance.
     */
    public AbstractInferenceModel getInferenceModel() {
        return inferenceModel;
    }

    /**
     * Returns the current DetectorState of this Detector.
     *
     * @return ProcessState
     */
    public ProcessState getDetectorState() {
        return detectorState;
    }

    /**
     * Set the current DetecotState and fires a PropertyChangeEvent if the
     * specified detectorState is not null.
     *
     * @param detectorState a ProcessState instance.
     */
    synchronized void setDetectorState(ProcessState detectorState) {
        if (detectorState != null) {
            Object oldValue = this.detectorState;
            this.detectorState = detectorState;
            pcs.firePropertyChange(PROP_NAME_DETECTOR_STATE, oldValue, detectorState);
        }
    }

    /**
     * Returns the underlying DataObject, which represents the
     * DetectorDescriptor xml file.
     *
     * @return DetectorDataObject
     */
    public DetectorDataObject getDataObject() {
        return dataObject;
    }

    /**
     * Convience method to access the DetectorDescriptor
     *
     * @return DetectorDescriptor.
     */
    public DetectorDescriptor getDetectorDescriptor() {
        DetectorDescriptor detectorDescriptor = null;
        try {
            detectorDescriptor = dataObject.getDetectorDescriptor();
        } catch (JAXBException ex) {
            setDetectorState(ProcessState.ERROR);
        } catch (IOException ex) {
            setDetectorState(ProcessState.ERROR);
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
     * Returns a list of all StatisticProvides from both Pipelines.
     *
     * @return a list of StatisticProviders
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
    public void updateSource() {
        if (dataObject != null) {
            dataObject.modifySourceEditor();
        }
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Detector[ name={0}, description={1}{2}]",
                getDetectorDescriptor().getName(),
                getDetectorDescriptor().getDescription());
    }

    private class ChangeListenerImpl implements ChangeListener {

        public ChangeListenerImpl() {
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            reinit();
            modelChanged(null);
        }
    }
}
