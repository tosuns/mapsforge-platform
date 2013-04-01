/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelInputDataSet;
import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.mapsforge.project.detector.model.inference.ui.InferenceResultPanel;
import de.fub.mapsforge.project.detector.utils.GPSUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("LBL_Detector_clustering_Title=Clustering")
@ServiceProvider(service = InferenceModelProcessHandler.class)
public class InferenceDataProcessHandler extends InferenceModelProcessHandler {

    private InferenceResultPanel inferenceResultPanel = null;
    // key = class/transport mode name, value = list of instance whose label is the key;
    // don't acces this member directly. use the respective methods
    private HashMap<String, List<Instance>> resultMap = new HashMap<String, List<Instance>>();
    // helper map to map instances to the original dataset
    private HashMap<Instance, TrackSegment> instanceToTrackSegmentMap = new HashMap<Instance, TrackSegment>();

    public InferenceDataProcessHandler() {
        super(null);
    }

    public InferenceDataProcessHandler(AbstractInferenceModel inferenceModel) {
        super(inferenceModel);
    }

    private void setClassesToView(Collection<String> classes) {
        ArrayList<String> arrayList = new ArrayList<String>(classes);
        Collections.sort(arrayList);
        for (String string : arrayList) {
            resultMap.put(string, new ArrayList<Instance>());
        }
        updateVisualRepresentation();
    }

    @Override
    protected void handle() {
        clearResults();

        Classifier classifier = getInferenceModel().getClassifier();
        HashSet<TrackSegment> inferenceDataSet = getInferenceDataSet();
        ArrayList<Attribute> attributeList = getInferenceModel().getAttributeList();

        if (!attributeList.isEmpty()) {
            Set<String> keySet = getInferenceModel().getInput().getTrainingsSet().keySet();
            setClassesToView(keySet);

            Instances unlabeledInstances = new Instances("Unlabeld Tracks", attributeList, 0); //NO18N
            unlabeledInstances.setClassIndex(0);

            ArrayList<TrackSegment> segmentList = new ArrayList<TrackSegment>();
            for (TrackSegment segment : inferenceDataSet) {
                Instance instance = getInstance(segment);
                unlabeledInstances.add(instance);
                segmentList.add(segment);
            }

            // create copy
            Instances labeledInstances = new Instances(unlabeledInstances);

            for (int index = 0; index < labeledInstances.numInstances(); index++) {
                try {
                    Instance instance = labeledInstances.instance(index);

                    // classify instance
                    double classifyed = classifier.classifyInstance(instance);
                    instance.setClassValue(classifyed);

                    // get class label
                    String value = unlabeledInstances.classAttribute().value((int) classifyed);

                    if (index < segmentList.size()) {
                        instanceToTrackSegmentMap.put(instance, segmentList.get(index));
                    }

                    // put label and instance to result map
                    put(value, instance);

                    // update visw
                    updateVisualRepresentation();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            // update result set of the inferenceModel
            for (Entry<String, List<Instance>> entry : resultMap.entrySet()) {
                List<TrackSegment> trackSegmentList = new ArrayList<TrackSegment>();
                for (Instance instance : entry.getValue()) {
                    TrackSegment trackSegment = instanceToTrackSegmentMap.get(instance);
                    if (trackSegment != null) {
                        trackSegmentList.add(trackSegment);
                    }
                }

                // only those classes are put into  the result data set, which are not empty
                if (!trackSegmentList.isEmpty()) {
                    Gpx gpx = GPSUtils.convert(trackSegmentList);
                    getInferenceModel().getResult().put(entry.getKey(), Arrays.asList(gpx));
                }
            }
        } else {
            throw new InferenceModelClassifyException(MessageFormat.format("No attributes available. Attribute list lengeth == {0}", attributeList.size()));
        }
    }

    protected void updateVisualRepresentation() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getInferenceResultPanel().updateView(new ClassificationResult(resultMap, instanceToTrackSegmentMap)); // TODO
            }
        });

    }

    private void put(String className, Instance instance) {
        if (!resultMap.containsKey(className)) {
            resultMap.put(className, new ArrayList<Instance>());
        }
        resultMap.get(className).add(instance);
    }

    private void clearResults() {
        getInferenceModel().getResult().clear();
        resultMap.clear();
        instanceToTrackSegmentMap.clear();
    }

    public HashSet<TrackSegment> getInferenceDataSet() {
        InferenceModelInputDataSet input = getInferenceModel().getInput();
        HashSet<TrackSegment> dataset = input.getInferenceSet();
        return dataset;
    }

    @Override
    public JComponent getVisualRepresentation() {
        return getInferenceResultPanel();
    }

    private InferenceResultPanel getInferenceResultPanel() {
        if (inferenceResultPanel == null) {
            inferenceResultPanel = new InferenceResultPanel();
            inferenceResultPanel.getTitle().setText(Bundle.LBL_Detector_clustering_Title());
        }
        return inferenceResultPanel;
    }

    private Instance getInstance(TrackSegment segment) {
        Instance instance = new DenseInstance(getInferenceModel().getAttributeList().size());

        for (FeatureProcess feature : getInferenceModel().getFeatureList()) {
            feature.setInput(segment);
            feature.run();
            String featureName = feature.getName();
            Attribute attribute = getInferenceModel().getAttributeMap().get(featureName);
            Double result = feature.getResult();
            instance.setValue(attribute, result);
        }
        return instance;
    }

    public static class ClassificationResult {

        private final HashMap<String, List<Instance>> resultMap;
        private final HashMap<Instance, TrackSegment> instanceToTrackSegmentMap;

        public ClassificationResult(HashMap<String, List<Instance>> resultMap, HashMap<Instance, TrackSegment> instanceToTrackSegmentMap) {
            this.instanceToTrackSegmentMap = instanceToTrackSegmentMap;
            this.resultMap = resultMap;
        }

        public Map<String, List<Instance>> getResultMap() {
            return Collections.unmodifiableMap(resultMap);
        }

        public HashMap<Instance, TrackSegment> getInstanceToTrackSegmentMap() {
            return instanceToTrackSegmentMap;
        }
    }
}
