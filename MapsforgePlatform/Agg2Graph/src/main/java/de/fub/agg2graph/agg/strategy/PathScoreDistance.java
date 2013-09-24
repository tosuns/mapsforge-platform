/**
 * *****************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Affero Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Contributors: Johannes Mitlmeier - initial API and implementation
 *****************************************************************************
 */
package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.agg2graph.structs.CartesianCalc;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PathScoreDistance implements ITraceDistance {

    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.default.dist");
    private double aggReflectionFactor = 4;
    private int maxOutliners = 10;
    private double maxDistance = 30;//7.5
    private int maxLookahead = 5;
    private double maxPathDifference = 20;//10
    private int minLengthFirstSegment = 1;
    private double maxAngle = 60;

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
        double bestValue = Double.MAX_VALUE;
        double bestValueLength = 0;
//		List<AggNode> internalPath = new ArrayList<AggNode>();
//		internalPath.addAll(aggPath);

        for (int i = startIndex; i < Math.min(startIndex + 20,
                tracePoints.size()); i++) {
            logger.log(
                    Level.FINER,
                    String.format("Testing %s and %s...", aggPath,
                            tracePoints.subList(startIndex, i + 1)));

            List<AggNode> aggLocations = aggPath;
            List<GPSPoint> traceLocations = tracePoints.subList(startIndex,
                    i + 1);

            // one-element similarity is not interesting
            if (traceLocations.size() <= getMinLengthFirstSegment()
                    || aggLocations.size() <= getMinLengthFirstSegment()) {
                logger.log(Level.FINE, "Too short path");
//				System.out.println("Too Short");
                continue;
            }

            // angle okay?
            double angle = 180;
            if (dmh != null && dmh.getGpsPoints().size() > 0) {
                angle = GPSCalc.getAngleBetweenEdges(dmh.getGpsPoints().get(0),
                        tracePoints.get(i), dmh.getAggNodes().get(0),
                        aggPath.get(aggPath.size() - 1));
            } else {
                angle = GPSCalc.getAngleBetweenEdges(
                        tracePoints.get(startIndex), tracePoints.get(i),
                        aggPath.get(0), aggPath.get(aggPath.size() - 1));
            }

            if (!CartesianCalc.isAngleMax(angle, maxAngle)) {
                logger.log(Level.FINE,
                        String.format("Angle is not good: %.1f", angle));
                continue;
            }

            /*
             * for the new point in the new trace: find minimal distance to all
             * possibly matching edges of the aggregation
             */
            double dist = Double.MAX_VALUE / 2;
            double[] traceToAggDistances = getPointToLineDistances(
                    traceLocations, aggLocations);
            if (!outlinersOkay(traceToAggDistances)) {
                continue;
            }
            dist = Math.min(dist, getAverageDistance(traceToAggDistances));
            double[] aggToTraceDistances = getPointToLineDistances(
                    aggLocations, traceLocations);
            dist = Math.max(dist, dist + 1.0 / getAggReflectionFactor()
                    * getAverageDistance(aggToTraceDistances));

            // value formula
            logger.log(Level.FINE, "dist: {0}", dist);
            // consider length
            double value = dist
                    * Math.pow(0.95,
                            aggLocations.size() + traceLocations.size());
            logger.log(Level.FINE, "value: {0}", value);

            if (value > getMaxPathDifference()) {
                value = Double.MAX_VALUE;
            }
            logger.log(Level.FINE, String.format("Value of path: %.3f", value));
            if (value < bestValue) {
                logger.log(Level.FINE, "best");
                bestValue = value;
                bestValueLength = traceLocations.size();
            }
        }
        return new Object[]{bestValue, bestValueLength};
    }

    private double[] getPointToLineDistances(List<? extends ILocation> from,
            List<? extends ILocation> to) {
        double[] result = new double[from.size()];
        ILocation loc;
        for (int i = 0; i < from.size(); i++) {
            loc = from.get(i);

            result[i] = GPSCalc.getDistancePointToTraceMeter(loc, to)[0];
        }
        return result;
    }

    private boolean outlinersOkay(double[] distances) {
        int outliers = 0;
        double distance;
        for (int dIndex = 0; dIndex < distances.length; dIndex++) {
            distance = distances[dIndex];
            if (distance > getMaxDistance()) {
                if (outliers > getMaxOutliners()) {
                    // this path is not good
                    logger.log(Level.FINE, String.format(
                            "Too many outliners (%d), limit is %d in a row.",
                            outliers + 1, outliers));
                    return false;
                } else if (dIndex == 0) {
                    logger.log(
                            Level.FINE,
                            String.format(
                                    "Outliner point at the start. Distance is %.3f, it should be below %.3f.",
                                    distance, getMaxDistance()));
                    return false;
                } else if (dIndex == distances.length - 1) {
                    logger.log(
                            Level.FINE,
                            String.format(
                                    "Outliner point at the end. Distance is %.3f, it should be below %.3f.",
                                    distance, getMaxDistance()));
                    return false;
                }
                outliers++;
            } else {
                outliers = 0;
            }
        }
        return true;
    }

    private double getAverageDistance(double[] aggToTraceDistances) {
        double sum = 0;
        int maxCounter = 0;
        for (double d : aggToTraceDistances) {
            if (d < Double.MAX_VALUE) {
                sum += d;
            } else {
                maxCounter++;
            }
        }
        return sum / (aggToTraceDistances.length - maxCounter);
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

    /**
     * @return the maxAngle
     */
    public double getMaxAngle() {
        return maxAngle;
    }

    /**
     * @param maxAngle the maxAngle to set
     */
    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }

}
