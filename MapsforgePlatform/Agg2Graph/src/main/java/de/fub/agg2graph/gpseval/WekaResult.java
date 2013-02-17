package de.fub.agg2graph.gpseval;

import java.util.LinkedList;
import java.util.List;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 *
 */
public class WekaResult {

    public String mClsName;
    public Evaluation mEval;
    public List<String> mClasses = new LinkedList<>();

    public WekaResult(String clsName, Evaluation eval, Instances trainingSet) {
        mClsName = clsName;
        mEval = eval;

        int numClasses = trainingSet.numClasses();
        for (int i = 0; i < numClasses; i++) {
            String className = trainingSet.classAttribute().value(i);
            mClasses.add(className);
        }
    }
}
