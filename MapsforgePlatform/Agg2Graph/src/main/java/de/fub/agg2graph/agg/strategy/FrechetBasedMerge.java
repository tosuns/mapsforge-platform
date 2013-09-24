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
import de.fub.agg2graph.structs.frechet.FrechetDistance;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graph.ui.gui.jmv.Layer;
import de.fub.agg2graph.ui.gui.jmv.TestUI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrechetBasedMerge implements IMergeHandler {

    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.default.merge");

    // contains only matched points/nodes
    private List<AggNode> aggNodes = null;
    private List<GPSPoint> gpsPoints = null;

    private int maxLookahead = 10;
    private double minContinuationAngle = 45;
    // helper stuff

    private AggNode inNode;
    private AggNode outNode;

    private AggContainer aggContainer;
    private RenderingOptions roMatchGPS;
    // cleaning stuff
    private final RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(0, 125);
    // private static AggCleaner cleaner = new AggCleaner().enableDefault();
    private double maxPointGhostDist = 12.5; // meters

    private double distance = 12.5;
    @SuppressWarnings("unused")
    private AggNode beforeNode;

    public HashSet<GPSEdge> criticalEdges = new HashSet<GPSEdge>();
    HashMap<AggNode, TreeSet<AggNode>> AtoT = new HashMap<AggNode, TreeSet<AggNode>>();

    public FrechetBasedMerge() {
        // debugging
        logger.setLevel(Level.ALL);
        roMatchGPS = new RenderingOptions();
        roMatchGPS.setColor(Color.PINK);
        logger.setLevel(Level.OFF);

        aggNodes = new ArrayList<AggNode>();
        gpsPoints = new ArrayList<GPSPoint>();
    }

    public FrechetBasedMerge(AggContainer aggContainer) {
        this();

        this.aggContainer = aggContainer;
    }

    public FrechetBasedMerge(AggContainer aggContainer, List<AggNode> aggNodes,
            List<GPSPoint> gpsPoints) {
        this.aggNodes = aggNodes;
        this.gpsPoints = gpsPoints;
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
    }

    private void showDebugMerge(AggNode locationToMove,
            TreeSet<GPSPoint> affectedTrace) {
        TestUI ui = (TestUI) Globals.get("ui");
        Layer mergingLayer = ui.getLayerManager().getLayer("merging");

        List<ILocation> line = new ArrayList<ILocation>(2);
        for (ILocation to : affectedTrace) {
            line.add(new GPSPoint(locationToMove));
            line.add(new GPSPoint(to));
            mergingLayer.addObject(line);
            line = new ArrayList<ILocation>(2);
        }
    }

    private void showDebugInfo() {
        TestUI ui = (TestUI) Globals.get("ui");
        if (ui == null) {
            return;
        }
        Layer matchingLayer = ui.getLayerManager().getLayer("matching");
        // clone the lists
        List<ILocation> aggNodesClone = new ArrayList<ILocation>(
                aggNodes.size());
        for (ILocation loc : aggNodes) {
            aggNodesClone.add(new GPSPoint(loc));
        }
        matchingLayer.addObject(aggNodesClone);
        matchingLayer.addObject(gpsPoints); // , roMatchGPS);
        //
        // Iterator<AggNode> it = AtoT.keySet().iterator();
        // AggNode nextKey;
        // while (it.hasNext()) {
        // nextKey = it.next();
        // if (AtoT.get(nextKey) == null)
        // continue;
        // List<ILocation> line = new ArrayList<ILocation>(2);
        // for (ILocation to : AtoT.get(nextKey)) {
        // line.add(new GPSPoint(nextKey));
        // line.add(new GPSPoint(to));
        // mergingLayer.addObject(line);
        // line = new ArrayList<ILocation>(2);
        // }
        // }
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
            // aConn.add(new AggConnection(lastNode, node, aggContainer));
            conn.tryToFill();

            lastNode = node;
        }
        frechetMerge(aggNodes, gpsPoints);
    }

    public void frechetMerge(List<AggNode> agg, List<GPSPoint> tra) {
        // Convert to vectors for frechet input.
        AggNode lastAgg = null;
        GPSPoint lastTra = null;
        List<GPSEdge> Av = new ArrayList<GPSEdge>();
        List<GPSEdge> Tv = new ArrayList<GPSEdge>();
        AggNode to;

        for (AggNode a : agg) {
            if (lastAgg == null) {
                lastAgg = a;
                continue;
            }
            Av.add(new GPSEdge(lastAgg, a));
            lastAgg = a;
        }

        for (GPSPoint t : tra) {
            if (lastTra == null) {
                lastTra = t;
                continue;
            }
            Tv.add(new GPSEdge(lastTra, t));
            lastTra = t;
        }

        // // Value of epsilon not relevant in our use case.
        FrechetDistance fd = new FrechetDistance(Av, Tv, distance / 92500.0);
        // Compute critical values and the meta information needed by our
        // algorithm.
        double epsilon = fd.computeEpsilon();
        // System.out.println("FrechetBasedMerge: Use epsilon of: " + epsilon
        // + " isOk " + fd.isInDistance());
        fd.computeMetaData();
        // // Map points from A to T
        for (Entry<Integer, TreeSet<AggNode>> entry : fd.fromP.entrySet()) {
            int i = entry.getKey();
            if (i < fd.P.size()) {
                AtoT.put(fd.P.get(i).getFrom(), entry.getValue());
            } else if (i == fd.P.size()) {
                AtoT.put(fd.P.get(i - 1).getTo(), entry.getValue());
            }
        }

        showDebugInfo();

        //
        // Move the aggregate by the mean of the segments introduced by
        // Frechet critical values. See paperwork for clarification.
        List<AggNode> keySet = new ArrayList<AggNode>(AtoT.keySet());
        for (int i = 0; i < keySet.size() - 1; i++) {
            // TODO
            AggNode locationToMove = keySet.get(i);

            if (AtoT.containsKey(locationToMove)) {
                TreeSet<AggNode> affectedTraceLocations = AtoT
                        .get(locationToMove);
                if (affectedTraceLocations != null) {
                    TreeSet<GPSPoint> affectedTrace = new TreeSet<GPSPoint>();
                    // Save meta information for display in the gui.
                    for (AggNode ti : affectedTraceLocations) {
                        double dist = GPSCalc.getDistanceTwoPointsDouble(
                                locationToMove, ti);
                        if (dist <= epsilon) {
                            criticalEdges.add(new GPSEdge(new GPSPoint(
                                    locationToMove), new GPSPoint(ti)));
                            affectedTrace.add(new GPSPoint(ti));
                        }
                    }
                    showDebugMerge(locationToMove, affectedTrace);

                    AggNode weightedpos = GPSCalc.calculateMean(locationToMove,
                            affectedTrace, epsilon, aggContainer, false);
                    AggNode toMean = new AggNode(weightedpos, aggContainer);
                    if (toMean.compareTo(locationToMove) != 0) {
                        to = GPSCalc.moveLocation(locationToMove, toMean,
                                aggContainer);
                        for (AggNode a : aggNodes) {
                            if (locationToMove.getLat() == a.getLat()
                                    && locationToMove.getLon() == a.getLon()) {
                                locationToMove = a;
                            }
                        }
                        aggContainer.moveNodeTo(locationToMove, to);
                    }
                }
            }
        }
        // Also the last point.
        AggNode locationToMove = keySet.get(keySet.size() - 1);
        if (AtoT.containsKey(locationToMove)) {
            TreeSet<AggNode> affectedTraceLocations = AtoT.get(locationToMove);
            if (affectedTraceLocations != null) {
                TreeSet<GPSPoint> affectedTrace = new TreeSet<GPSPoint>();
                // Save meta information for display in the gui.
                for (AggNode ti : affectedTraceLocations) {
                    // double dist = locationToMove.getDistanceTo(ti);
                    double dist = GPSCalc.getDistanceTwoPointsDouble(
                            locationToMove, ti);
                    // TODO epsilon, nicht delta
                    if (dist <= epsilon) {
                        criticalEdges.add(new GPSEdge(new GPSPoint(
                                locationToMove), new GPSPoint(ti))); // System.err.printf("\\draw[dashed,thin] (%.8f, %.8f) to (%.8f, %.8f);\n",
                        // locationToMove.getLongitude(),
                        // locationToMove.getLatitude(),
                        // ti.getLongitude(), ti.getLatitude());
                    }
                }
                AggNode weightedpos = GPSCalc.calculateMean(locationToMove,
                        affectedTrace, epsilon, aggContainer, false);
                AggNode toMean = new AggNode(weightedpos, aggContainer);
                if (toMean.compareTo(locationToMove) != 0) {
                    to = GPSCalc.moveLocation(locationToMove, toMean,
                            aggContainer);
                    for (AggNode a : aggNodes) {
                        if (locationToMove.getLat() == a.getLat()
                                && locationToMove.getLon() == a.getLon()) {
                            locationToMove = a;
                        }
                    }
                    aggContainer.moveNodeTo(locationToMove, to);
                }
            }
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
        FrechetBasedMerge object = new FrechetBasedMerge();
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
}
