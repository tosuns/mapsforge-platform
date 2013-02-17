package de.fub.agg2graph.gpseval;

import java.util.List;

/**
 *
 */
public class TestResult {

    public Config mCfg;
    public List<WekaResult> mCrossValidationResults;
    public List<WekaResult> mTrainingTestResults;

    public TestResult(Config cfg, List<WekaResult> crossValidationResults, List<WekaResult> trainingTestResults) {
        mCfg = cfg;
        mCrossValidationResults = crossValidationResults;
        mTrainingTestResults = trainingTestResults;
    }
}
