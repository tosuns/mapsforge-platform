/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.mapsforge.project.detector.factories.nodes.inference.InferenceModelNode;
import de.fub.mapsforge.project.detector.model.Detector;
import static de.fub.mapsforge.project.detector.model.inference.InferenceMode.CROSS_VALIDATION_MODE;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.model.inference.processhandler.CrossValidationProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.processhandler.InferenceDataProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.processhandler.InferenceModelProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.processhandler.TrainingsDataProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.ui.InferenceModelSettingForm;
import de.fub.mapsforge.project.detector.model.process.DetectorProcess;
import de.fub.mapsforge.project.detector.model.xmls.Features;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlers;
import de.fub.mapsforge.project.detector.model.xmls.TransportMode;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public static final String OPTIONS_PROPERTY_SECTION = "inference.model.option";
    /**
     *
     */
    public static final String CLASSES_ATTRIBUTE_NAME = "classes";
    /**
     *
     */
    public static final String PROP_NAME_FEATURE_LIST = "abstractInferenceMode.featureList";
    /**
     *
     */
    public static final String PROP_NAME_INFERENCE_MODE = "abstractInferenceMode.inferenceMode";
    /**
     *
     */
    private static final String ICON_NAME = "inferenceModelIcon.png";
    /**
     *
     */
    private final Object INFERENCE_MODEL_LISTENER_MUTEX = new Object();
    /**
     *
     */
    private final InferenceModelResultDataSet outputDataSet = new InferenceModelResultDataSet();
    /**
     * *
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
    private final ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
    /**
     *
     */
    private final Map<String, Attribute> attibuteMap = new HashMap<String, Attribute>();
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
     *
     */
    private void reinit() {
        if (getInferenceModelDescriptor() != null) {
            featureList.clear();
            processHandlerMap.clear();

            // get all features description and instanciate the features.
            Features features = getInferenceModelDescriptor().getFeatures();
            if (features != null) {
                FeatureProcess feature = null;
                for (ProcessDescriptor featureDescriptor : features.getFeatureList()) {
                    try {
                        feature = FeatureProcess.find(featureDescriptor.getJavaType(), getDetector());
                        if (feature != null) {
                            addFeature(feature);
                        }
                    } catch (DetectorProcessNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            // get all processHandler descriptions and instanciate the processhandlers
            ProcessHandlers inferenceModelProcessHandlers = getInferenceModelDescriptor().getInferenceModelProcessHandlers();
            if (inferenceModelProcessHandlers != null) {
                InferenceModelProcessHandler processHandler = null;
                for (ProcessHandlerDescriptor processHandlerDescriptor : inferenceModelProcessHandlers.getProcessHandlerList()) {
                    processHandler = DetectorUtils.createProcessHandler(processHandlerDescriptor, getDetector());
                    if (processHandler != null) {
                        putInferenceProcessHandler(processHandlerDescriptor.getInferenceMode(), processHandler.getClass());
                    }
                }
            }

            initAttributes();
        }
    }

    public ModelSynchronizer.ModelSynchronizerClient getModelSynchronizerClient() {
        return modelSynchronizerClient;
    }

    /**
     *
     * @return
     */
    public InferenceModelDescriptor getInferenceModelDescriptor() {
        if (inferenceModelDescriptor == null) {
            if (getDetector() == null) {
                try {
                    inferenceModelDescriptor = DetectorUtils.getXmlDescriptor(InferenceModelDescriptor.class, getClass());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                inferenceModelDescriptor = getDetector().getDetectorDescriptor().getInferenceModel();
            }
        }
        return inferenceModelDescriptor;
    }

    /**
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
    public ArrayList<Attribute> getAttributeList() {
        return attributeList;
    }

    /**
     *
     * @return
     */
    public Map<String, Attribute> getAttributeMap() {
        return attibuteMap;
    }

    /**
     *
     */
    private void initAttributes() {
        if (getDetector() != null
                && getDetector().getDetectorDescriptor() != null
                && getDetector().getDetectorDescriptor().getDatasets() != null
                && getDetector().getDetectorDescriptor().getDatasets().getTrainingSet() != null
                && getDetector().getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList() != null) {
            attributeList.clear();
            attibuteMap.clear();

            ArrayList<String> classes = new ArrayList<String>();
            List<TransportMode> transportModeList = getDetector().getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList();
            for (TransportMode transportMode : transportModeList) {
                classes.add(transportMode.getName());
            }

            // the class label attribute will be the first attribute
            // in the list of attributes.
            // this is more like a convention
            Attribute attribute = new Attribute("class", classes);
            attributeList.add(attribute);
            attibuteMap.put(CLASSES_ATTRIBUTE_NAME, attribute);


            for (FeatureProcess feature : getFeatureList()) {
                attribute = new Attribute(feature.getName());
                attibuteMap.put(feature.getName(), attribute);
                attributeList.add(attribute);
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
     */
    private void startTraining() {
        initAttributes();
        if (!getAttributeList().isEmpty()) {
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
     *
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
     *
     * @param infMode1
     * @return
     */
    public InferenceModelProcessHandler getProcessHandlerInstance(InferenceMode infMode1) {
        InferenceModelProcessHandler processHandler = processHandlerInstanceMap.get(infMode1);
        if (processHandler == null) {
            Class<? extends InferenceModelProcessHandler> clazz = processHandlerMap.get(infMode1);
            if (clazz != null) {
                try {
                    Constructor<? extends InferenceModelProcessHandler> constructor = clazz.getConstructor(AbstractInferenceModel.class);
                    processHandler = constructor.newInstance(AbstractInferenceModel.this);
                } catch (NoSuchMethodException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InstantiationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InvocationTargetException ex) {
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
     *
     * @return
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
     *
     * @return
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
     *
     * @return
     */
    @Override
    protected Image getDefaultImage() {
        return IconRegister.findRegisteredIcon(ICON_NAME);
    }

    /**
     *
     * @return
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
     *
     * @param o
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
     *
     */
    public void clearFeatureList() {
        featureList.clear();
        pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
    }

    /**
     *
     * @param c
     * @return
     */
    public boolean removeAllFeatures(Collection<?> c) {
        boolean result = featureList.removeAll(c);
        if (result) {
            pcs.firePropertyChange(PROP_NAME_FEATURE_LIST, null, featureList);
        }
        return result;
    }

    /**
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
     *
     * @return
     */
    public Collection<FeatureProcess> getFeatureList() {
        return Collections.unmodifiableCollection(featureList);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean cancel() {
        return false;
    }

    /**
     *
     * @return
     */
    public JToolBar getToolbarRepresenter() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getSettingsView() {
        InferenceModelSettingForm inferenceModelSettingForm = new InferenceModelSettingForm(AbstractInferenceModel.this);
        return inferenceModelSettingForm;
    }

    public void resetClassifier() {
        classifier = createClassifier();
    }

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
}
