package de.fub.agg2graph.gpseval;

import java.util.LinkedList;
import java.util.List;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * This class is used to store the results of a Weka-evaluation.
 */
public class WekaResult {

	/**
	 * The name of the classifier used for the evaluation.
	 */
	public String mClsName;

	/**
	 * The weka.classifiers.Evaluation-object containing the results.
	 */
	public Evaluation mEval;

	/**
	 * The names of the classes used in the Evaluation.
	 */
	public List<String> mClasses = new LinkedList<>();

	/**
	 * 
	 * @param clsName
	 *            The name of the classifier used for the evaluation.
	 * @param eval
	 *            The weka.classifiers.Evaluation-object containing the results.
	 * @param trainingSet
	 *            The instances from which the class-names are taken from.
	 */
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
