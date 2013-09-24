package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.PointGhostPointPair;
import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.Globals;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graph.ui.gui.jmv.Layer;
import de.fub.agg2graph.ui.gui.jmv.TestUI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IterativeClosestPointsMerge implements IMergeHandler {

    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.default.merge");

    // contains only matched points/nodes
    private List<AggNode> aggNodes = null;
    private List<GPSPoint> gpsPoints = null;
    private int max = 0;
    private int maxLookahead = 4;
    private double minContinuationAngle = 45;
    // helper stuff
    // private Map<AggNode, List<GPSPoint>> kNeighbours = new HashMap<AggNode,
    // List<GPSPoint>>();
    private Map<AggConnection, List<PointGhostPointPair>> newNodesPerConn;
    private List<PointGhostPointPair> pointGhostPointPairs = new ArrayList<PointGhostPointPair>();

    private AggNode inNode;
    private AggNode outNode;

    private AggContainer aggContainer;
    private RenderingOptions roMatchGPS;
    // cleaning stuff
    private final RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(0, 125);
    // private static AggCleaner cleaner = new AggCleaner().enableDefault();
    private double maxPointGhostDist = 40; // meters

    private double distance = 10;
    @SuppressWarnings("unused")
    private AggNode beforeNode;

    private double delta = 0.003;
    private final int k = 3;

    public IterativeClosestPointsMerge() {
        // debugging
        logger.setLevel(Level.ALL);
        roMatchGPS = new RenderingOptions();
        roMatchGPS.setColor(Color.PINK);
        logger.setLevel(Level.OFF);

        aggNodes = new ArrayList<AggNode>();
        gpsPoints = new ArrayList<GPSPoint>();
    }

    public IterativeClosestPointsMerge(AggContainer aggContainer) {
        this();

        this.aggContainer = aggContainer;
    }

    public IterativeClosestPointsMerge(AggContainer aggContainer,
            List<AggNode> aggNodes, List<GPSPoint> gpsPoints) {
        this.aggNodes = aggNodes;
        this.gpsPoints = gpsPoints;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
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
     * @return the minContinuationAngle
     */
    public double getMinContinuationAngle() {
        return minContinuationAngle;
    }

    /**
     * @param minContinuationAngle the minContinuationAngle to set
     */
    public void setMinContinuationAngle(double minContinuationAngle) {
        this.minContinuationAngle = minContinuationAngle;
    }

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getMaxPointGhostDist() {
        return maxPointGhostDist;
    }

    public void setMaxPointGhostDist(double maxPointGhostDist) {
        this.maxPointGhostDist = maxPointGhostDist;
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
        return this.aggNodes;
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

        // Not interested with too few points
        if (getAggNodes().size() < 3 || getGpsPoints().size() < 2) {
            return;
        }

        inNode = aggNodes.get(0);
        outNode = aggNodes.get(aggNodes.size() - 1);

        // projections of the aggregation to the trace
        for (int pointIndex = 0; pointIndex < getAggNodes().size(); pointIndex++) {

            AggNode node = getAggNodes().get(pointIndex);
            logger.log(Level.FINER, "agg node {0}", node);
            // loop over all possible opposing lines
            PointGhostPointPair pair = null;
            // START
            List<GPSPoint> neighbour;
            if (pointIndex == 0 || pointIndex == getAggNodes().size() - 1) {
                neighbour = getKSmallest(gpsPoints, node, 1);
            } else {
                neighbour = getKSmallest(gpsPoints, node, k);
            }
            if (neighbour.size() > 0) {
                pair = PointGhostPointPair.createIterative(
                        node, neighbour, 0);
                pointGhostPointPairs.add(pair);
            }

            if (pair != null && pointIndex < getAggNodes().size() - 1) {
                AggConnection conn = getAggNodes().get(pointIndex)
                        .getConnectionTo(getAggNodes().get(pointIndex + 1));
                if (!newNodesPerConn.containsKey(conn)) {
                    newNodesPerConn.put(conn,
                            new ArrayList<PointGhostPointPair>());
                }
                newNodesPerConn.get(conn).add(pair);
            }
            // END
        }
    }

    private static List<GPSPoint> getKSmallest(List<GPSPoint> trace,
            AggNode from, int k) {
        double currentMaxDistance = 0;
        GPSPoint currentMax = null;
        if (trace.isEmpty()) {
            return null;
        } else if (trace.size() <= k) {
            return trace;
        } else {
            List<GPSPoint> dist = new ArrayList<GPSPoint>(k);
            for (GPSPoint p : trace) {
                if (dist.size() < k) {
                    dist.add(p);
                } else {
                    currentMaxDistance = GPSCalc.getDistanceTwoPointsMeter(
                            from, p);
                    currentMax = p;
                    for (GPSPoint d : dist) {
                        if (currentMaxDistance < GPSCalc
                                .getDistanceTwoPointsMeter(from, d)) {
                            currentMaxDistance = GPSCalc
                                    .getDistanceTwoPointsMeter(from, d);
                            currentMax = d;
                        }
                    }
                    if (dist.contains(currentMax)) {
                        dist.remove(currentMax);
                        dist.add(p);
                    }
                }
            }
            return dist;
        }
    }

    @SuppressWarnings("unused")
    private ILocation testLength(GPSPoint point, ILocation newPoint) {
        if (newPoint != null) {
            if (GPSCalc.getDistanceTwoPointsMeter(point, newPoint) > maxPointGhostDist) {
                return null;
            }
        }
        return newPoint;
    }

    private void showDebugInfo() {
        TestUI ui = (TestUI) Globals.get("ui");
        if (ui == null) {
            return;
        }
        Layer matchingLayer = ui.getLayerManager().getLayer("matching");
        Layer mergingLayer = ui.getLayerManager().getLayer("merging");
        // clone the lists
        List<ILocation> aggNodesClone = new ArrayList<ILocation>(
                aggNodes.size());
        for (ILocation loc : aggNodes) {
            aggNodesClone.add(new GPSPoint(loc));
        }
        matchingLayer.addObject(aggNodesClone);
        matchingLayer.addObject(gpsPoints); // , roMatchGPS);

        for (PointGhostPointPair pgpp : pointGhostPointPairs) {
            List<ILocation> line = new ArrayList<ILocation>(2);
            for (int j = 0; j < pgpp.ghostPoints.size(); j++) {
                line.add(new GPSPoint(pgpp.source));
                line.add(new GPSPoint(pgpp.ghostPoints.get(j)));
                mergingLayer.addObject(line);
                line = new ArrayList<ILocation>(2);
            }
        }
    }

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
        this.setMax(aggNodes.size());
        showDebugInfo();

        // add nodes
        AggNode lastNode = null;
        AggConnection conn = null;
        for (AggNode node : getAggNodes()) {
            if (lastNode == null) {
                lastNode = node;
                continue;
            }

            /* Make sure that they are connected */
            conn = lastNode.getConnectionTo(node);
            if (conn == null) {
                continue;
            }
            conn.tryToFill();

            lastNode = node;
        }

        for (PointGhostPointPair pgpp : pointGhostPointPairs) {
            closestPointsMerge(pgpp.source, pgpp.ghostPoints);
        }

    }

    public void closestPointsMerge(AggNode a, List<GPSPoint> ts) {

        AggNode toMean = GPSCalc.calculateMean(a, ts, getDelta(), aggContainer, true);
        AggNode to = GPSCalc.moveLocation(a, toMean, aggContainer);
        // GPSCalc.moveLocation(map, a, toCopy, aggContainer);
        a.setK(a.getK() + 1);
        aggContainer.moveNodeTo(a, to);
        // System.out.println("t         = " + to.getLat() + " <> " +
        // to.getLon());
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
        List<AggNode> agg = new ArrayList<AggNode>();
        agg.add(bestConn.getFrom());
        agg.add(bestConn.getTo());
        addAggNodes(agg);
    }

    @Override
    public void addGPSPoints(GPSEdge edge) {
        List<GPSPoint> tra = new ArrayList<GPSPoint>();
        tra.add(edge.getFrom());
        tra.add(edge.getTo());
        addGPSPoints(tra);
    }

    @Override
    public IMergeHandler getCopy() {
        IterativeClosestPointsMerge object = new IterativeClosestPointsMerge();
        object.aggContainer = this.aggContainer;
        object.setMaxLookahead(this.getMaxLookahead());
        object.setMinContinuationAngle(this.getMinContinuationAngle());
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

    @Override
    public List<PointGhostPointPair> getPointGhostPointPairs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
