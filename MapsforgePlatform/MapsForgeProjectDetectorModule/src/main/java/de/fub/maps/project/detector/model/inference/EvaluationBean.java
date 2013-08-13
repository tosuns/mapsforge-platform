/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.inference;

import weka.classifiers.Evaluation;

/**
 *
 * @author Serdar
 */
public class EvaluationBean {

    private final Evaluation evaluation;

    public EvaluationBean(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public boolean isDiscardPredictions() {
        return evaluation.getDiscardPredictions();
    }

    public double getWeightedAreaUnderPRC() {
        return evaluation.weightedAreaUnderPRC();
    }

    public final double getNumInstances() {
        return evaluation.numInstances();
    }

    public final double getCoverageOfTestCasesByPredictedRegions() {
        return evaluation.coverageOfTestCasesByPredictedRegions();
    }

    public final double getSizeOfPredictedRegions() {
        return evaluation.sizeOfPredictedRegions();
    }

    public final double getIncorrect() {
        return evaluation.incorrect();
    }

    public final double getPctIncorrect() {
        return evaluation.pctIncorrect();
    }

    public final double getTotalCost() {
        return evaluation.totalCost();
    }

    public final double getAvgCost() {
        return evaluation.avgCost();
    }

    public final double getCorrect() {
        return evaluation.correct();
    }

    public final double getPctCorrect() {
        return evaluation.pctCorrect();
    }

    public final double getUnclassified() {
        return evaluation.unclassified();
    }

    public final double getPctUnclassified() {
        return evaluation.pctUnclassified();
    }

    public final double getErrorRate() {
        return evaluation.errorRate();
    }

    public final double getKappa() {
        return evaluation.kappa();
    }

    public String getRevision() {
        return evaluation.getRevision();
    }

    public final double getCorrelationCoefficient() throws Exception {
        return evaluation.correlationCoefficient();
    }

    public final double getMeanAbsoluteError() {
        return evaluation.meanAbsoluteError();
    }

    public final double getMeanPriorAbsoluteError() {
        return evaluation.meanPriorAbsoluteError();
    }

    public final double getRelativeAbsoluteError() throws Exception {
        return evaluation.relativeAbsoluteError();
    }

    public final double getRootMeanSquaredError() {
        return evaluation.rootMeanSquaredError();
    }

    public final double getRootRelativeSquaredError() {
        return evaluation.rootRelativeSquaredError();
    }

    public final double getPriorEntropy() throws Exception {
        return evaluation.priorEntropy();
    }

    public final double getKBInformation() throws Exception {
        return evaluation.KBInformation();
    }

    public final double getKBMeanInformation() throws Exception {
        return evaluation.KBMeanInformation();
    }

    public final double getKBRelativeInformation() throws Exception {
        return evaluation.KBRelativeInformation();
    }

    public final double getSFPriorEntropy() {
        return evaluation.SFPriorEntropy();
    }

    public final double getSFMeanPriorEntropy() {
        return evaluation.SFMeanPriorEntropy();
    }

    public final double getSFSchemeEntropy() {
        return evaluation.SFSchemeEntropy();
    }

    public final double getSFMeanSchemeEntropy() {
        return evaluation.SFMeanSchemeEntropy();
    }

    public final double getSFEntropyGain() {
        return evaluation.SFEntropyGain();
    }

    public final double getSFMeanEntropyGain() {
        return evaluation.SFMeanEntropyGain();
    }

    public String getToCumulativeMarginDistributionString() throws Exception {
        return evaluation.toCumulativeMarginDistributionString();
    }

    public double getWeightedTruePositiveRate() {
        return evaluation.weightedTruePositiveRate();
    }

    public double getWeightedTrueNegativeRate() {
        return evaluation.weightedTrueNegativeRate();
    }

    public double getWeightedFalsePositiveRate() {
        return evaluation.weightedFalsePositiveRate();
    }

    public double getWeightedFalseNegativeRate() {
        return evaluation.weightedFalseNegativeRate();
    }

    public double getWeightedMatthewsCorrelation() {
        return evaluation.weightedMatthewsCorrelation();
    }

    public double getWeightedRecall() {
        return evaluation.weightedRecall();
    }

    public double getWeightedPrecision() {
        return evaluation.weightedPrecision();
    }

    public double getWeightedFMeasure() {
        return evaluation.weightedFMeasure();
    }

    public double getUnweightedMacroFmeasure() {
        return evaluation.unweightedMacroFmeasure();
    }

    public double getUnweightedMicroFmeasure() {
        return evaluation.unweightedMicroFmeasure();
    }

    public void getUseNoPriors() {
        evaluation.useNoPriors();
    }
}
