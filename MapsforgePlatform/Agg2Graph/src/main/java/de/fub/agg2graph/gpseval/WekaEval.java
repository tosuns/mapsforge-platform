package de.fub.agg2graph.gpseval;

import de.fub.agg2graph.gpseval.data.AggregatedData;
import de.fub.agg2graph.gpseval.features.Feature;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * WekaEval is used to run Weka with training- and test-sets to test the
 * different classification algorithms with different parameters. Instead of
 * using training/test-sets you can run a cross validation, too.
 */
public class WekaEval {

    private Map<String, List<AggregatedData>> mGpsData;
    private List<Feature> mFeatures;
    private Map<String, Attribute> mFeatureAttrMapping = new HashMap<>();
    private Attribute mClassAttribute;
    private FastVector mAttrs;
    private double mTrainingSetSize = 0.6;
    private int mCrossValidationFolds = 10;
    private List<WekaResult> mCrossValidationResults = new LinkedList<>();
    private List<WekaResult> mTrainingTestResults = new LinkedList<>();
    // TODO implement setter
    private Classifier[] mClassifiers = new Classifier[]{new J48(),
        new RandomForest(), new NaiveBayes(),
        // TODO new BayesNet(), --> produces warning
        new MultilayerPerceptron()};

    /**
     *
     * @param gpsData The GPS-data to use
     * @param features The feature-set
     */
    @SuppressWarnings("unchecked")
    public WekaEval(Map<String, List<AggregatedData>> gpsData,
            List<Feature> features) {
        mGpsData = gpsData;
        mFeatures = features;

        /*
         * Create attributes based on feature-list
         */

        mAttrs = new FastVector();

        for (Feature feature : mFeatures) {
            Attribute attr = new Attribute(feature.getIdentifier(),
                    mFeatureAttrMapping.keySet().size());
            mFeatureAttrMapping.put(feature.getIdentifier(), attr);
            mAttrs.addElement(attr);
        }

        // the last attribute is the class attributes which accepts
        // the class name as string. These names must be added to a FastVector.
        int classCount = gpsData.keySet().size();
        FastVector classAttributeValue = new FastVector(classCount);
        for (String className : gpsData.keySet()) {
            classAttributeValue.addElement(className);
        }
        mClassAttribute = new Attribute("class", classAttributeValue);
        mAttrs.addElement(mClassAttribute);

    }

    /**
     * Get the Attribute-instance specified by the feature-name.
     *
     * @param feature
     * @return
     */
    public Attribute getAttributeByFeature(String feature) {
        return mFeatureAttrMapping.get(feature);
    }

    /**
     * Runs Weka (1) using training- and test-set and (2) using cross
     * validation.
     *
     * @throws Exception
     */
    public void run() throws Exception {
        runTrainTestSet();
        runCrossValidation();
    }

    /**
     * Runs Weka using training- and test-set.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void runTrainTestSet() throws Exception {
        // Training set

        Instances trainingSet = new Instances("Classes", mAttrs, 0);
        trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

        // Test set
        Instances testingSet = new Instances("Classes", mAttrs, 0);
        testingSet.setClassIndex(testingSet.numAttributes() - 1);

        // fill training and test set
        for (String className : mGpsData.keySet()) {
            List<AggregatedData> gpsData = mGpsData.get(className);
            int curTrainingSetSize = (int) Math.ceil(gpsData.size()
                    * mTrainingSetSize);

            for (int i = 0; i < gpsData.size(); i++) {
                Instance instance = getInstance(className, gpsData.get(i));

                if (i < curTrainingSetSize) {
                    trainingSet.add(instance);
                } else {
                    testingSet.add(instance);
                }
            }
        }

        if (trainingSet.numInstances() < 1) {
            throw new Exception("Empty training set!");
        }

        if (testingSet.numInstances() < 1) {
            throw new Exception("Empty test set!");
        }

        // start evaluating for each algorithm
        for (Classifier cls : mClassifiers) {
            evaluate(cls, trainingSet, testingSet);
        }
    }

    /**
     * Runs Weka using cross-validation.
     */
    @SuppressWarnings("unchecked")
    public void runCrossValidation() throws Exception {
        // Training set

        Instances trainingSet = new Instances("Classes", mAttrs, 0);
        trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

        // fill training set
        for (String className : mGpsData.keySet()) {
            List<AggregatedData> gpsData = mGpsData.get(className);

            for (AggregatedData data : gpsData) {
                Instance instance = getInstance(className, data);
                trainingSet.add(instance);
            }
        }

        // start evaluating for each algorithm
        for (Classifier cls : mClassifiers) {
            evaluate(cls, trainingSet, mCrossValidationFolds);
        }
    }

    /**
     * Get a Weka-Instance-object for the given aggregated data and class name.
     *
     * @param className
     * @param data
     * @return
     */
    public Instance getInstance(String className, AggregatedData data) {
        int capacity = mAttrs.size();
        Instance instance = new DenseInstance(capacity);

        for (Feature feature : mFeatures) {
            String featureId = feature.getIdentifier();
            Attribute attr = getAttributeByFeature(featureId);
            double value = data.getData(featureId);
            instance.setValue(attr, value);
        }

        instance.setValue(mClassAttribute, className);

        return instance;
    }

    /**
     * Start Weka-evaluation using training- and test-set.
     *
     * @param cls
     * @param trainingSet
     * @param testingSet
     * @throws Exception
     */
    public void evaluate(Classifier cls, Instances trainingSet,
            Instances testingSet) throws Exception {
        cls.buildClassifier(trainingSet);
        Evaluation eval = new Evaluation(trainingSet);
        eval.evaluateModel(cls, testingSet);

        mTrainingTestResults.add(new WekaResult(cls.getClass().getSimpleName(),
                eval, trainingSet));
    }

    /**
     * Start Weka-evaluation using cross-validation.
     *
     * @param cls
     * @param trainingSet
     * @param numFolds
     * @throws Exception
     */
    public void evaluate(Classifier cls, Instances trainingSet, int numFolds)
            throws Exception {
        Evaluation eval = new Evaluation(trainingSet);
        eval.crossValidateModel(cls, trainingSet, numFolds, new Random());

        mCrossValidationResults.add(new WekaResult(cls.getClass()
                .getSimpleName(), eval, trainingSet));
    }

    /**
     * Sets the training set size.
     *
     * @param size Set size between 0 and 1.
     */
    public void setTrainingSetSize(double size) {
        if (size > 1.0 || size <= 0.0) {
            throw new IllegalArgumentException(
                    "Training size must be a value between 0 (exclusive) and 1.");
        }
        mTrainingSetSize = size;
    }

    /**
     * Set the number of folds used for cross-validation.
     *
     * @param numFolds
     */
    public void setCrossValidationFolds(int numFolds) {
        mCrossValidationFolds = numFolds;
    }

    /**
     * Get the results for the cross-validation evaluation.
     *
     * @return
     */
    public List<WekaResult> getCrossValidationResults() {
        return mCrossValidationResults;
    }

    /**
     * Get the results for the evaluation based on training/test set.
     *
     * @return
     */
    public List<WekaResult> getTrainingTestResults() {
        return mTrainingTestResults;
    }
}
