/**
 * *****************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Affero Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Contributors: Johannes Mitlmeier - initial API and implementation
 * ****************************************************************************
 */
package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.AggregationStrategyFactory;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.MergeHandlerFactory;
import de.fub.agg2graph.agg.TraceDistanceFactory;
import de.fub.agg2graph.management.MyStatistic;
import de.fub.agg2graph.structs.BoundedQueue;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PathScoreMatchDefaultMergeStrategy extends
        AbstractAggregationStrategy {

    MyStatistic statistic;
    int counter = 1;

    private int maxLookahead = 7;
    private double maxPathDifference = 1000;
    private double maxInitDistance = 150;//12.5

    List<AggNode> lastNodes = new ArrayList<AggNode>();
    List<GPSSegment> lastNewNodes = new ArrayList<GPSSegment>();

    public enum State {

        NO_MATCH, IN_MATCH
    }

    private State state = State.NO_MATCH;

    /**
     * Preferably use the {@link AggregationStrategyFactory} for creating
     * instances of this class.
     */
    public PathScoreMatchDefaultMergeStrategy() {
        statistic = new MyStatistic(
                "agg2graph/test/exp/Evaluation-DefaultMatchDefaultMerge.txt");
        TraceDistanceFactory.setClass(PathScoreDistance.class);
        traceDistance = TraceDistanceFactory.getObject();
        MergeHandlerFactory.setClass(WeightedClosestPointMerge.class);
        baseMergeHandler = MergeHandlerFactory.getObject();
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
     * @return the maxInitDistance
     */
    public double getMaxInitDistance() {
        return maxInitDistance;
    }

    /**
     * @param maxInitDistance the maxInitDistance to set
     */
    public void setMaxInitDistance(double maxInitDistance) {
        this.maxInitDistance = maxInitDistance;
    }

    @Override
    public void aggregate(GPSSegment segment, boolean isAgg) {
        // reset all attributes
        lastNode = null;
        mergeHandler = null;
        matches = new ArrayList<IMergeHandler>();
        state = State.NO_MATCH;

        // insert first segment without changes (assuming somewhat cleaned
        // data!)
        // attention: node counter is not necessarily accurate!
        //
        if (aggContainer.getCachingStrategy() == null
                || aggContainer.getCachingStrategy().getNodeCount() == 0
                || isAgg) {
            int i = 0;
            clear();
            while (i < segment.size()) {
                GPSPoint pointI = segment.get(i);
                AggNode node = new AggNode(pointI, aggContainer);
                node.setK(pointI.getK());
                node.setRelevant(pointI.isRelevant());
                node.setID(MessageFormat.format("A-{0}", pointI.getID()));
                addNodeToAgg(aggContainer, node);
                lastNode = node;
                i++;
            }
            lastNodes.add(lastNode);
            return;
        }

        BoundedQueue<ILocation> lastParsedCurrentPoints = new BoundedQueue<ILocation>(
                5);

        int i = 0;

        while (i < segment.size()) {
            // step 1: find starting point
            // get close points, within 10 meters (merge candidates)
            Set<AggNode> nearPoints = null;
            GPSPoint currentPoint = segment.get(i);

            // no progress? (should not be necessary)
            if (lastParsedCurrentPoints.size() > 2
                    && lastParsedCurrentPoints.get(
                            lastParsedCurrentPoints.size() - 1).equals(
                            currentPoint)
                    && lastParsedCurrentPoints.get(
                            lastParsedCurrentPoints.size() - 2).equals(
                            currentPoint)) {
                i++;
                continue;
            }
            lastParsedCurrentPoints.offer(currentPoint);

            State lastState = state;

            // get all close points, but none that are already in the current
            // match (because we would kinda search backwards)
            nearPoints = aggContainer.getCachingStrategy().getCloseNodes(
                    currentPoint, maxInitDistance);
            if (mergeHandler != null) {
                List<AggNode> nodes = mergeHandler.getAggNodes();
                for (int j = 0; j < nodes.size() - 1; j++) {
                    nearPoints.remove(nodes.get(j));
                }
            }

            /* Tinus - Filtering near points */
            nearPoints = filterNearPoints(nearPoints);

            boolean isMatch = true;
            if (nearPoints.isEmpty()) {
                isMatch = false;
                state = State.NO_MATCH;
            } else {
                // there is candidates for a match start
                List<List<AggNode>> paths = getPathsByDepth(nearPoints, 1,
                        maxLookahead);

                /* Tinus - Filtering Paths */
                for (List<AggNode> path : paths) {
                    filterPath(path);
                }

                // evaluate paths, pick best, continue
                double bestDifference = Double.MAX_VALUE, difference;
                int length;
                List<AggNode> bestPath = null;
                int bestPathLength = 0;

                for (List<AggNode> path : paths) {
                    Object[] returnValues = traceDistance.getPathDifference(path, segment, i, mergeHandler);
                    difference = (Double) returnValues[0];
                    length = (int) Math.round(Double.valueOf(returnValues[1]
                            .toString()));
                    if (difference < bestDifference
                            || (difference == bestDifference && length > bestPathLength)) {
                        bestDifference = difference;
                        bestPathLength = length;
                        bestPath = path;
                        if (bestPath.isEmpty()) {
                            int j = i;
                            i = j;
                        }
                    }
                }

                // do we have a successful match?
                if (bestDifference >= maxPathDifference || bestPath == null) {
                    isMatch = false;
                } else if (bestPath.size() <= 1 && bestPathLength <= 1) {
                    isMatch = false;
                }

                state = isMatch ? State.IN_MATCH : State.NO_MATCH;
                if (isMatch) {
                    // make a merge handler if the match would start here
                    if (lastState == State.NO_MATCH) {
                        mergeHandler = baseMergeHandler.getCopy();
                        mergeHandler.setAggContainer(aggContainer);
                    }
                    mergeHandler.addAggNodes(bestPath);
                    mergeHandler.addGPSPoints(segment.subList(i, i
                            + bestPathLength));
                    mergeHandler.setDistance(bestDifference);
                    i = i + bestPathLength - 1;
                }
            }

            if (!isMatch
                    && (lastState == State.IN_MATCH && (state == State.NO_MATCH || i == segment
                    .size() - 1))) {
                finishMatch();
            } else if (!isMatch && lastState == State.NO_MATCH) {
                // if there is no close points or no valid match, add it to the
                // aggregation

                AggNode node = new AggNode(currentPoint, aggContainer);
                node.setID("A-" + currentPoint.getID());
                node.setK(1);
                addNodeToAgg(aggContainer, node);
                lastNode = node;
                i++;
            }
        }

        // New Segment
        if (getAddAllowed() && lastNode != null) {
            List<AggNode> newSegment = new ArrayList<AggNode>();
            AggNode currentLast = lastNode;
            newSegment.add(0, currentLast);
            while (!lastNode.getIn().isEmpty()) {
                lastNode = lastNode.getIn().iterator().next().getFrom();
                if (GPSCalc.getDistanceTwoPointsMeter(currentLast, lastNode) < 100) {
                    newSegment.add(0, lastNode);
                } else {
                    if (newSegment.size() > 1) {
                        lastNewNodes.add(new GPSSegment(newSegment));
                    }
                    newSegment = new ArrayList<AggNode>();
                }
                currentLast = lastNode;
            }
            if (newSegment.size() > 1) {
                lastNewNodes.add(new GPSSegment(newSegment));
            }
        }

        // step 2 and 3 of 3: ghost points, merge everything
        System.out.println(MessageFormat.format("{0}. MATCHES : {1}", counter, matches.size()));
        System.out.println(MessageFormat.format("New Segment : {0}", getAddAllowed()));

        for (IMergeHandler match : matches) {
            if (!match.isEmpty()) {
                match.mergePoints();
            }
        }
    }

    private static void filterPath(List<AggNode> path) {
        boolean found = false;
        for (int i = 0; i < path.size(); i++) {
            if (!found && !path.get(i).isRelevant()) {
                found = true;
            } else if (found) {
                path.remove(i);
                i--;
            }
        }
    }

    private Set<AggNode> filterNearPoints(Set<AggNode> nearPoints) {
        Iterator<AggNode> nearIt = nearPoints.iterator();
        while (nearIt.hasNext()) {
            if (!nearIt.next().isRelevant()) {
                nearIt.remove();
            }
        }
        return nearPoints;
    }

    protected void finishMatch() {
        // last match is over now
        matches.add(mergeHandler);
        mergeHandler.processSubmatch();
        /*
         * connect to previous node lastNode is the last non-matched node or the
         * outNode of the last match
         */
        aggContainer.connect(lastNode, mergeHandler.getInNode());
        mergeHandler.setBeforeNode(lastNode);
        // remember outgoing node (for later connection)
        lastNode = mergeHandler.getOutNode();
    }

    /*
     * reverse paths
     */
    private List<List<AggNode>> getPathsByDepth(Set<AggNode> nearPoints,
            int minDepth, int maxDepth) {
        List<List<AggNode>> paths = new ArrayList<List<AggNode>>();
        for (AggNode startNode : nearPoints) {
            List<AggNode> path = new ArrayList<AggNode>();
            path.add(startNode);
            addPaths(paths, path, 1, minDepth, maxDepth);
        }
        return paths;
    }

    private void addPaths(List<List<AggNode>> paths, List<AggNode> path,
            int depth, int minDepth, int maxDepth) {
        if (depth > maxDepth) {
            return;
        }
        // add out nodes
        // TODO load node if necessary instead of null check
        if (path.get(depth - 1).getOut() != null) {
            for (AggConnection outConn : path.get(depth - 1).getOut()) {
                AggNode outNode = outConn.getTo();
                path.add(outNode);
                if (depth >= minDepth) {
                    ArrayList<AggNode> pathCopy = new ArrayList<AggNode>();
                    pathCopy.addAll(path);
                    paths.add(pathCopy);
                }
                addPaths(paths, path, depth + 1, minDepth, maxDepth);
                path.remove(path.size() - 1);
            }
        }
    }

    @Override
    public String toString() {
        return "DefaultMatch-AttractionMerge";
    }

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
}
