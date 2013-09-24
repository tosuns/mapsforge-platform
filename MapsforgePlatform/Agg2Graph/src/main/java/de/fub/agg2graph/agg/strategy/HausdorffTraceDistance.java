package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class HausdorffTraceDistance implements ITraceDistance {

    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.default.dist");
    private double aggReflectionFactor = 4;
    private int maxOutliners = 10;
    private double maxDistance = 5;
    private int maxLookahead = 4;
    private double maxPathDifference = 10;
    private int minLengthFirstSegment = 1;

    /**
     * Compute the difference of a path to the aggregation. This measure only
     * guarantees relative correctness when questioned repeatedly for different
     * paths. A difference of 0 indicates equality while larger values indicate
     * increasingly different paths.
     *
     * @param aggPath
     * @param tracePoints
     * @param startIndex
     * @param dmh
     * @return Object[] { double bestValue, int bestValueLength }
     */
    @Override
    public Object[] getPathDifference(List<AggNode> aggPath,
            List<GPSPoint> tracePoints, int startIndex, IMergeHandler dmh) {
        double bestValue = Double.MIN_VALUE;
        double bestValueLength = 0;
        List<AggNode> aggResult = new ArrayList<AggNode>();
        List<GPSPoint> traceResult = new ArrayList<GPSPoint>();

        List<AggNode> aggLocations = aggPath;
        List<GPSPoint> traceLocations = tracePoints;

        // step 1a: get nearest distance in agg from trace
        int j = startIndex;
        int currentK;
        int bestK;
        double globalBestDistance = -1;
        while (j < traceLocations.size()) {
            double distance = 0;
            double bestDistance = Double.MAX_VALUE;
            ILocation currentNode = traceLocations.get(j);

            // Get the nearest distance to an edge/a point and mark it.
            for (int k = 0; k < aggLocations.size() - 1; k++) {
                distance = GPSCalc.getDistancePointToEdgeMeter(currentNode,
                        aggLocations.get(k), aggLocations.get(k + 1));

                if (bestDistance > distance && distance < getMaxDistance()) {
                    bestDistance = distance;
                }
            }
            if (bestDistance > globalBestDistance
                    && bestDistance < Double.MAX_VALUE) {
                globalBestDistance = bestDistance;
            } else if (bestDistance == Double.MAX_VALUE) {
                break;
            }

            j++;
        }

        // There is absolut no match
        if (globalBestDistance == -1) {
            return null;
        }

        // step 1b: get nearest distance in trace from agg
        int l = 0;
        while (l < aggLocations.size()) {
            AggNode current = aggLocations.get(l);
            double bestDistFrom = Double.MAX_VALUE;
            int bestKFrom = -1;

            // Distance check & get the nearest points
            for (int k = 0; k < Math.min(traceLocations.size() - 1, j); k++) {
                double distFrom = GPSCalc.getDistancePointToEdgeMeter(current,
                        traceLocations.get(k), traceLocations.get(k + 1));
                if (bestDistFrom > distFrom) {
                    bestDistFrom = distFrom;
                    bestKFrom = k;

                }
            }
            // If best distance is higher than max distance
            if (bestDistFrom > getMaxDistance()) {
                break;
            }

            // Update global distance if necessary
            if (bestDistFrom > globalBestDistance
                    && bestDistFrom < Double.MAX_VALUE) {
                globalBestDistance = bestDistFrom;
            }

            // Add the projection to result
            aggResult.add(current);
            if (!traceResult.contains(traceLocations.get(bestKFrom))) {
                traceResult.add(traceLocations.get(bestKFrom));
            }
            if (!traceResult.contains(traceLocations.get(bestKFrom + 1))
                    && GPSCalc.getDistanceTwoPointsMeter(current,
                            traceLocations.get(bestKFrom + 1)) <= getMaxDistance()) {
                traceResult.add(traceLocations.get(bestKFrom + 1));
            }

            l++;
        }

        bestValue = globalBestDistance;
//		bestValueLength = traceLocations.subList(startIndex, Math.min(traceLocations.size() - 1, j)).size();
        bestValueLength = traceResult.size();

        if (aggResult.size() <= 1) {
            return null;
        } else {
            return new Object[]{bestValue, bestValueLength, aggResult,
                traceResult};
        }
    }

    @Override
    public List<ClassObjectEditor> getSettings() {
        List<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>();
        result.add(new ClassObjectEditor(this));
        return result;
    }

    /**
     * @return the aggReflectionFactor
     */
    public double getAggReflectionFactor() {
        return aggReflectionFactor;
    }

    /**
     * @param aggReflectionFactor the aggReflectionFactor to set
     */
    public void setAggReflectionFactor(double aggReflectionFactor) {
        this.aggReflectionFactor = aggReflectionFactor;
    }

    /**
     * @return the maxOutliners
     */
    public int getMaxOutliners() {
        return maxOutliners;
    }

    /**
     * @param maxOutliners the maxOutliners to set
     */
    public void setMaxOutliners(int maxOutliners) {
        this.maxOutliners = maxOutliners;
    }

    /**
     * @return the maxDistance
     */
    public double getMaxDistance() {
        return maxDistance;
    }

    /**
     * @param maxDistance the maxDistance to set
     */
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     * @return the maxLookahead
     */
    public int getMaxLookahead() {
        return maxLookahead;
    }

    /**
     * @param maxLookahead the maxLookahead to set
     */
    public void setMaxLookahead(int maxLookahead) {
        this.maxLookahead = maxLookahead;
    }

    /**
     * @return the maxPathDifference
     */
    public double getMaxPathDifference() {
        return maxPathDifference;
    }

    /**
     * @param maxPathDifference the maxPathDifference to set
     */
    public void setMaxPathDifference(double maxPathDifference) {
        this.maxPathDifference = maxPathDifference;
    }

    /**
     * @return the minLengthFirstSegment
     */
    public int getMinLengthFirstSegment() {
        return minLengthFirstSegment;
    }

    /**
     * @param minLengthFirstSegment the minLengthFirstSegment to set
     */
    public void setMinLengthFirstSegment(int minLengthFirstSegment) {
        this.minLengthFirstSegment = minLengthFirstSegment;
    }

}
