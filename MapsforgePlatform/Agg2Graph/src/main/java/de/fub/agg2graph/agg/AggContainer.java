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
package de.fub.agg2graph.agg;

import de.fub.agg2graph.agg.tiling.CachingStrategyFactory;
import de.fub.agg2graph.agg.tiling.DefaultCachingStrategy;
import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.agg2graph.agg.tiling.TileManager;
import de.fub.agg2graph.input.GPXReader;
import de.fub.agg2graph.structs.CartesianCalc;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Container class for an aggregation graph. Contains a number of
 * {@link AggNode}s and {@link AggConnection}s. Can be serialized to disk with
 * an {@link ICachingStrategy}. Merging is done using an
 * {@link IAggregationStrategy}.
 *
 * @author Johannes Mitlmeier
 */
public class AggContainer {

    private static final Logger LOG = Logger.getLogger(AggContainer.class.getName());
    private File sourceFolder = null;
    private IAggregationStrategy aggregationStrategy;
    private ICachingStrategy cachingStrategy;

    private AggContainer() {
    }

    /**
     * Preferred way to construct an AggContainer specifying all its important
     * features.
     *
     * @param sourceFolder
     * @param aggStrategy
     * @param cachingStrategy
     * @return
     */
    public static AggContainer createContainer(File sourceFolder,
            IAggregationStrategy aggStrategy, ICachingStrategy cachingStrategy) {
        AggContainer agg = new AggContainer();
        if (aggStrategy != null) {
            agg.setAggregationStrategy(aggStrategy);
        }
        if (cachingStrategy != null) {
            agg.setCachingStrategy(cachingStrategy);
        }
        agg.setDataSource(sourceFolder);
        return agg;
    }

    public static AggContainer createContainer(File sourceFolder) {
        return createContainer(sourceFolder,
                AggregationStrategyFactory.getObject(),
                CachingStrategyFactory.getObject());
    }

    public void setDataSource(File sourceFolder) {
        if (sourceFolder == null) {
            return;
        }
        this.sourceFolder = sourceFolder;
    }

    public File getDataSource() {
        return sourceFolder;
    }

    /**
     * Call this method to merge a .gpx file to the AggContainer using the
     * current {@link IAggregationStrategy}.
     *
     * @param gpxFile
     */
    public void addFile(File gpxFile) {
        List<GPSSegment> segments = GPXReader.getSegments(gpxFile);
        if (segments != null && !segments.isEmpty()) {
            for (int index = 0; index < segments.size(); index++) {
                if (index < segments.size() - 1) {
                    addSegment(segments.get(index), true);
                } else {
                    addSegment(segments.get(index), false);
                }
            }
        }
    }

    /**
     * This method merges a {@link GPSSegment} into the container.
     *
     * @param segment
     * @param isAgg
     */
    public void addSegment(GPSSegment segment, boolean isAgg) {
        getAggregationStrategy().aggregate(segment, isAgg);
    }

    public void addNode(AggNode node) {
        node.setAggContainer(this);
        if (getCachingStrategy() != null) {
            getCachingStrategy().addNode(node);
        }
    }

    public void addConnection(AggConnection conn) {
        if (conn == null
                || conn.getFrom() == null
                || (conn.getFrom().getOut() != null && conn.getFrom().getOut()
                .contains(conn))) {
            return;
        }

        conn.setAggContainer(this);
        getCachingStrategy().addConnection(conn);
    }

    public void save() {
        getCachingStrategy().save();
    }

    public void removeConnection(AggConnection conn) {
        getCachingStrategy().removeConnection(conn);
    }

    /**
     * Removes all incoming and outgoing {@link AggConnection}s before
     * ultimately removing the specified node itself.
     *
     * @param node
     */
    public void deleteNode(AggNode node) {
        // get all connections and remove them first
        ArrayList<AggConnection> connections = new ArrayList<AggConnection>();
        connections.addAll(node.getOut());
        for (AggConnection conn : connections) {
            getCachingStrategy().removeConnection(conn);
        }
        connections.clear();
        connections.addAll(node.getIn());
        for (AggConnection conn : connections) {
            getCachingStrategy().removeConnection(conn);
        }
        getCachingStrategy().removeNode(node);
    }

    public void moveNodeTo(AggNode target, ILocation newPos) {
        target.setLatLon(newPos.getLatLon());
    }

    /**
     * Removes the onde only without touching its {@link AggConnection}s.
     *
     * @param node
     */
    public void removeNodeSilently(AggNode node) {
        getCachingStrategy().removeNode(node);
    }

    /**
     * Removes an {@link AggNode} while preserving and reconnecting the
     * {@link AggConnection}s it was part of. Every ingoing connection is merged
     * with every outgoing one using
     * {@link IAggregationStrategy#combineConnections(AggConnection, AggConnection)}
     *
     * @param node
     */
    public void extractNode(AggNode node) {
        for (AggConnection inConn : new ArrayList<AggConnection>(node.getIn())) {
            for (AggConnection outConn : new ArrayList<AggConnection>(
                    node.getOut())) {
                if (!inConn.getFrom().equals(outConn.getTo())) {
                    if (inConn.getFrom().getConnectionTo(outConn.getTo()) == null) {
                        getCachingStrategy().addConnection(getAggregationStrategy()
                                .combineConnections(inConn, outConn));
                    }
                }
            }
        }
        // remove old things
        for (AggConnection inConn : new ArrayList<AggConnection>(node.getIn())) {
            getCachingStrategy().removeConnection(inConn);
        }
        for (AggConnection outConn : new ArrayList<AggConnection>(node.getOut())) {
            getCachingStrategy().removeConnection(outConn);
        }
        getCachingStrategy().removeNode(node);
    }

    /**
     * Split an {@link AggConnection} into a given number of sub-connections of
     * equal length.
     *
     * @param conn
     * @param numParts
     * @return
     */
    public List<AggConnection> splitConnection(AggConnection conn, int numParts) {
        // System.out.println("called");
        List<AggNode> nodes = new ArrayList<AggNode>(5);
        List<AggConnection> result = new ArrayList<AggConnection>(Math.max(1,
                numParts));
        if (conn == null) {
            return null;
        }
        AggNode from = conn.getFrom();
        AggNode to = conn.getTo();
        nodes.add(from);
        if (numParts > 1) {
            double latDiff = (to.getLat() - from.getLat()) / numParts;
            double lonDiff = (to.getLon() - from.getLon()) / numParts;
            AggNode lastNode = from, newNode;
            for (int i = 1; i < numParts; i++) {
                newNode = new AggNode(
                        from.getID() + "-" + i + "-" + to.getID(),
                        from.getLat() + i * latDiff, from.getLon() + i
                        * lonDiff, this);
                // add node
                // System.out.println(getCachingStrategy().getNodeCount());
                // System.out.println(getCachingStrategy().getConnectionCount());
                insertNode(newNode, lastNode.getConnectionTo(to));
                // System.out.println(getCachingStrategy().getNodeCount());
                // System.out.println(getCachingStrategy().getConnectionCount());
                nodes.add(newNode);
                lastNode = newNode;
            }
        }

        nodes.add(to);
        // make connection list
        result = new ArrayList<AggConnection>(nodes.size() - 1);
        for (int i = 1; i < nodes.size(); i++) {
            // System.out.println("adding conn "
            // + nodes.get(i - 1).getConnectionTo(nodes.get(i)));
            result.add(nodes.get(i - 1).getConnectionTo(nodes.get(i)));
        }

        return result;
    }

    public IAggregationStrategy getAggregationStrategy() {
        return aggregationStrategy;
    }

    public void setAggregationStrategy(IAggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
        if (aggregationStrategy != null) {
            aggregationStrategy.setAggContainer(AggContainer.this);
        }
    }

    public ICachingStrategy getCachingStrategy() {
        return cachingStrategy;
    }

    public void setCachingStrategy(ICachingStrategy cachingStrategy) {
        this.cachingStrategy = cachingStrategy;
        if (cachingStrategy != null) {
            cachingStrategy.clear();
            cachingStrategy.setAggContainer(AggContainer.this);
        }
    }

    @Override
    public String toString() {
        return toStringHelper(false);
    }

    /**
     * Get a string representation of this {@link AggContainer} as seen by later
     * steps in the process.
     *
     * @return
     */
    public String toStringVisible() {
        return toStringHelper(true);
    }

    private String toStringHelper(boolean onlyVisible) {
        StringBuilder sb = new StringBuilder("AggContainer: [\n");
        if (getCachingStrategy().getNodeCount() > 100) {
            sb.append(String.format("%d active nodes\n",
                    getCachingStrategy().getNodeCount()));
        } else {
            for (AggNode node : getCachingStrategy().clipRegion(TileManager.WORLD)) {
                if (!node.isVisible()) {
                    continue;
                }
                sb.append("\t").append(node);
                sb.append("\n\t\tin: ");
                for (AggConnection inConn : node.getIn()) {
                    if (!inConn.isVisible()) {
                        continue;
                    }
                    AggNode inNode = inConn.getFrom();
                    sb.append(inNode).append(" ");
                }
                sb.append("\n\t\tout: ");
                for (AggConnection outConn : node.getOut()) {
                    if (!outConn.isVisible()) {
                        continue;
                    }
                    AggNode outNode = outConn.getTo();
                    sb.append(outNode).append(
                            outConn.isComplete() ? " " : " (<- shallow) ");
                }
                sb.append("\n");
            }
        }
        return sb.append("]").toString();
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder("AggContainer (Debug): [\n");
        if (getCachingStrategy().getNodeCount() > 100) {
            sb.append(String.format("%d active nodes\n",
                    getCachingStrategy().getNodeCount()));
        } else {
            for (AggNode node : getCachingStrategy().clipRegion(TileManager.WORLD)) {
                sb.append("\t").append(node.toDebugString());
                sb.append("\n\t\tin: ");
                for (AggConnection inConn : node.getIn()) {
                    AggNode inNode = inConn.getFrom();
                    sb.append(inNode.toDebugString()).append(" ");
                }
                sb.append("\n\t\tout: ");
                for (AggConnection outConn : node.getOut()) {
                    AggNode outNode = outConn.getTo();
                    sb.append(outNode.toDebugString()).append(
                            outConn.isComplete() ? " " : " (<- shallow) ");
                }
                sb.append("\n");
            }
        }
        return sb.append("]").toString();
    }

    /**
     * Merges two nodes by moving all connections that lead to or originated
     * from the mergeSource to the mergeTarget.
     *
     * @param mergeSource
     * @param mergeTarget
     */
    public void mergeNodes(AggNode mergeSource, AggNode mergeTarget) {
        List<AggConnection> connections = new ArrayList<AggConnection>();
        connections.addAll(mergeSource.getIn());
        for (AggConnection conn : connections) {
            if (!conn.getFrom().equals(mergeTarget)) {
                conn.setTo(mergeTarget);
            } else {
                getCachingStrategy().removeConnection(conn);
            }
        }
        connections.clear();
        connections.addAll(mergeSource.getOut());
        for (AggConnection conn : connections) {
            if (!conn.getTo().equals(mergeTarget)) {
                conn.setFrom(mergeTarget);
            } else {
                getCachingStrategy().removeConnection(conn);
            }
        }
        // remove source
        getCachingStrategy().removeNode(mergeSource);
    }

    /**
     * Add new {@link AggNode}s between two other ones. Make sure the nodes are
     * added somewhat in order to prevent artifacts.
     *
     * @param before
     * @param after
     * @param newNodes
     * @return
     */
    public List<AggConnection> insertNodesOrdered(final AggNode before,
            AggNode after, List<AggNode> newNodes) {
        // TODO handle null for before or after
        // sort by distance to before
        if (newNodes != null) {
            Collections.sort(newNodes, new Comparator<AggNode>() {
                @Override
                public int compare(AggNode o1, AggNode o2) {
                    return (int) Math.signum(CartesianCalc
                            .getDistancePointToPoint(o1, before)
                            - CartesianCalc.getDistancePointToPoint(o2, before));
                }
            });
        }
        // add the nodes to the edge
        AggNode lastNode = before;
        List<AggConnection> result = new ArrayList<AggConnection>();
        // no new connection?
        if (newNodes == null || newNodes.isEmpty()) {
            result.add(before.getConnectionTo(after));
            return result;
        }
        for (AggNode node : newNodes) {
            insertNode(node, lastNode, after);
            result.add(lastNode.getConnectionTo(node));
            lastNode = node;
        }
        result.add(lastNode.getConnectionTo(after));
        return result;
    }

    /**
     * Add a new {@link AggNode} on an existing {@link AggConnection}.
     *
     * @param newNode
     * @param conn
     */
    public void insertNode(AggNode newNode, AggConnection conn) {
        addNode(newNode);
        insertNodeWithoutAdding(newNode, conn);
    }

    private void insertNodeWithoutAdding(AggNode newNode, AggConnection conn) {
        AggConnection newConn1 = connect(conn.getFrom(), newNode);
        AggConnection newConn2 = connect(newNode, conn.getTo());
        newConn1.inheritPropertiesFrom(conn);
        newConn2.inheritPropertiesFrom(conn);
        removeConnection(conn);
    }

    /**
     * Add a new {@link AggNode} between two existing nodes, taking both
     * directions into account.
     *
     * @param newNode
     * @param a
     * @param b
     */
    public void insertNode(AggNode newNode, AggNode a, AggNode b) {
        addNode(newNode);
        AggConnection conn = a.getConnectionTo(b);
        if (conn != null) {
            insertNodeWithoutAdding(newNode, conn);
        }
        conn = b.getConnectionTo(a);
        if (conn != null) {
            insertNodeWithoutAdding(newNode, conn);
        }
    }

    /**
     * Remove everything from the container.
     */
    public void clear() {
        if (getCachingStrategy() != null) {
            getCachingStrategy().clear();
            getAggregationStrategy().clear();
        }
    }

    /**
     * Make a new {@link AggConnection} between two {@link AggNode}s. If a
     * connection already exists, it is returned.
     *
     * @param from
     * @param to
     * @return
     */
    public AggConnection connect(AggNode from, AggNode to) {
        if (from == null || to == null) {
            return null;
        }
        // find already existing connection with same endpoints
        AggConnection existingConn = findConn(from, to);

        if (existingConn != null) {
            // add nodes
            if (ShallowAggNode.class
                    .isInstance(existingConn.getFrom())
                    && !ShallowAggNode.class
                    .isInstance(from)) {
                existingConn.fillFrom(from);
            }
            if (ShallowAggNode.class
                    .isInstance(existingConn.getTo())
                    && !ShallowAggNode.class
                    .isInstance(to)) {
                existingConn.fillTo(to);
            }
            return existingConn;
        } else {
            AggConnection conn = new AggConnection(from, to, this);
            if (getCachingStrategy() != null) {
                getCachingStrategy().addConnection(conn);
            }
            return conn;
        }
    }

    /**
     * Find an existing {@link AggConnection} between two {@link AggNode}s.
     *
     * @param from
     * @param to
     * @return
     */
    private AggConnection findConn(AggNode from, AggNode to) {
        if (from.isShallow()) {
            AggNode fromAggNode;
            try {
                fromAggNode = ((DefaultCachingStrategy) getCachingStrategy())
                        .getTm().getNodeByFullID(from.getInternalID());

                if (fromAggNode != null && fromAggNode.getOut() != null) {
                    for (AggConnection conn : fromAggNode.getOut()) {
                        if (conn.getFrom().equals(from)
                                && conn.getTo().equals(to)) {
                            return conn;
                        }
                    }
                }
            } catch (ParserConfigurationException e) {
                LOG.log(Level.INFO, e.getMessage(), e);
            } catch (SAXException e) {
                LOG.log(Level.INFO, e.getMessage(), e);
            } catch (IOException e) {
                LOG.log(Level.INFO, e.getMessage(), e);
            }
        } else if (to.isShallow()) {
            try {
                AggNode toAggNode = ((DefaultCachingStrategy) getCachingStrategy())
                        .getTm().getNodeByFullID(to.getInternalID());
                if (toAggNode != null && toAggNode.getIn() != null) {
                    for (AggConnection conn : toAggNode.getIn()) {
                        if (conn.getFrom().equals(from)
                                && conn.getTo().equals(to)) {
                            return conn;
                        }
                    }
                }
            } catch (ParserConfigurationException e) {
                LOG.log(Level.INFO, e.getMessage(), e);
            } catch (SAXException e) {
                LOG.log(Level.INFO, e.getMessage(), e);
            } catch (IOException e) {
                LOG.log(Level.INFO, e.getMessage(), e);
            }
        }
        return null;
    }
}
