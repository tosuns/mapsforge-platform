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
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GpxmergeMatchAttractionMergeStrategy extends
        AbstractAggregationStrategy {

    MyStatistic statistic;
    int counter = 1;

    private int maxLookahead = 5;
    private double maxPathDifference = 40;
    private double maxInitDistance = 12.5;

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
    public GpxmergeMatchAttractionMergeStrategy() {
        statistic = new MyStatistic(
                "agg2graph/test/exp/Evaluation-GPXMergeAttractionMerge.txt");
        TraceDistanceFactory.setClass(GpxmergeTraceDistance.class);
        traceDistance = TraceDistanceFactory.getObject();
        MergeHandlerFactory.setClass(AttractionForceMerge.class);
        baseMergeHandler = MergeHandlerFactory.getObject();
    }

    public int getMaxLookahead() {
        return maxLookahead;
    }

    public void setMaxLookahead(int maxLookahead) {
        this.maxLookahead = maxLookahead;
    }

    public double getMaxPathDifference() {
        return maxPathDifference;
    }

    public void setMaxPathDifference(double maxPathDifference) {
        this.maxPathDifference = maxPathDifference;
    }

    public double getMaxInitDistance() {
        return maxInitDistance;
    }

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

        int i = 0;
        Set<AggConnection> nearEdges = null;
        statistic.setTraceLength(GPSCalc.traceLengthMeter(segment));
        statistic.setTracePoints(segment.size());
        long matchStart = System.currentTimeMillis();
        while (i < segment.size()) {
            State lastState = state;

            if ((i == segment.size() - 1)) {
                if (lastState == State.IN_MATCH) {
                    finishMatch();
                }
                break;
            }
            // step 1: find starting point
            // get close points, within 10 meters (merge candidates)
            GPSPoint firstPoint = segment.get(i);
            GPSPoint secondPoint = segment.get(i + 1);
            GPSEdge currentEdge = new GPSEdge(firstPoint, secondPoint);

            nearEdges = aggContainer.getCachingStrategy().getCloseConnections(
                    currentEdge, maxInitDistance);
            /* TODO Tinus - Filtering near points */
            nearEdges = filterNearPoints(nearEdges);

            boolean isMatch = true;
            if (nearEdges.isEmpty()) {
                isMatch = false;
                state = State.NO_MATCH;
            } else {
                Iterator<AggConnection> itNear = nearEdges.iterator();
                Double grade = Double.MAX_VALUE;
                AggConnection bestConn = null;
                double dist = Double.MAX_VALUE;
                while (itNear.hasNext()) {
                    AggConnection near = itNear.next();
                    Object[] distReturn = traceDistance.getPathDifference(
                            near.toPointList(), segment, i, mergeHandler);
                    dist = (Double) distReturn[0];
                    if (dist < maxPathDifference && dist < grade) {
                        grade = dist;
                        bestConn = near;
                    }
                }

                // do we have a successful match?
                if (bestConn == null) {
                    isMatch = false;
                }
                // else if (bestPath.size() <= 1 && bestPathLength <= 1) {
                //
                // }

                state = isMatch ? State.IN_MATCH : State.NO_MATCH;
                if (isMatch) {
                    // System.out.println("I = " + i);
                    // System.out.println(bestConn.getFrom() + " : " +
                    // bestConn.getTo());
                    // System.out.println(currentEdge.getFrom() + " : " +
                    // currentEdge.getTo());
                    // make a merge handler if the match would start here
                    if (lastState == State.NO_MATCH) {
                        mergeHandler = baseMergeHandler.getCopy();
                        mergeHandler.setAggContainer(aggContainer);
                    }
                    // isMatch = false;
                    if (!mergeHandler.getAggNodes()
                            .contains(bestConn.getFrom())) {
                        mergeHandler.addAggNode(bestConn.getFrom());
                    }
                    if (!mergeHandler.getAggNodes().contains(bestConn.getTo())) {
                        mergeHandler.addAggNode(bestConn.getTo());
                    }
                    if (!mergeHandler.getGpsPoints().contains(currentEdge.getFrom()));
                    mergeHandler.addGPSPoint(currentEdge.getFrom());
                    if (!mergeHandler.getGpsPoints().contains(currentEdge.getTo())) ;
                    mergeHandler.addGPSPoint(currentEdge.getTo());

                    mergeHandler.setDistance(grade);
                    i++;
                }
            }

            if (!isMatch
                    && (lastState == State.IN_MATCH && (state == State.NO_MATCH || i == segment
                    .size() - 1))) {
                finishMatch();
                i++;
            } else if (!isMatch && lastState == State.NO_MATCH) {
                // if there is no close points or no valid match, add it to the
                // aggregation
                if (getAddAllowed()) {
                    GPSPoint currentPoint = segment.get(i);
                    AggNode node = new AggNode(currentPoint, aggContainer);
                    node.setID("A-" + currentPoint.getID());
                    node.setK(1);
                    addNodeToAgg(aggContainer, node);
                    lastNode = node;
                }
                i++;
            }
        }
        long matchEnd = System.currentTimeMillis();
        statistic.setRuntimeMatch(matchEnd - matchStart);

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
        System.out.println(counter + ". MATCHES : " + matches.size());
        System.out.println("New Segment : " + getAddAllowed());
        statistic.resetMatchedAggLength();
        statistic.resetMatchedAggPoints();
        statistic.resetMatchedTraceLength();
        statistic.resetMatchedTracePoints();

        long mergeStart = System.currentTimeMillis();
        for (IMergeHandler match : matches) {
//			System.out.println(match.getAggNodes());
//			System.out.println(match.getGpsPoints());
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

        /**
         * Save new Map
         */
//		try {
//			List<GPSSegment> segments = new ArrayList<GPSSegment>();
//			for (AggNode last : lastNodes) {
//				segments.add(SerializeAgg.getSegmentFromLastNode(last));
//			}
//
//			// Extension
//			if (lastNewNodes.size() > 0 && getAddAllowed())
//				segments.addAll(lastNewNodes);
//
//			GPXWriter.writeSegments(new File(new String("agg2graph/test/input/map 2.0a/"
//					+ "map" + counter++ + ".gpx")), segments);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		for(GPSSegment lastNewNode : lastNewNodes) {
//			statistic.setNewAggLength(GPSCalc.traceLengthMeter(lastNewNode));
//			statistic.setNewAggPoints(lastNewNode.size());
//		}
        /**
         * Statistic record
         */
//		try {
//			statistic.writefile();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        statistic.resetAll();
        lastNodes.clear();
        lastNewNodes.clear();
    }

    private Set<AggConnection> filterNearPoints(Set<AggConnection> nearEdges) {
        Iterator<AggConnection> nearIt = nearEdges.iterator();
        while (nearIt.hasNext()) {
            if (!nearIt.next().isRelevant()) {
                nearIt.remove();
            }
        }
        return nearEdges;
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
        // // remember outgoing node (for later connection)
        // lastNode = mergeHandler.getOutNode();
    }

    @Override
    public String toString() {
        return "GPXMatch-AttractionMerge";
    }

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
}
