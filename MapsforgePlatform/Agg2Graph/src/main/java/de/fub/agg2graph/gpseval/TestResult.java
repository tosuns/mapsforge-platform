package de.fub.agg2graph.gpseval;

import java.util.List;

/**
 * This class is used to store the results of a
 * {@link de.fub.agg2graph.gpseval.TestCase TestCase}.
 */
public class TestResult {

	/**
	 * The {@link de.fub.agg2graph.gpseval.Config Config} used for the
	 * {@link de.fub.agg2graph.gpseval.TestCase TestCase} to which this result
	 * belongs to.
	 */
	public Config mCfg;

	/**
	 * The results for the cross validation test.
	 */
	public List<WekaResult> mCrossValidationResults;

	/**
	 * The results for the training/test-set method.
	 */
	public List<WekaResult> mTrainingTestResults;

	public TestResult(Config cfg, List<WekaResult> crossValidationResults,
			List<WekaResult> trainingTestResults) {
		mCfg = cfg;
		mCrossValidationResults = crossValidationResults;
		mTrainingTestResults = trainingTestResults;
	}
}
