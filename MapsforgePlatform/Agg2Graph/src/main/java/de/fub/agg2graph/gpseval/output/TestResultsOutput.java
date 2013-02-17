package de.fub.agg2graph.gpseval.output;

import de.fub.agg2graph.gpseval.TestResult;
import java.nio.file.Path;
import java.util.List;

/**
 *
 */
public abstract class TestResultsOutput {

    protected List<TestResult> mResults;
    protected Path mResultsFolder;

    public TestResultsOutput init(List<TestResult> results, Path resultsFolder) {
        mResults = results;
        mResultsFolder = resultsFolder;
        return this;
    }

    public abstract void run();
}
