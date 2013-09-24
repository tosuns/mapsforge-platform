package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggCleaner;
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
import de.fub.agg2graph.structs.frechet.Pair;
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

public class AttractionForceMerge implements IMergeHandler {

    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.default.merge");

    // contains only matched points/nodes
    private List<AggNode> aggNodes = null;
    private List<GPSPoint> gpsPoints = null;
    private int maxLookahead = 10;
    private double minContinuationAngle = 45;
    // helper stuff
    private Map<AggConnection, List<PointGhostPointPair>> newNodesPerConn;
    private List<PointGhostPointPair> pointGhostPointPairs = new ArrayList<PointGhostPointPair>();

    private AggNode inNode;
    private AggNode outNode;

    private AggContainer aggContainer;
    private RenderingOptions roMatchGPS;
    // cleaning stuff
    private final RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(0, 50);
    private static AggCleaner cleaner = new AggCleaner().enableDefault();
    private double maxPointGhostDist = 10; // meters

    private double distance = 10;
    @SuppressWarnings("unused")
    private AggNode beforeNode;

    private final AttractionValue av = new AttractionValue();

    public AttractionForceMerge() {
        // debugging
        logger.setLevel(Level.ALL);
        roMatchGPS = new RenderingOptions();
        roMatchGPS.setColor(Color.PINK);
        logger.setLevel(Level.OFF);

        aggNodes = new ArrayList<AggNode>();
        gpsPoints = new ArrayList<GPSPoint>();
    }

    public AttractionForceMerge(AggContainer aggContainer) {
        this();

        this.aggContainer = aggContainer;
    }

    public AttractionForceMerge(AggContainer aggContainer,
            List<AggNode> aggNodes, List<GPSPoint> gpsPoints) {
        this.aggNodes = aggNodes;
        this.gpsPoints = gpsPoints;
    }

    /**
     * @return the maxPointGhostDist
     */
    public double getMaxPointGhostDist() {
        return maxPointGhostDist;
    }

    /**
     * @param maxPointGhostDist the maxPointGhostDist to set
     */
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

        Pair<AggNode, AggNode> pairAgg = null;
        Pair<GPSPoint, GPSPoint> pairTraj = null;

        // Not interested with too few points
        if (getAggNodes().size() < 3 || getGpsPoints().size() < 2) {
            return;
        }

        inNode = aggNodes.get(0);
        outNode = aggNodes.get(aggNodes.size() - 1);

        // projections of the aggregation to the trace
        for (int pointIndex = 0; pointIndex < getAggNodes().size(); pointIndex++) {
            double current, best = Double.MAX_VALUE;
            int bestI = 0;

            AggNode node = getAggNodes().get(pointIndex);
            logger.log(Level.FINER, "agg node {0}", node);
            // loop over all possible opposing lines
            List<GPSPoint> internalGpsPoints = getGpsPoints();
            PointGhostPointPair pair = null;
            // START
            // For all point with exception start and end point
            if (!(pointIndex == 0 || pointIndex == getAggNodes().size() - 1)) {
                for (int i = 0; i < internalGpsPoints.size() - 1; i++) {
                    current = GPSCalc.getDistancePointToEdgeMeter(getAggNodes()
                            .get(pointIndex), internalGpsPoints.get(i),
                            internalGpsPoints.get(i + 1));
                    if (current < best
                            && GPSCalc.getProjectionPoint(
                                    getAggNodes().get(pointIndex),
                                    internalGpsPoints.get(i),
                                    internalGpsPoints.get(i + 1)) != null) {
                        best = current;
                        bestI = i;
                    }
                }
                if (best < Double.MAX_VALUE) {
                    pairAgg = new Pair<AggNode, AggNode>(getAggNodes().get(
                            pointIndex - 1), getAggNodes().get(pointIndex + 1));
                    pairTraj = new Pair<GPSPoint, GPSPoint>(
                            internalGpsPoints.get(bestI),
                            internalGpsPoints.get(bestI + 1));
                }
                if (pairAgg != null && pairTraj != null) {
                    pair = PointGhostPointPair.createAttraction(getAggNodes()
                            .get(pointIndex), pairAgg, pairTraj, 0);
                    pointGhostPointPairs.add(pair);
                }
            } else {
                int temp = pointIndex == 0 ? 0 : getAggNodes().size() - 1;

                for (int i = 0; i < internalGpsPoints.size(); i++) {
                    current = GPSCalc.getDistanceTwoPointsMeter(getAggNodes()
                            .get(temp), internalGpsPoints.get(i));
                    if (current < best) {
                        best = current;
                        bestI = i;
                    }
                }
                pair = PointGhostPointPair.createAttraction(
                        getAggNodes().get(temp), internalGpsPoints.get(bestI));
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

    public Pair<GPSPoint, GPSPoint> getBestEdge(AggNode node, GPSPoint start,
            GPSPoint end) {
        return null;
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
            if (!pgpp.isEnd) {
                List<ILocation> line = new ArrayList<ILocation>(2);
                List<ILocation> line2 = new ArrayList<ILocation>(2);
                line.add(new GPSPoint(pgpp.source));
                line.add(new GPSPoint(pgpp.pairTraj.a));
                line2.add(new GPSPoint(pgpp.source));
                line2.add(new GPSPoint(pgpp.pairTraj.b));
                mergingLayer.addObject(line);
                mergingLayer.addObject(line2);
            } else {
                List<ILocation> line = new ArrayList<ILocation>(2);
                line.add(new GPSPoint(pgpp.source));
                line.add(new GPSPoint(pgpp.proj));
                mergingLayer.addObject(line);

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
        if (getAggNodes().size() < 3 || getGpsPoints().size() < 2) {
            return;
        }
        showDebugInfo();

        // add nodes
//		List<AggConnection> changedAggConnections = new ArrayList<AggConnection>(
//				10);
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
            if (!pgpp.isEnd) {
                attractionForce(pgpp.source, pgpp.pairAgg.a, pgpp.pairAgg.b,
                        pgpp.pairTraj.a, pgpp.pairTraj.b);
            } else {
                attractionForce(pgpp.source, pgpp.proj);
            }
        }

    }

    // AttractionForce Parameter
    // double M = 1;
    // double N = 20;
    // double x = 0;
    // double y = 0;
    // double s1 = 5;
    // double s2 = 5;
    //
    public void attractionForce(AggNode currentNode, GPSPoint projection) {
        double distance = GPSCalc.getDistanceTwoPointsMeter(currentNode,
                projection);
        Double aValue = av.getValue(distance);
        currentNode.setK(currentNode.getK() + 1);
        if (aValue != null) {
            //To damp the movement due to k, log2 is used
            double dampingFactor = Math.log10(currentNode.getK()) / Math.log10(2);
            ILocation newPos = GPSCalc.getPointAt(
                    (av.getKey(distance) - aValue) / (av.getKey(distance) * dampingFactor),
                    currentNode, projection);
            aggContainer.moveNodeTo(currentNode, newPos);
        }
    }

    public void attractionForce(AggNode currentNode, AggNode before,
            AggNode after, GPSPoint trajStart, GPSPoint trajEnd) {
        // TODO Null gefahr
        double angle = GPSCalc.getAngleBetweenEdges(before, after, trajStart,
                trajEnd);
        ILocation aggProj = GPSCalc.getProjectionPoint(currentNode, before,
                after);
        if (aggProj == null) {
            return;
        }
        ILocation trajProj = GPSCalc.intersection(currentNode, aggProj,
                trajStart, trajEnd);
        if (trajProj == null) {
            return;
        }
        double distance = GPSCalc.getDistanceTwoPointsMeter(currentNode,
                trajProj);
        Double aValue = av.getValue(distance);
        currentNode.setK(currentNode.getK() + 1);
        if (aValue != null) {
            //To damp the movement due to k, log2 is used
            double dampingFactor = Math.log10(currentNode.getK()) / Math.log10(2);
            ILocation newPos = GPSCalc.getPointAt(
                    (av.getKey(distance) - aValue) / (av.getKey(distance) * dampingFactor),
                    currentNode, trajProj);

            aggContainer.moveNodeTo(currentNode, newPos);
        }
    }

    @Override
    public AggNode getInNode() {
        return inNode;
    }

    @Override
    public AggNode getOutNode() {
        return outNode;
    }

    // TODO FAUL
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
        AttractionForceMerge object = new AttractionForceMerge();
        object.aggContainer = this.aggContainer;
        object.setMaxLookahead(this.getMaxLookahead());
        object.setMinContinuationAngle(this.getMinContinuationAngle());
        object.setMaxPointGhostDist(this.getMaxPointGhostDist());
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
}
