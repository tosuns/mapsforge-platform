/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference;

import de.fub.maps.project.detector.factories.nodes.inference.InferenceModelNode;
import de.fub.maps.project.detector.model.Detector;
import static de.fub.maps.project.detector.model.inference.InferenceMode.CROSS_VALIDATION_MODE;
import de.fub.maps.project.detector.model.inference.features.FeatureProcess;
import de.fub.maps.project.detector.model.inference.processhandler.CrossValidationProcessHandler;
import de.fub.maps.project.detector.model.inference.processhandler.InferenceDataProcessHandler;
import de.fub.maps.project.detector.model.inference.processhandler.InferenceModelProcessHandler;
import de.fub.maps.project.detector.model.inference.processhandler.TrainingsDataProcessHandler;
import de.fub.maps.project.detector.model.inference.ui.InferenceModelSettingForm;
import de.fub.maps.project.detector.model.process.DetectorProcess;
import de.fub.maps.project.detector.model.xmls.Features;
import de.fub.maps.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.maps.project.detector.model.xmls.ProcessHandlers;
import de.fub.maps.project.detector.model.xmls.TransportMode;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * comment 2 3
 *
 * @author Serdar
 */
public abstract class AbstractInferenceModel extends DetectorProcess<InferenceModelInputDataSet, InferenceModelResultDataSet> {

    private static final Logger LOG = Logger.getLogger(AbstractInferenceModel.class.getName());
    public static final String OPTIONS_PROPERTY_SECTION = "inference.model.option";
    /**
     * constant for the classes Weka Atttribute creation.
     */
    public static final String CLASSES_ATTRIBUTE_NAME = "classes";
    /**
     *
     * Property name for propergating feature list changes.
     */
    public static final String PROP_NAME_FEATURE_LIST = "abstractInferenceMode.featureList";
    /**
     * Property name for propergating inference mode changes.
     */
    public static final String PROP_NAME_INFERENCE_MODE = "abstractInferenceMode.inferenceMode";
    /**
     * The name of the icon for the ui representation, which will be access via
     * the utility class IconRegister.
     */
    private static final String ICON_NAME = "inferenceModelIcon.png";
    /**
     * MUTEX to synchronize.
     */
    private final Object INFERENCE_MODEL_LISTENER_MUTEX = new Object();
    /**
     * The result value of the classification.
     */
    private final InferenceModelResultDataSet outputDataSet = new InferenceModelResultDataSet();
    /**
     * Holds the processhandlers for the three inference modes.
     *
     */
    private final EnumMap<InferenceMode, Class<? extends InferenceModelProcessHandler>> processHandlerMap = new EnumMap<InferenceMode, Class<? extends InferenceModelProcessHandler>>(InferenceMode.class);
    /**
     *
     */
    private final EnumMap<InferenceMode, InferenceModelProcessHandler> processHandlerInstanceMap = new EnumMap<InferenceMode, InferenceModelProcessHandler>(InferenceMode.class);
    /**
     *
     */
    private final HashSet<InferenceModelListener> listenerSet = new HashSet<InferenceModelListener>();
    /**
     *
     */
    private final HashSet<FeatureProcess> featureList = new HashSet<FeatureProcess>();
    /**
     *
     */
    private final List<Attribute> attributeList = new ArrayList<Attribute>();
    /**
     *
     */
    private final Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();
    /**
     *
     */
    protected ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    /**
     *
     */
    private InferenceModelInputDataSet inputDataSet;
    /**
     *
     */
    private InferenceMode inferenceMode = InferenceMode.ALL_MODE;
    /**
     *
     */
    private Classifier classifier;
    /**
     *
     */
    private InferenceModelDescriptor inferenceModelDescriptor = null;

    public AbstractInferenceModel() {
    }

    @Override
    protected void setDetector(Detector detector) {
        super.setDetector(detector);
        if (detector != null) {
            modelSynchronizerClient = detector.create(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    reinit();
                }
            });
        }
        reinit();
    }

    /**
     * initialises this inference model.
     */
    private void reinit() {
        if (getInferenceModelDescriptor() != null) {
            featureList.clear();
            processHandlerMap.clear();

            // get all features description and instanciate the features.
            Features features = getInferenceModelDescriptor().getFeatures();
            if (features != null) {
                FeatureProcess feature = null;
                HashSet<String> featureType = new HashSet<String>();
                for (ProcessDescriptor featureDescriptor : features.getFeatureList()) {
                    if (featureDescriptor != null && !featureType.contains(featureDescriptor.getName())) {
                        featureType.add(featureDescriptor.getName());
                        try {
                            feature = FeatureProcess.find(featureDescriptor, getDetector());
                            if (feature != null) {
                                addFeature(feature);
                            }
                        } catch (DetectorProcessNotFoundException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                featureType.clear();
            }

            // get all processHandler descriptions and instanciate the processhandlers
            ProcessHandlers inferenceModelProcessHandlers = getInferenceModelDescriptor().getInferenceModelProcessHandlers();
            if (inferenceModelProcessHandlers != null) {
                InferenceModelProcessHandler processHandler = null;
                for (ProcessHandlerDescriptor processHandlerDescriptor : inferenceModelProcessHandlers.getProcessHandlerList()) {
                    try {
                        processHandler = InferenceModelProcessHandler.find(processHandlerDescriptor, AbstractInferenceModel.this);
                        processHandlerInstanceMap.put(processHandlerDescriptor.getInferenceMode(), processHandler);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

            initAttributes();
        }
    }

    /**
     * Returns the data synchronizer client. Via this synchornizer visual
     * components can use this client to receive change events of the inference
     * model descriptor.
     *
     * @return ModuleSynchronizerClient
     */
    public ModelSynchronizer.ModelSynchronizerClient getModelSynchronizerClient() {
        return modelSynchronizerClient;
    }

    /**
     * Returns the inference model descriptor if this inference model. If this
     * model has a parent Detector, then the inferenceModelDescriptor will be
     * the one specified in the DetectorDescriptor, otherwise a specified
     * default descriptor will be created via the
     * <code>createDefaultDescriptor</code>.
     *
     * @return InferenceModelDescriptor or null if the creation failed.
     */
    public InferenceModelDescriptor getInferenceModelDescriptor() {
        if (inferenceModelDescriptor == null) {
            if (getDetector() == null) {
                inferenceModelDescriptor = createDefaultDescriptor();
            } else {
                inferenceModelDescriptor = getDetector().getDetectorDescriptor().getInferenceModel();
            }
        }
        return inferenceModelDescriptor;
    }

    /**
     * Starts the process depending on the current inference mode.
     *
     */
    @Override
    protected void start() {
        switch (inferenceMode) {
            case INFERENCE_MODE:
                startInference();
                break;
            case TRAININGS_MODE:
                startTraining();
                break;
            case ALL_MODE:
                startTraining();
                startInference();
                break;
            case CROSS_VALIDATION_MODE:
                // TODO not supported anymore
                break;
            default:
                throw new IllegalArgumentException(MessageFormat.format("{0} not supported", inferenceMode)); //NO18N
        }
    }

    /**
     *
     * @return
     */
    public Collection<Attribute> getAttributes() {
        return attributeList;
    }

    /**
     *
     * @return
     */
    public Map<String, Attribute> getAttributeMap() {
        return attributeMap;
    }

    /**
     * Initialised the attributeList and attributeMap of this inference model.
     */
    private void initAttributes() {
        if (getDetector() != null
                && getDetector().getDetectorDescriptor() != null
                && getDetector().getDetectorDescriptor().getDatasets() != null
                && getDetector().getDetectorDescriptor().getDatasets().getTrainingSet() != null
                && getDetector().getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList() != null) {
            attributeList.clear();
            attributeMap.clear();

            HashSet<String> classes = new HashSet<String>();
            List<TransportMode> transportModeList = getDetector().getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList();
            for (TransportMode transportMode : transportModeList) {
                classes.add(transportMode.getName());
            }
            // the class label attribute will be the first attribute
            // in the list of attributes.
            // this is more like a convention
            ArrayList<String> classLabels = new ArrayList<String>(classes);
            Collections.sort(classLabels);
            Attribute attribute = new Attribute(CLASSES_ATTRIBUTE_NAME, classLabels);

            if (!attributeMap.containsKey(CLASSES_ATTRIBUTE_NAME)) {
                attributeList.add(attribute);
                attributeMap.put(CLASSES_ATTRIBUTE_NAME, attribute);

                for (FeatureProcess feature : getFeatureList()) {
                    if (feature != null) {
                        attribute = new Attribute(feature.getName());
                        // make sure there was no attribute overwritten
                        if (!attributeMap.containsKey(feature.getName())) {
                            attributeMap.put(feature.getName(), attribute);
                            attributeList.add(attribute);
                        } else {
                            LOG.log(Level.SEVERE, "attribute {0} was overwritten. this should never happen and is a bug", feature.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Note : The classifier[, , ] should not be trained when handed over to the
     * crossValidateModel method. Why? If the classifier does not abide to the
     * Weka convention that a classifier must be re-initialized every time the
     * buildClassifier method is called (in other words: subsequent calls to the
     * buildClassifier method always return the same results), you will get
     * inconsistent and worthless results. The crossValidateModel takes care of
     * training and evaluating the classifier. (It creates a copy of the
     * original classifier that you hand over to the crossValidateModel for each
     * run of the cross-validation.)
     *
     * @see {@link http://weka.wikispaces.com/Use+WEKA+in+your+Java+code}
     *
     * Starts the training process of this inference model. throws an
     * IllegalStateException if this model does not contain any features.
     *
     */
    private void startTraining() {
        if (!getAttributes().isEmpty()) {
            InferenceModelProcessHandler processHandler = null;

            // first start cross validation of classifier. Important see comment above
            fireStartEvent(CROSS_VALIDATION_MODE);
            processHandler = getProcessHandlerInstance(CROSS_VALIDATION_MODE);
            processHandler.start();
            fireFinishedEvent(CROSS_VALIDATION_MODE);

            // then training with test of classifier
            fireStartEvent(InferenceMode.TRAININGS_MODE);
            processHandler = getProcessHandlerInstance(InferenceMode.TRAININGS_MODE);
            processHandler.start();
            fireFinishedEvent(InferenceMode.TRAININGS_MODE);
        } else {
            throw new IllegalStateException("There are no attributes/features registered to use for the classifier!"); // NO18N
        }
    }

    /**
     * Starts the inference process of this inference model.
     */
    private void startInference() {
        if (getClassifier() != null) {
            InferenceModelProcessHandler processHandler = null;
            // third phase inference
            fireStartEvent(InferenceMode.INFERENCE_MODE);
            processHandler = getProcessHandlerInstance(InferenceMode.INFERENCE_MODE);
            processHandler.start();
            fireFinishedEvent(InferenceMode.INFERENCE_MODE);
        }
    }

    /**
     *
     * @param inferenceMode1
     * @param inferenceModelProcessHandleClass
     */
    public final void putInferenceProcessHandler(InferenceMode inferenceMode1, Class<? extends InferenceModelProcessHandler> inferenceModelProcessHandleClass) {
        if (inferenceMode1 != null && inferenceModelProcessHandleClass != null) {
            processHandlerMap.put(inferenceMode, inferenceModelProcessHandleClass);
        }
    }

    /**
     * Returns a list with all InferenceModelProcessHander instances of this
     * inference model.
     *
     * @return a list of InferenceModelProcessHandlers
     */
    public List<InferenceModelProcessHandler> getProcessHandlers() {
        List<InferenceModelProcessHandler> list = new ArrayList<InferenceModelProcessHandler>(processHandlerInstanceMap.size());
        for (InferenceMode mode : InferenceMode.values()) {
            InferenceModelProcessHandler processHandlerInstance = getProcessHandlerInstance(mode);
            if (processHandlerInstance != null) {
                list.add(processHandlerInstance);
            }
        }
        return list;
    }

    /**
     * Returns the ProcessHandler instance for the specified InferenceMode. If
     * there is no ProcessHandler registered for the specified InferenceMode,
     * then a default instance for the InferenceMode will be created if
     * possible.
     *
     * @param infMode1 The InferenceMode for which a ProcessHandler should be
     * returned.
     * @return a InferenceModelProcessHander or null if there is no instance
     * could be created for the specified InferenceMode.
     */
    public InferenceModelProcessHandler getProcessHandlerInstance(InferenceMode infMode1) {
        InferenceModelProcessHandler processHandler = processHandlerInstanceMap.get(infMode1);
        if (processHandler == null) {
            Class<? extends InferenceModelProcessHandler> clazz = processHandlerMap.get(infMode1);
            if (clazz != null) {
                try {
                    processHandler = InferenceModelProcessHandler.find(clazz.getName(), AbstractInferenceModel.this);
                } catch (InstantiationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            // fall back to default process handlers
            if (processHandler == null) {
                switch (infMode1) {
                    case TRAININGS_MODE:
                        processHandler = new TrainingsDataProcessHandler(AbstractInferenceModel.this);
                        break;
                    case CROSS_VALIDATION_MODE:
                        processHandler = new CrossValidationProcessHandler(AbstractInferenceModel.this);
                        break;
                    case INFERENCE_MODE:
                        processHandler = new InferenceDataProcessHandler(AbstractInferenceModel.this);
                        break;
                    case ALL_MODE: // do nothing
                        break;
                    default:
                        throw new IllegalArgumentException(MessageFormat.format("{0} not supported!", infMode1)); // NO18N
                }
                processHandlerInstanceMap.put(infMode1, processHandler);
            } else {
                processHandlerInstanceMap.put(infMode1, processHandler);
            }
        }
        return processHandler;
    }

    /**
     *
     * @param mode
     */
    protected void fireStartEvent(InferenceMode mode) {
        synchronized (INFERENCE_MODEL_LISTENER_MUTEX) {
            for (InferenceModelListener listener : listenerSet) {
                switch (mode) {
                    case TRAININGS_MODE:
                        listener.startedTraining();
                        break;
                    case CROSS_VALIDATION_MODE:
                        listener.startedCrossValidation();
                        break;
                    case INFERENCE_MODE:
                        listener.startedClustering();
                        break;
                    case ALL_MODE:
                        // do nothing
                        break;
                    default:
                        throw new IllegalArgumentException("Event not supported"); //NO18N
                }
            }
        }
    }

    /**
     *
     * @param mode
     */
    protected void fireFinishedEvent(InferenceMode mode) {
        synchronized (INFERENCE_MODEL_LISTENER_MUTEX) {
            for (InferenceModelListener listener : listenerSet) {
                switch (mode) {
                    case TRAININGS_MODE:
                        listener.finishedTraining();
                        break;
                    case CROSS_VALIDATION_MODE:
                        listener.finishedCrossValidation();
                        break;
                    case INFERENCE_MODE:
                        listener.finishedClustering();
                        break;
                    case ALL_MODE:
                        // do nothing
                        break;
                    default:
                        throw new IllegalArgumentException("Event not supported"); //NO18N
                }
            }
        }
    }

    /**
     * Returns the current inference mode of this model.
     *
     * @return InferenceMode instance.
     */
    public InferenceMode getInferenceMode() {
        return inferenceMode;
    }

    /**
     *
     * @param inferenceMode
     */
    public void setInferenceMode(InferenceMode inferenceMode) {
        Object oldValue = this.inferenceMode;
        this.inferenceMode = inferenceMode;
        pcs.firePropertyChange(PROP_NAME_INFERENCE_MODE, oldValue, this.inferenceMode);
    }

    /**
     *
     * @param inputDataset
     */
    @Override
    public void setInput(InferenceModelInputDataSet inputDataset) {
        this.inputDataSet = inputDataset;
    }

    /**
     * Returns the input data model, which is used to train the classifier and
     * classify the provided instances.
     *
     * @return InferenceModelInputData instance or null.
     */
    public InferenceModelInputDataSet getInput() {
        return inputDataSet;
    }

    /**
     * Returns the result of the training and inference processes of this
     * inference model.
     *
     *
     * @return always an InferenceModelResultDataSet
     */
    @Override
    public InferenceModelResultDataSet getResult() {
        return this.outputDataSet;
    }

    /**
     *
     *
     * @return
     */
    @Override
    protected Node createNodeDelegate() {
        return getDetector() == null ? new InferenceModelNode(AbstractInferenceModel.this) : new InferenceModelNode(getDetector());
    }

    /**
     * Returns the default icon, which represents this model.
     *
     * @return an Image instance or null.
     */
    @Override
    protected Image getDefaultImage() {
        return IconRegister.findRegisteredIcon(ICON_NAME);
    }

    /**
     * Returns the used weka classifier.
     *
     * @return a weka classifier.
     */
    public final Classifier getClassifier() {
        if (classifier == null) {
            classifier = createClassifier();
        }
        return classifier;
    }

    /**
     *
     * @param listener
     */
    public final void addInferenceModelListener(final InferenceModelListener listener) {
        synchronized (INFERENCE_MODEL_LISTENER_MUTEX) {
            listenerSet.add(listener);
        }
    }

    /**
     *
     * @param listener
     */
    public final void removeInferenceModelListener(final InferenceModelListener listener) {
        synchronized (INFERENCE_MODEL_LISTENER_MUTEX) {
            listenerSet.remove(listener);
        }
    }

    /**
     *
     * @return
     */
    public int featureListSize() {
        return featureList.size();
    }

    /**
     *
     * @return
     */
    public boolean isfeatureListEmpty() {
        return featureList.isEmpty();
    }

    /**
     * Behaves like the List interface's add method except it fires a
     * PropertyChangeEvent.
     *
     * @param e
     * @return
     */
    public boolean addFeature(FeatureProcess e) {
        boolean result = featureList.add(e);
        if (result) {
            pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
        }
        return result;
    }

    /**
     * Removes the specified feature from the feature list of this model and
     * fires a PropertyChangeEvent.
     *
     * @param o FeatureProcess
     * @return
     */
    public boolean removeFeature(FeatureProcess o) {
        boolean result = featureList.remove(o);
        if (result) {
            pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
        }
        return result;
    }

    /**
     * Clears the feature list of this model.
     */
    public void clearFeatureList() {
        featureList.clear();
        pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
    }

    /**
     * Removes the specified features from the feature list of this model and
     * fires a propertyChangeEvent.
     *
     * @param c
     * @return
     *
     */
    public boolean removeAllFeatures(Collection<?> c) {
        boolean result = featureList.removeAll(c);
        if (result) {
            pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
        }
        return result;
    }

    /**
     * Adds the collection of features to the list of features of this model and
     * fires a propertyChangeEvent.
     *
     * @param c
     * @return
     */
    public boolean addAllFeatures(Collection<? extends FeatureProcess> c) {
        boolean result = featureList.addAll(c);
        if (result) {
            pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
        }
        return result;
    }

    /**
     * Returns the list of used features.
     *
     * @return a copy of the list of all features.
     */
    public Collection<FeatureProcess> getFeatureList() {
        return Collections.unmodifiableCollection(featureList);
    }

    /**
     * Attempts to cance the process of this inference model.
     *
     * @return default always false and has no effect on the process.
     */
    @Override
    public boolean cancel() {
        return false;
    }

    /**
     * Returns a toolbar which contains can contain commands and other ui
     * controllers to control this inference model.
     *
     * @return JToolbar, default null.
     */
    public JToolBar getToolbarRepresenter() {
        return null;
    }

    /**
     * Returns the default settings from the configure this inference model
     * implementation, which gets used by the project settings component.
     *
     * @return a JCompoent.
     */
    @Override
    public JComponent getSettingsView() {
        InferenceModelSettingForm inferenceModelSettingForm = new InferenceModelSettingForm(AbstractInferenceModel.this);
        return inferenceModelSettingForm;
    }

    /**
     * Reinitializes the used weka classifier.
     */
    public void resetClassifier() {
        classifier = createClassifier();
    }

    /**
     * Factory method, which creates for the specified detector and qualified
     * name that should be instanciated. This methods lookps up via
     * <code>findAll</code> all registered AbstractInferenceModel
     * implementations
     *
     * @param qualifiedInstanceName the full qualified name of the to be
     * instanciated AbstractInferenceModel type.
     * @param detector the parent detector of the to be instanciated
     * AbstractInferenceMode.
     * @return an AbstractInferenceModel implementation.
     * @throws
     * de.fub.maps.project.detector.model.process.DetectorProcess.DetectorProcessNotFoundException
     * if there is no registered type with the specified full qualified name.
     */
    public static synchronized AbstractInferenceModel find(String qualifiedInstanceName, Detector detector) throws DetectorProcessNotFoundException {
        AbstractInferenceModel abstractInferenceModel = null;
        try {
            Class<?> clazz = null;
            ClassLoader classLoader = Lookup.getDefault().lookup(ClassLoader.class);
            // prefer netbeans classloader
            if (classLoader != null) {
                clazz = classLoader.loadClass(qualifiedInstanceName);
            } else {
                // fall back
                clazz = Class.forName(qualifiedInstanceName);
            }
            if (AbstractInferenceModel.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                Class<AbstractInferenceModel> abstractInferenceModelClass = (Class<AbstractInferenceModel>) clazz;
                abstractInferenceModel = DetectorProcess.find(abstractInferenceModelClass, detector);
            } else {
                throw new DetectorProcessNotFoundException(MessageFormat.format("{0} is not type of {1}", clazz.getSimpleName(), AbstractInferenceModel.class.getSimpleName()));
            }
        } catch (Throwable ex) {
            throw new DetectorProcessNotFoundException(ex);
        }
        return abstractInferenceModel;
    }

    /**
     * Creates a weka classifier for this inference model.
     *
     * @return Classifier - a weka classifier. null not permitted.
     */
    protected abstract Classifier createClassifier();

    /**
     * Creates the default descriptor of this inference model implementation.
     */
    protected abstract InferenceModelDescriptor createDefaultDescriptor();
}
