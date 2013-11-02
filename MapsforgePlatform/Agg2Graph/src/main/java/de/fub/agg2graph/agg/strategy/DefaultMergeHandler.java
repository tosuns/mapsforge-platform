/**
 * *****************************************************************************
 * Copyright 2013 Johannes Mitlmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggCleaner;
import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.PointGhostPointPair;
import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.structs.CartesianCalc;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jscience.mathematics.vector.Float64Vector;

public class DefaultMergeHandler implements IMergeHandler {

    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.default.merge");
    private List<AggNode> aggNodes = null;
    private List<GPSPoint> gpsPoints = null;
    private int maxLookahead = 4;
    private double minContinuationAngle = 45;
    // helper stuff
    private Map<AggConnection, List<PointGhostPointPair>> newNodesPerConn;
    private List<PointGhostPointPair> pointGhostPointPairs;
    private AggNode inNode;
    private AggNode outNode;
    private AggContainer aggContainer;
    private RenderingOptions roMatchGPS;
    // cleaning stuff
    private final RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(0, 125);
    private static AggCleaner cleaner = new AggCleaner().enableDefault();
    private double maxPointGhostDist = 40; // meters
    private double distance = 0;
    private AggNode beforeNode;

    public DefaultMergeHandler() {
        // debugging
        logger.setLevel(Level.ALL);
        roMatchGPS = new RenderingOptions();
        roMatchGPS.setColor(Color.PINK);
        logger.setLevel(Level.OFF);

        aggNodes = new ArrayList<AggNode>();
        gpsPoints = new ArrayList<GPSPoint>();
    }

    public DefaultMergeHandler(AggContainer aggContainer) {
        this();

        this.aggContainer = aggContainer;
    }

    public DefaultMergeHandler(AggContainer aggContainer,
            List<AggNode> aggNodes, List<GPSPoint> gpsPoints) {
        this(aggContainer);
        this.aggNodes = aggNodes;
        this.gpsPoints = gpsPoints;
    }

    public int getMaxLookahead() {
        return maxLookahead;
    }

    public void setMaxLookahead(int maxLookahead) {
        this.maxLookahead = maxLookahead;
    }

    public double getMinContinuationAngle() {
        return minContinuationAngle;
    }

    public void setMinContinuationAngle(double minContinuationAngle) {
        this.minContinuationAngle = minContinuationAngle;
    }

    public double getMaxPointGhostDist() {
        return maxPointGhostDist;
    }

    public void setMaxPointGhostDist(double maxPointGhostDist) {
        this.maxPointGhostDist = maxPointGhostDist;
    }

    public double getEpsilon() {
        return rdpf.getEpsilon();
    }

    public double getMaxSegmentLength() {
        return rdpf.getMaxSegmentLength();
    }

    public void setMaxSegmentLength(double maxSegmentLength) {
        rdpf.setMaxSegmentLength(maxSegmentLength);
    }

    @Override
    public List<PointGhostPointPair> getPointGhostPointPairs() {
        return pointGhostPointPairs;
    }

    @Override
    public AggContainer getAggContainer() {
        return aggContainer;
    }

    @Override
    public void setAggContainer(AggContainer aggContainer) {
        this.aggContainer = aggContainer;
    }

    @Override
    public List<AggNode> getAggNodes() {
        return aggNodes;
    }

    @Override
    public void addAggNode(AggNode aggNode) {
        if (this.aggNodes.size() > 0
                && this.aggNodes.get(this.aggNodes.size() - 1).equals(aggNode)) {
            this.aggNodes.remove(this.aggNodes.size() - 1);
        }
        this.aggNodes.add(aggNode);
    }

    @Override
    public void addAggNodes(List<AggNode> aggNodes) {
        int i = 0;
        while (aggNodes.size() > i
                && this.aggNodes.size() > 0
                && this.aggNodes.get(this.aggNodes.size() - 1).equals(
                        aggNodes.get(i))) {
            this.aggNodes.remove(this.aggNodes.size() - 1);
            i++;
        }
        this.aggNodes.addAll(aggNodes);
    }

    @Override
    public List<GPSPoint> getGpsPoints() {
        return gpsPoints;
    }

    @Override
    public void addGPSPoints(List<GPSPoint> gpsPoints) {
        int i = 0;
        while (gpsPoints.size() > i
                && this.gpsPoints.size() > 0
                && this.gpsPoints.get(this.gpsPoints.size() - 1).equals(
                        gpsPoints.get(i))) {
            this.gpsPoints.remove(this.gpsPoints.size() - 1);
            i++;
        }
        this.gpsPoints.addAll(gpsPoints);
    }

    @Override
    public void addGPSPoint(GPSPoint gpsPoint) {
        if (this.gpsPoints.size() > 0
                && this.gpsPoints.get(this.gpsPoints.size() - 1).equals(
                        gpsPoint)) {
            return;
        }
        this.gpsPoints.add(gpsPoint);
    }

    @Override
    public void processSubmatch() {
        newNodesPerConn = new HashMap<AggConnection, List<PointGhostPointPair>>();
        pointGhostPointPairs = new ArrayList<PointGhostPointPair>();
        // projections of the trace to the aggregation
        int start = 0;
        GPSPoint lastPoint = null, point = null;
        for (int pointIndex = 0; pointIndex < getGpsPoints().size(); pointIndex++) {
            lastPoint = point;
            point = getGpsPoints().get(pointIndex);
            logger.log(Level.FINER, "point {0}", point);
            // loop over all possible opposing lines
            List<AggNode> internalAggNodes = getAggNodes();
            boolean afterHit = false;
            int iMax;
            for (int i = start; i < Math.min(start + maxLookahead,
                    internalAggNodes.size() - 1); i++) {
                iMax = Math.min(start + maxLookahead,
                        internalAggNodes.size() - 1);
                AggNode startNode = internalAggNodes.get(i);
                AggNode endNode = internalAggNodes.get(i + 1);
                ILocation newPoint = GPSCalc.getProjectionPoint(point,
                        startNode, endNode);
                newPoint = testLength(point, newPoint);
                AggNode newNode;
                if (newPoint == null) {
                    if (afterHit || pointIndex == 0) {
                        break;
                    }
                    continue;
                } else {
                    newNode = new AggNode(newPoint, aggContainer);
                    if (!afterHit) {
                        start = Math.max(0, i - 1);
                    }
                }
                newNode.setID(String.format("%s+", point.getID()));
                logger.log(Level.FINER, String.format(
                        "made ghost point %s at %s%s", newNode, startNode,
                        endNode));

                AggConnection conn = startNode.getConnectionTo(endNode);
                PointGhostPointPair pair = PointGhostPointPair
                        .createTraceToAgg(point, newNode, conn, afterHit);
                pointGhostPointPairs.add(pair);

                if (!newNodesPerConn.containsKey(conn)) {
                    newNodesPerConn.put(conn,
                            new ArrayList<PointGhostPointPair>());
                }
                // remove (now) invalid ghost points of earlier trace points
                PointGhostPointPair loopPair = null;
                List<PointGhostPointPair> nodesOnThisConn = newNodesPerConn
                        .get(conn);
                for (int m = nodesOnThisConn.size() - 1; m >= 0; m--) {
                    loopPair = nodesOnThisConn.get(m);
                    // is it okay?
                    if (!loopPair.getPoint().equals(lastPoint)
                            && !loopPair.getPoint().equals(point)) {
                        nodesOnThisConn.remove(m);
                        pointGhostPointPairs.remove(loopPair);
                    }
                }
                // add the new pair (only the node will later be accessed)
                nodesOnThisConn.add(pair);

                afterHit = true;

                // trace to agg
                if (i < iMax - 1) {
                    AggNode nextNode = internalAggNodes.get(i + 2);
                    double nextAngle = GPSCalc.getAngleBetweenEdges(startNode,
                            endNode, endNode, nextNode);
                    if (CartesianCalc.isAngleMax(nextAngle,
                            minContinuationAngle)) {
                        break;
                    }
                }
            }

            /*
             * Compute inNode and outNode such that it is the AggNode closest to
             * the first/last GPSPoint in the Match.
             */
            inNode = aggNodes.get(0);
            double distReal = GPSCalc.getDistance(inNode, gpsPoints.get(0));
            if (pointGhostPointPairs.size() > 0) {
                AggNode ghostAggPoint = pointGhostPointPairs.get(0)
                        .getAggNode();
                double distGhost = GPSCalc.getDistance(ghostAggPoint,
                        gpsPoints.get(0));
                if (distGhost < distReal) {
                    inNode = ghostAggPoint;
                }
            }

            outNode = aggNodes.get(aggNodes.size() - 1);
            distReal = GPSCalc.getDistance(inNode,
                    gpsPoints.get(gpsPoints.size() - 1));
            if (pointGhostPointPairs.size() > 0) {
                AggNode ghostAggPoint = pointGhostPointPairs.get(
                        pointGhostPointPairs.size() - 1).getAggNode();
                double distGhost = GPSCalc.getDistance(ghostAggPoint,
                        gpsPoints.get(gpsPoints.size() - 1));
                if (distGhost < distReal) {
                    outNode = ghostAggPoint;
                }
            }
        }

        // projections of the aggregation to the trace
        start = 0;
        for (int pointIndex = 0; pointIndex < getAggNodes().size(); pointIndex++) {
            AggNode node = getAggNodes().get(pointIndex);
            logger.log(Level.FINER, "agg node {0}", node);
            // loop over all possible opposing lines
            List<GPSPoint> internalGpsPoints = getGpsPoints();
            boolean afterHit = false;
            int iMax;
            for (int i = start; i < Math.min(start + maxLookahead,
                    internalGpsPoints.size() - 1); i++) {
                iMax = Math.min(start + maxLookahead,
                        internalGpsPoints.size() - 1);
                GPSPoint startNode = internalGpsPoints.get(i);
                GPSPoint endNode = internalGpsPoints.get(i + 1);
                ILocation newPoint = GPSCalc.getProjectionPoint(node,
                        startNode, endNode);
                newPoint = testLength(point, newPoint);
                if (newPoint == null) {
                    if (afterHit || pointIndex == 0) {
                        break;
                    }
                    continue;
                } else {
                    if (!afterHit) {
                        start = Math.max(0, i - 1);
                    }
                }
                GPSPoint newNode = new GPSPoint(newPoint);
                newNode.setID(String.format("%s+", node.getID()));
                logger.log(Level.FINER, String.format(
                        "made ghost point %s at %s%s", newNode, startNode,
                        endNode));
                AggNode addNode = node;
                if (afterHit) {
                    addNode = new AggNode(node);
                    addNode.setID(MessageFormat.format("dup-{0}", node.getID()));
                }
                PointGhostPointPair pair = PointGhostPointPair
                        .createAggToTrace(addNode, newNode, pointIndex,
                                afterHit);
                pointGhostPointPairs.add(pair);
                if (afterHit && pointIndex < getAggNodes().size() - 1) {
                    AggConnection conn = getAggNodes().get(pointIndex)
                            .getConnectionTo(getAggNodes().get(pointIndex + 1));
                    if (!newNodesPerConn.containsKey(conn)) {
                        newNodesPerConn.put(conn,
                                new ArrayList<PointGhostPointPair>());
                    }
                    // remove (now) invalid ghost points of earlier trace points
                    PointGhostPointPair loopPair = null;
                    List<PointGhostPointPair> nodesOnThisConn = newNodesPerConn
                            .get(conn);
                    for (int m = nodesOnThisConn.size() - 1; m >= 0; m--) {
                        loopPair = nodesOnThisConn.get(m);
                        // is it okay?
                        if (!loopPair.getPoint().equals(lastPoint)
                                && !loopPair.getPoint().equals(point)) {
                            nodesOnThisConn.remove(m);
                            pointGhostPointPairs.remove(loopPair);
                        }
                    }
                    newNodesPerConn.get(conn).add(pair);
                }
                afterHit = true;

                // agg to trace
                if (i < iMax - 1) {
                    GPSPoint nextNode = internalGpsPoints.get(i + 2);
                    double nextAngle = GPSCalc.getAngleBetweenEdges(startNode,
                            endNode, endNode, nextNode);
                    if (CartesianCalc.isAngleMax(nextAngle,
                            minContinuationAngle)) {
                        break;
                    }
                }
            }
        }
    }

    private ILocation testLength(GPSPoint point, ILocation newPoint) {
        if (newPoint != null) {
            if (GPSCalc.getDistance(point, newPoint) > maxPointGhostDist) {
                return null;
            }
        }
        return newPoint;
    }

//    private void showDebugInfo() {
//        // debug
//        TestUI ui = (TestUI) Globals.get("ui");
//        if (ui == null) {
//            return;
//        }
//        Layer matchingLayer = ui.getLayerManager().getLayer("matching");
//        Layer mergingLayer = ui.getLayerManager().getLayer("merging");
//        // clone the lists
//        List<ILocation> aggNodesClone = new ArrayList<ILocation>(
//                aggNodes.size());
//        for (ILocation loc : aggNodes) {
//            aggNodesClone.add(new GPSPoint(loc));
//        }
//        matchingLayer.addObject(aggNodesClone);
//        matchingLayer.addObject(gpsPoints); // , roMatchGPS);
//
//        // for debugging highlight trace to agg with an arrow
//        for (PointGhostPointPair pgpp : pointGhostPointPairs) {
//            List<ILocation> line = new ArrayList<ILocation>(2);
//            line.add(new GPSPoint(pgpp.point));
//            line.add(new GPSPoint(pgpp.ghostPoint));
//            mergingLayer.addObject(line);
//        }
//    }
    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(double bestDifference) {
        this.distance = bestDifference;
    }

    @Override
    public void mergePoints() {
//        showDebugInfo();

        List<AggConnection> changedAggConnections = new ArrayList<AggConnection>(100);
        List<AggConnection> newAggConnections;
        // add nodes
        AggNode lastNode = null;
        AggConnection conn = null;
        for (AggNode node : getAggNodes()) {
            if (lastNode != null && (conn = lastNode.getConnectionTo(node)) != null) {
                conn.tryToFill();
                List<AggNode> aggNodeList = new ArrayList<AggNode>(100);
                if (newNodesPerConn.get(conn) != null) {
                    for (PointGhostPointPair pair : newNodesPerConn.get(conn)) {
                        aggNodeList.add(pair.getAggNode());
                    }
                    newAggConnections = aggContainer.insertNodesOrdered(conn.getFrom(), conn.getTo(), aggNodeList);
                    changedAggConnections.addAll(newAggConnections);
                } else {
                    // edge without
                    changedAggConnections.add(conn);
                }
            }
            lastNode = node;
        }

        // merge points
        for (PointGhostPointPair pgpp : pointGhostPointPairs) {
            mergePointPair(pgpp.getAggNode(), pgpp.getGPSPoint());
        }

        List<AggNode> changedAggPoints = AggConnection.listToPoints(changedAggConnections);
        // update distance and weights
        for (AggConnection loopConn : changedAggConnections) {
            if (loopConn != null) {

                float oldWeight = loopConn.getWeight();
                double oldAvgDist = loopConn.getAvgDist();
                loopConn.setAvgDist(((oldWeight - 1) * oldAvgDist + distance) / oldWeight);
                loopConn.setWeight(oldWeight + 1);
            }
        }
        for (AggNode node : changedAggPoints) {
            node.refreshWeight();
        }

        // add turns
        List<AggNode> turnNodes = new ArrayList<AggNode>();
        turnNodes.addAll(changedAggPoints);
        if (turnNodes.size() > 1) {
            turnNodes.remove(0);
            turnNodes.remove(turnNodes.size() - 1);
        }
        turnNodes.add(0, inNode);
        turnNodes.add(0, beforeNode);
        turnNodes.add(outNode);
        // AggNode node;
        for (int i = 2; i < changedAggPoints.size(); i++) {
            changedAggPoints.get(i - 1).addTurn(changedAggPoints.get(i - 2),
                    changedAggPoints.get(i - 0));
        }

        // clean like in the GPSCleaner
        cleaner.clean(changedAggPoints);
        // simplify
        rdpf.simplifyAgg(changedAggPoints, aggContainer);
    }

    private void mergePointPair(AggNode aggNode, GPSPoint gpsPoint) {
        double factor = gpsPoint.getWeight()
                / (aggNode.getWeight() + gpsPoint.getWeight()); // -1
        // because
        // it is
        // already
        // connected?
        // factor = 0.5;
        // // DEBUGGING
        Float64Vector newPos = GPSCalc.getVector(aggNode).plus(
                GPSCalc.getVector(aggNode, gpsPoint).times(factor));
        logger.log(Level.FINER, "moving {0} {1}, {2} and {3}, {4} to {5}, {6}",
                new Object[]{
                    aggNode,
                    aggNode.getLat(),
                    aggNode.getLon(),
                    gpsPoint.getLat(),
                    gpsPoint.getLon(),
                    newPos.getValue(0),
                    newPos.getValue(1)});
        ILocation newPosCopy = new GPSPoint(newPos.getValue(0),
                newPos.getValue(1));
        aggContainer.moveNodeTo(aggNode, newPosCopy);
    }

    @Override
    public AggNode getInNode() {
        return inNode;
    }

    @Override
    public AggNode getOutNode() {
        return outNode;
    }

    @Override
    public String toString() {
        StringBuilder gps = new StringBuilder();
        for (GPSPoint point : gpsPoints) {
            gps.append(point).append(", ");
        }
        StringBuilder agg = new StringBuilder();
        for (AggNode node : aggNodes) {
            agg.append(node).append(", ");
        }
        return String.format("MergeHandler:\n\tGPS: %s\n\tAgg: %s", gps, agg);
    }

    @Override
    public boolean isEmpty() {
        return gpsPoints.isEmpty() && gpsPoints.isEmpty();
    }

    @Override
    public void setBeforeNode(AggNode lastNode) {
        this.beforeNode = lastNode;
    }

    @Override
    public void addAggNodes(AggConnection bestConn) {
        this.addAggNode(bestConn.getFrom());
        this.addAggNode(bestConn.getTo());
    }

    @Override
    public void addGPSPoints(GPSEdge edge) {
        this.addGPSPoint(edge.getFrom());
        this.addGPSPoint(edge.getTo());
    }

    @Override
    public IMergeHandler getCopy() {
        DefaultMergeHandler object = new DefaultMergeHandler();
        object.aggContainer = this.aggContainer;
        object.maxLookahead = this.maxLookahead;
        object.minContinuationAngle = this.minContinuationAngle;
        object.maxPointGhostDist = this.maxPointGhostDist;
        return object;
    }

    @Override
    public List<ClassObjectEditor> getSettings() {
        List<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>();
        result.add(new ClassObjectEditor(this, Arrays.asList(new String[]{
            "aggContainer", "distance", "rdpf"})));
        result.add(new ClassObjectEditor(this.rdpf));
        return result;
    }
}
