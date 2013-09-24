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
import de.fub.agg2graph.input.GPXWriter;
import de.fub.agg2graph.input.SerializeAgg;
import de.fub.agg2graph.management.MyStatistic;
import de.fub.agg2graph.structs.BoundedQueue;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SecondAggregationStrategyAttraction extends AbstractAggregationStrategy {

    MyStatistic statistic;
    int counter = 1;

    public int maxLookahead = Integer.MAX_VALUE;
    public double maxInitDistance = 12.5;

    List<AggNode> lastNodes = new ArrayList<AggNode>();
    List<GPSSegment> lastNewNodes = new ArrayList<GPSSegment>();

    public enum State {

        NO_MATCH, IN_MATCH
    }

    @SuppressWarnings("unused")
    private State state = State.NO_MATCH;

    /**
     * Preferably use the {@link AggregationStrategyFactory} for creating
     * instances of this class.
     */
    public SecondAggregationStrategyAttraction() {
        statistic = new MyStatistic(
                "test/exp/Evaluation-SecondMatchAttractionMerge.txt");
        TraceDistanceFactory.setClass(ConformalPathDistance.class);
        traceDistance = TraceDistanceFactory.getObject();
        MergeHandlerFactory.setClass(AttractionForceMerge.class);
        baseMergeHandler = MergeHandlerFactory.getObject();
    }

    @SuppressWarnings("unchecked")
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
        if (aggContainer.getCachingStrategy() == null
                || aggContainer.getCachingStrategy().getNodeCount() == 0
                || isAgg) {
            int i = 0;
            while (i < segment.size()) {
                GPSPoint pointI = segment.get(i);
                AggNode node = new AggNode(pointI, aggContainer);
                node.setK(pointI.getK());
                node.setRelevant(pointI.isRelevant());
                node.setID("A-" + pointI.getID());
                addNodeToAgg(aggContainer, node);
                lastNode = node;
                i++;
            }
            lastNodes.add(lastNode);
            statistic.setAggLength(GPSCalc.traceLengthMeter(segment));
            statistic.setAggPoints(segment.size());
            return;
        }

        BoundedQueue<ILocation> lastParsedCurrentPoints = new BoundedQueue<ILocation>(
                5);

        GPSSegment copySegment = new GPSSegment(segment);
        GPSPoint lastCurrentPoint = null;

        int i = 0;

        statistic.setTraceLength(GPSCalc.traceLengthMeter(segment));
        statistic.setTracePoints(segment.size());

        long matchStart = System.currentTimeMillis();
        while (i < copySegment.size()) {
            // step 1: find starting point
            // get close points, within 10 meters (merge candidates)
            Set<AggNode> nearPoints = null;
            GPSPoint currentPoint = copySegment.get(i);

            // avoiding loop
            if (lastCurrentPoint == null) {
                lastCurrentPoint = currentPoint;
            } else if (lastCurrentPoint.equals(currentPoint)) {
                i++;
                continue;
            } else {
                lastCurrentPoint = currentPoint;
            }

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

            // State lastState = state;
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
            if (nearPoints.size() == 0) {
                i++;
                continue;
            } else {
                // get only nearest Point
                AggNode nearest = nearestPoint(currentPoint, nearPoints);
                // unnecessary, but needed atm
                Set<AggNode> nearestSet = new HashSet<AggNode>();
                nearestSet.add(nearest);
                // there is candidates for a match start
                List<List<AggNode>> paths = getPathsByDepth(nearestSet, 1,
                        maxLookahead);

                /* Tinus - Filtering Paths */
                removeSamePath(paths);
                for (List<AggNode> path : paths) {
                    filterPath(path);
                }

                // evaluate paths, pick best, continue
                List<List<AggNode>> bestAggResult = new ArrayList<List<AggNode>>();
                List<List<GPSPoint>> bestTraceResult = new ArrayList<List<GPSPoint>>();
                double value, bestValue = 0;

                for (List<AggNode> path : paths) {
                    Object[] returnValues = traceDistance.getPathDifference(
                            path, copySegment, i, mergeHandler);
                    List<List<AggNode>> aggResult = (List<List<AggNode>>) returnValues[1];
                    List<List<GPSPoint>> traceResult = (List<List<GPSPoint>>) returnValues[2];
                    if (aggResult.isEmpty() || traceResult.isEmpty()
                            || aggResult.size() != traceResult.size()) {
                        continue;
                    }

                    value = getValue(aggResult, traceResult);
                    if (value > bestValue) {
                        bestValue = value;
                        bestAggResult = aggResult;
                        bestTraceResult = traceResult;
                    }
                }

                // do we have a successful match?
                if (!validate(segment, copySegment, bestAggResult,
                        bestTraceResult)) {
                    isMatch = false;
                    i++;
                }

                state = isMatch ? State.IN_MATCH : State.NO_MATCH;
                if (isMatch) {
                    for (int j = 0; j < Math.min(bestAggResult.size(),
                            bestTraceResult.size()); j++) {
                        List<AggNode> aggMatched = bestAggResult.get(j);
                        List<GPSPoint> traceMatched = bestTraceResult.get(j);

                        // Size only 1 --> delete
                        if (aggMatched.size() <= 1 || traceMatched.size() <= 1) {
                            deleteMatchedTraces(copySegment,
                                    bestTraceResult.get(j), 2);
                            bestAggResult.remove(j);
                            bestTraceResult.remove(j);
                            continue;
                        }

                        mergeHandler = baseMergeHandler.getCopy();
                        mergeHandler.setAggContainer(aggContainer);

                        mergeHandler.addAggNodes(aggMatched);
                        mergeHandler.addGPSPoints(traceMatched);
                        mergeHandler.setDistance(0); // TODO
                        finishMatch();

                        // Delete the matched traces
                        if (j == 0) {
                            deleteMatchedTraces(copySegment,
                                    bestTraceResult.get(j), 0);
                        } else {
                            deleteMatchedTraces(copySegment,
                                    bestTraceResult.get(j), 1);
                        }
                    }
                    i = 0;
                }
            }
        }
        long matchEnd = System.currentTimeMillis();
        statistic.setRuntimeMatch(matchEnd - matchStart);

        // New Segment
        if (getAddAllowed()) {
            i = 0;
            lastNode = null;
            GPSSegment lastNewNode = new GPSSegment();
            while (i < copySegment.size()) {
                GPSPoint currentPoint = copySegment.get(i);
                if (lastNode == null) {
                    AggNode node = new AggNode(currentPoint, aggContainer);
                    node.setID("A-" + currentPoint.getID());
                    node.setK(1);
                    addNodeToAgg(aggContainer, node);
                    lastNode = node;
                    lastNewNode.add(0, node);
                } else {
                    if (GPSCalc.getDistanceTwoPointsMeter(lastNode,
                            currentPoint) < 100) {
                        AggNode node = new AggNode(currentPoint, aggContainer);
                        node.setID("A-" + currentPoint.getID());
                        node.setK(1);
                        addNodeToAgg(aggContainer, node);
                        lastNode = node;
                        lastNewNode.add(0, node);
                        if (i == copySegment.size() - 1) {
//							lastNodes.add(lastNode);
                            lastNewNodes.add(lastNewNode);
                        }
                    } else {
//						lastNodes.add(lastNode);
                        lastNode = null;
                        lastNewNodes.add(lastNewNode);
                        lastNewNode = new GPSSegment();
                        i--;
                    }
                }

                i++;
            }
        }

        // step 2 and 3 of 3: ghost points, merge everything
        System.out.println(counter + ". MATCHES : " + matches.size());
        System.out.println("New Segment : " + getAddAllowed());
        statistic.resetMatchedAggLength();
        statistic.resetMatchedAggPoints();
        statistic.resetMatchedTraceLength();
        statistic.resetMatchedTracePoints();

        long mergeStart = System.currentTimeMillis();
        for (IMergeHandler match : matches) {
            statistic.setMatchedAggLength(GPSCalc.traceLengthMeter(match
                    .getAggNodes()));
            statistic.setMatchedAggPoints(match.getAggNodes().size());
            statistic.setMatchedTraceLength(GPSCalc.traceLengthMeter(match
                    .getGpsPoints()));
            statistic.setMatchedTracePoints(match
                    .getGpsPoints().size());
            if (!match.isEmpty()) {
                match.mergePoints();
            }
        }
        long mergeEnd = System.currentTimeMillis();
        statistic.setRuntimeMerge(mergeEnd - mergeStart);

        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();

        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        statistic.setMemoryUsed(bytesToMegabytes(memory));

        for (GPSSegment lastNewNode : lastNewNodes) {
            statistic.setNewAggLength(GPSCalc.traceLengthMeter(lastNewNode));
            statistic.setNewAggPoints(lastNewNode.size());
        }

        /**
         * Save new Map
         */
        try {
            List<GPSSegment> segments = new ArrayList<GPSSegment>();
            for (AggNode last : lastNodes) {
                segments.add(SerializeAgg.getSegmentFromLastNode(last));
            }

            // Extension
            if (lastNewNodes.size() > 0 && getAddAllowed()) {
                segments.addAll(lastNewNodes);
            }

            GPXWriter.writeSegments(new File(("test/input/map 2.0a/"
                    + "map" + counter++ + ".gpx")), segments);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /**
         * Statistic record
         */
        try {
            statistic.writefile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        statistic.resetAll();
        lastNodes.clear();
        lastNewNodes.clear();
    }

    private boolean validate(GPSSegment segment, GPSSegment copySegment,
            List<List<AggNode>> bestAggResult,
            List<List<GPSPoint>> bestTraceResult) {
        for (int j = 0; j < Math.min(bestAggResult.size(),
                bestTraceResult.size()); j++) {
            List<GPSPoint> traceMatched = bestTraceResult.get(j);

            if (!checkTraceDistance(segment, traceMatched)) {
                bestAggResult.remove(j);
                bestTraceResult.remove(j);
            }
        }

        return !bestAggResult.isEmpty() && !bestTraceResult.isEmpty();
    }

    private boolean checkTraceDistance(GPSSegment segment,
            List<GPSPoint> traceMatched) {
        GPSPoint last = null;
        for (GPSPoint element : traceMatched) {
            if (last == null) {
                last = element;
                continue;
            }
            int lastIndex = segment.indexOf(last);
            int currentIndex = segment.indexOf(element);
            if (currentIndex - lastIndex > 1) {
                return false;
            }

            last = element;
        }
        return true;
    }

    private void deleteMatchedTraces(GPSSegment copySegment,
            List<GPSPoint> list, int mode) {
        // first match
        if (mode == 0) {
            for (int i = 0; i < list.size() - 1; i++) {
                GPSPoint element = list.get(i);
                copySegment.remove(element);
            }
        } // second and other match
        else if (mode == 1) {
            for (int i = 1; i < list.size() - 1; i++) {
                GPSPoint element = list.get(i);
                copySegment.remove(element);
            }
        } // fail match
        else {
            for (int i = 0; i < list.size(); i++) {
                GPSPoint element = list.get(i);
                copySegment.remove(element);
            }
        }
    }

    private double getValue(List<List<AggNode>> aggResult,
            List<List<GPSPoint>> traceResult) {
        double value = 0;
        for (int i = 0; i < Math.min(aggResult.size(), traceResult.size()); i++) {
            value = value
                    + GPSCalc
                    .traceLengthMeter((List<? extends ILocation>) aggResult
                            .get(i))
                    + GPSCalc
                    .traceLengthMeter((List<? extends ILocation>) traceResult
                            .get(i));
        }
        return value;
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

    protected void finishMatch() {
        // last match is over now
        matches.add(mergeHandler);
        mergeHandler.processSubmatch();
        /*
         * connect to previous node lastNode is the last non-matched node or the
         * outNode of the last match
         */
        // aggContainer.connect(lastNode, mergeHandler.getInNode());
        // mergeHandler.setBeforeNode(lastNode);
        // remember outgoing node (for later connection)
        // lastNode = mergeHandler.getOutNode();
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
        return "SecondAggregation-Attraction";
    }

    private static AggNode nearestPoint(ILocation current,
            Set<AggNode> nearPoints) {
        double bestDistance = Double.MAX_VALUE;
        double distance;
        AggNode best = null;
        for (AggNode point : nearPoints) {
            distance = GPSCalc.getDistanceTwoPointsMeter(current, point);
            if (bestDistance > distance) {
                bestDistance = distance;
                best = point;
            }
        }

        return best;
    }

    /**
     * to remove same path. Bug from addPaths
     *
     * @param paths
     */
    private void removeSamePath(List<List<AggNode>> paths) {
        for (int i = 0; i < paths.size(); i++) {
            for (int j = 0; j < paths.size(); j++) {
                if (paths.get(i).containsAll(paths.get(j)) && i != j) {
                    paths.remove(j);
                    if (i > j) {
                        i--;
                    }
                    j--;
                }
            }
        }
    }

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
}
