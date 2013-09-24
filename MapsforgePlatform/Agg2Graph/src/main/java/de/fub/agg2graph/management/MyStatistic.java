package de.fub.agg2graph.management;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyStatistic {

    private double aggLength = 0;
    private int aggPoints = 0;
    private double matchedAggLength = 0;
    private int matchedAggPoints = 0;
    private double traceLength = 0;
    private int tracePoints = 0;
    private double matchedTraceLength = 0;
    private int matchedTracePoints = 0;
    private double newAggLength = 0;
    private int newAggPoints = 0;
    private String name;
    private int number = 0;
    private long runtimeMatch = 0;
    private long runtimeMerge = 0;
    private long memoryUsed = 0;

    public MyStatistic(String name) {
        this.name = name;
    }

    /**
     * Write a file with some statistic value
     *
     * @throws IOException
     */
    public void writefile() throws IOException {
        List<Double> values = wrapValues();
        FileWriter fstream = new FileWriter(name, true);
        BufferedWriter fbw = new BufferedWriter(fstream);
        fbw.write(number++ + "\t");
        for (Double value : values) {
            fbw.write(value + "\t");
        }
        fbw.newLine();
        fbw.close();
    }

    public List<Double> wrapValues() {
        List<Double> values = new ArrayList<Double>();
        values.add(aggLength);
        values.add(new Double(aggPoints));
        values.add(matchedAggLength);
        values.add(new Double(matchedAggPoints));
        values.add(traceLength);
        values.add(new Double(tracePoints));
        values.add(matchedTraceLength);
        values.add(new Double(matchedTracePoints));
        values.add(newAggLength);
        values.add(new Double(newAggPoints));
        values.add(new Double(runtimeMatch));
        values.add(new Double(runtimeMerge));
        values.add(new Double(memoryUsed));
        return values;
    }

    public void setAggLength(double aggLength) {
        this.aggLength += aggLength;
    }

    public void resetAggLength() {
        this.aggLength = 0;
    }

    public void setMatchedAggLength(double matchedAggLength) {
        this.matchedAggLength += matchedAggLength;
    }

    public void resetMatchedAggLength() {
        this.matchedAggLength = 0;
    }

    public void setTraceLength(double traceLength) {
        this.traceLength += traceLength;
    }

    public void resetTraceLength() {
        this.traceLength = 0;
    }

    public void setMatchedTraceLength(double matchedTraceLength) {
        this.matchedTraceLength += matchedTraceLength;
    }

    public void resetMatchedTraceLength() {
        this.matchedTraceLength = 0;
    }

    public void setNewAggLength(double newAgg) {
        this.newAggLength += newAgg;
    }

    public void resetNewAggLength() {
        this.newAggLength = 0;
    }

    public void setRuntimeMatch(long runtime) {
        this.runtimeMatch = runtime;
    }

    public void resetRuntimeMatch() {
        this.runtimeMatch = 0;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public void resetMemoryUsed() {
        this.memoryUsed = 0;
    }

    public void setAggPoints(int aggPoints) {
        this.aggPoints += aggPoints;
    }

    public void resetAggPoints() {
        this.aggPoints = 0;
    }

    public void setMatchedAggPoints(int matchedAggPoints) {
        this.matchedAggPoints += matchedAggPoints;
    }

    public void resetMatchedAggPoints() {
        this.matchedAggPoints = 0;
    }

    public void setTracePoints(int tracePoints) {
        this.tracePoints += tracePoints;
    }

    public void resetTracePoints() {
        this.tracePoints = 0;
    }

    public void setMatchedTracePoints(int matchedTracePoints) {
        this.matchedTracePoints += matchedTracePoints;
    }

    public void resetMatchedTracePoints() {
        this.matchedTracePoints = 0;
    }

    public void setNewAggPoints(int newAggPoints) {
        this.newAggPoints += newAggPoints;
    }

    public void resetNewAggPoints() {
        this.newAggPoints = 0;
    }

    public void setRuntimeMerge(long runtimeMerge) {
        this.runtimeMerge = runtimeMerge;
    }

    public void resetRuntimeMerge() {
        this.runtimeMerge = 0;
    }

    public void resetAll() {
        this.resetAggLength();
        this.resetMatchedAggLength();
        this.resetTraceLength();
        this.resetMatchedTraceLength();
        this.resetNewAggLength();
        this.resetRuntimeMatch();
        this.resetRuntimeMerge();
        this.resetMemoryUsed();
        this.resetAggPoints();
        this.resetMatchedAggPoints();
        this.resetTracePoints();
        this.resetMatchedTracePoints();
        this.resetNewAggPoints();
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAggLength() {
        return aggLength;
    }

    public double getMatchedAggLength() {
        return matchedAggLength;
    }

    public double getTraceLength() {
        return traceLength;
    }

    public double getMatchedTraceLength() {
        return matchedTraceLength;
    }

    public double getNewAggLength() {
        return newAggLength;
    }

    public String getName() {
        return name;
    }

    public long getRuntimeMatch() {
        return runtimeMatch;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public int getAggPoints() {
        return aggPoints;
    }

    public int getMatchedAggPoints() {
        return matchedAggPoints;
    }

    public int getTracePoints() {
        return tracePoints;
    }

    public int getMatchedTracePoints() {
        return matchedTracePoints;
    }

    public int getNewAggPoints() {
        return newAggPoints;
    }

    public long getRuntimeMerge() {
        return runtimeMerge;
    }
}
