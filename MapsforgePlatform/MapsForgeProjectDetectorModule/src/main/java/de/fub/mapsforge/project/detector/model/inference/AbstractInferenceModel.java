/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.factories.nodes.InferenceModelNode;
import de.fub.mapsforge.project.detector.model.inference.processhandler.InferenceDataProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.processhandler.TrainingsDataProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.processhandler.InferenceModelProcessHandler;
import de.fub.mapsforge.project.detector.model.DetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import static de.fub.mapsforge.project.detector.model.inference.InferenceMode.CROSS_VALIDATION_MODE;
import de.fub.mapsforge.project.detector.model.inference.processhandler.CrossValidationProcessHandler;
import de.fub.mapsforge.project.detector.model.inference.ui.DefaultInferenceModelVisualRepresenterComponent;
import de.fub.mapsforge.project.detector.model.inference.ui.InferenceModelSettingForm;
import de.fub.mapsforge.project.detector.model.xmls.Features;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlers;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * comment 2 3
 *
 * @author Serdar
 */
public abstract class AbstractInferenceModel extends DetectorProcess<InferenceModelInputDataSet, InferenceModelResultDataSet> {

    private static final JComponent DEFAULT_VISUAL_REPRESENTER = new DefaultInferenceModelVisualRepresenterComponent();
    public static final String CLASSES_ATTRIBUTE_NAME = "classes";
    public static final String PROP_NAME_FEATURE_LIST = "abstractInferenceMode.featureList";
    public static final String PROP_NAME_INFERENCE_MODE = "abstractInferenceMode.inferenceMode";
    private static final String ICON_NAME = "inferenceModelIcon.png";
    private final Object INFERENCE_MODEL_LISTENER_MUTEX = new Object();
    private final InferenceModelResultDataSet outputDataSet = new InferenceModelResultDataSet();
    private final EnumMap<InferenceMode, Class<? extends InferenceModelProcessHandler>> processHandlerMap = new EnumMap<InferenceMode, Class<? extends InferenceModelProcessHandler>>(InferenceMode.class);
    private final EnumMap<InferenceMode, InferenceModelProcessHandler> processHandlerInstanceMap = new EnumMap<InferenceMode, InferenceModelProcessHandler>(InferenceMode.class);
    private final HashSet<InferenceModelListener> listenerSet = new HashSet<InferenceModelListener>();
    private final HashSet<FeatureProcess> featureList = new HashSet<FeatureProcess>();
    private final ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
    private final Map<String, Attribute> attibuteMap = new HashMap<String, Attribute>();
    protected ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private InferenceModelInputDataSet inputDataSet;
    private InferenceMode inferenceMode = InferenceMode.ALL_MODE;
    private Classifier classifier;
    private InferenceModelDescriptor inferenceModelDescriptor = null;

    public AbstractInferenceModel() {
        this(null);
    }

    public AbstractInferenceModel(Detector detector) {
        super(detector);
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
                    feature = DetectorUtils.createInstance(FeatureProcess.class, featureDescriptor.getJavaType());
                    if (feature != null) {
                        addFeature(feature);
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
        }
    }

    /**
     *
     * @return
     */
    public InferenceModelDescriptor getInferenceModelDescriptor() {
        if (inferenceModelDescriptor == null) {
            if (getDetector() == null) {
                try {
                    inferenceModelDescriptor = DetectorUtils.getInferenceModelDescriptor(getClass());
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
                throw new IllegalArgumentException(inferenceMode + " not supported"); //NO18N
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
        attributeList.clear();
        attibuteMap.clear();

        // the class label attribute will be the first attribute
        // in the list of attributes.
        // this is more like a convention
        Attribute attribute = new Attribute("class", new ArrayList<String>(inputDataSet.getTrainingsSet().keySet()));
        attributeList.add(attribute);
        attibuteMap.put(CLASSES_ATTRIBUTE_NAME, attribute);


        for (FeatureProcess feature : getFeatureList()) {
            attribute = new Attribute(feature.getName());
            attibuteMap.put(feature.getName(), attribute);
            attributeList.add(attribute);
        }
    }

    /**
     *
     */
    private void startTraining() {
        // build feature Array and Map
        initAttributes();

        InferenceModelProcessHandler processHandler = null;
        // training with test of classifier
        fireStartEvent(InferenceMode.TRAININGS_MODE);
        processHandler = getProcessHandlerInstance(InferenceMode.TRAININGS_MODE);
        processHandler.start();
        fireFinishedEvent(InferenceMode.TRAININGS_MODE);

        // cross validation of classifier
        fireStartEvent(CROSS_VALIDATION_MODE);
        processHandler = getProcessHandlerInstance(CROSS_VALIDATION_MODE);
        processHandler.start();
        fireFinishedEvent(CROSS_VALIDATION_MODE);
    }

    /**
     *
     */
    private void startInference() {
        InferenceModelProcessHandler processHandler = null;
        // third phase inference
        fireStartEvent(InferenceMode.INFERENCE_MODE);
        processHandler = getProcessHandlerInstance(InferenceMode.INFERENCE_MODE);
        processHandler.start();
        fireFinishedEvent(InferenceMode.INFERENCE_MODE);
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
                    default:
                        throw new IllegalArgumentException(infMode1 + " not supported!"); // NO18N
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
     *
     * @return
     */
    @Override
    public InferenceModelResultDataSet getResult() {
        return this.outputDataSet;
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
     *
     * @return
     */
    @Override
    public JComponent getSettingsView() {
        return new InferenceModelSettingForm(AbstractInferenceModel.this);
    }

    /**
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
     * Creates a weka classifier for this inference model.
     *
     * @return Classifier - a weka classifier. null not permitted.
     */
    protected abstract Classifier createClassifier();

    public JToolBar getToolbarRepresenter() {
        return null;
    }

    public JComponent getVisualRepresenter() {
        return DEFAULT_VISUAL_REPRESENTER;
    }
}
