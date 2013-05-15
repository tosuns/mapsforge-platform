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
package de.fub.agg2graph.agg.tiling;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.IEdge;
import de.fub.agg2graph.structs.ILocation;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class DefaultCachingStrategy implements ICachingStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultCachingStrategy.class.getName());
    protected File sourceFolder = null;
    protected AggContainer agg;
    protected TileManager tm;
    protected TileCache tc;

    public DefaultCachingStrategy() {
        tc = new TileCache(this, null, 100);
        tm = new TileManager(this);
        // TODO initialize the tile cache (Google Guava?)
    }

    public TileManager getTm() {
        return tm;
    }

    public void setTm(TileManager tm) {
        this.tm = tm;
    }

    public TileCache getTc() {
        return tc;
    }

    public void setTc(TileCache tc) {
        this.tc = tc;
    }

    @Override
    public AggNode getNode(String ID) {
        // TODO implement this
        return null;
    }

    @Override
    public void addNode(AggNode node) {
        /*
         * Before adding a node we load the tile surrounding it, so that
         * consistency can be achieved.
         */
        tm.addElement(node);
    }

    @Override
    public void addConnection(AggConnection connection) {
        tm.addConnection(connection);
    }

    @Override
    public void save() {
        // TODO implement invalidation algorithm to prevent saving unchanged
        // data unnecessarily
        try {
            tc.saveTile(tm.getRoot());
        } catch (ParserConfigurationException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (TransformerException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public int getNodeCount() {
        return tm.getNodeCount();
    }

    /**
     * maxDist in meters
     */
    @Override
    public Set<AggNode> getCloseNodes(ILocation loc, double maxDist) {
        return tm.getCloseElements(loc, maxDist);
    }

    @Override
    public void setAggContainer(AggContainer aggContainer) {
        this.agg = aggContainer;
        tm.setAggContainer(agg);
        tc.setAggContainer(agg);
    }

    @Override
    public AggContainer getAggContainer() {
        return agg;
    }

    @Override
    public String toString() {
        return String
                .format("DefaultCachingStrategy, %d nodes", getNodeCount());
    }

    @Override
    public void removeNode(AggNode node) {
        tm.removeElement(node);
    }

    @Override
    public void removeConnection(AggConnection conn) {
        // load both nodes
        conn.fillFrom(loadNode(conn.getFrom()));
        conn.fillTo(loadNode(conn.getTo()));
        // remove the connection for real
        boolean failed = false;
        failed = !conn.getFrom().getOut().remove(conn) || failed;
        failed = !conn.getTo().getIn().remove(conn) || failed;
        if (!failed) {
            tm.removeConnection(conn); // reduces connection counter by 1
            conn = null;
        } else {
            LOG.log(Level.FINE, "REMOVAL ERROR!"); // NO18N
        }
    }

    @Override
    public AggNode loadNode(AggNode node) {
        if (node.isShallow()) {
            return tc.loadNode(node.getInternalID());
        }
        return node;
    }

    @Override
    public void clear() {
        tm.clear();
    }

    @Override
    public int getConnectionCount() {
        return tm.getConnectionCount();
    }

    @Override
    public List<AggNode> clipRegionProjected(Double rect) {
        return tm.clipRegionProjected(rect);
    }

    @Override
    public List<AggNode> clipRegion(Double rect) {
        return tm.clipRegion(rect);
    }

    public TileManager getTileManager() {
        return tm;
    }

    @Override
    public Set<AggConnection> getLoadedConnections() {
        Set<AggNode> nodes = getLoadedNodes();
        Set<AggConnection> conns = new HashSet<AggConnection>();
        for (AggNode node : nodes) {
            for (AggConnection conn : node.getOut()) {
                if (conn.isComplete()) {
                    conns.add(conn);
                }
            }
        }
        return conns;
    }

    @Override
    public Set<AggNode> getLoadedNodes() {
        Set<Tile<AggNode>> tiles = tc.getActiveTiles();
        Set<AggNode> nodes = new HashSet<AggNode>();
        for (Tile<AggNode> tile : tiles) {
            nodes.addAll(tile.getInnerNodes());
        }
        return nodes;
    }

    @Override
    public void addConnectionCounter(int i) {
        tm.addConnectionCounter(i);
    }

    @Override
    public Set<AggConnection> getCloseConnections(
            IEdge<? extends ILocation> edge, double maxDist) {
        return tm.getCloseConnections(edge, maxDist);
    }
}
