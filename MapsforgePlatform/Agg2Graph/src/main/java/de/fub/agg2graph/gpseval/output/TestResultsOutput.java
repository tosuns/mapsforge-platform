package de.fub.agg2graph.gpseval.output;

import de.fub.agg2graph.gpseval.TestResult;
import java.nio.file.Path;
import java.util.List;

/**
 * A TestResultOutput is used to export the results of test-cases.
 */
public abstract class TestResultsOutput {

	protected List<TestResult> mResults;
	protected Path mResultsFolder;

	/**
	 * 
	 * @param results
	 *            The results of the test cases.
	 * @param resultsFolder
	 *            The folder where the results should be stored to.
	 * @return
	 */
	public TestResultsOutput init(List<TestResult> results, Path resultsFolder) {
		mResults = results;
		mResultsFolder = resultsFolder;
		return this;
	}

	/**
	 * Start export.
	 */
	public abstract void run();
}
