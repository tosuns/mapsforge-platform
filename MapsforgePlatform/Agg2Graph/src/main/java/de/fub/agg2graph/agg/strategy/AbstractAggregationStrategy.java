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
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.agg2graph.structs.ClassObjectEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractAggregationStrategy implements
        IAggregationStrategy {

    protected AggContainer aggContainer;
    protected ITraceDistance traceDistance;
    protected List<IMergeHandler> matches;
    protected IMergeHandler baseMergeHandler;
    protected IMergeHandler mergeHandler;
    protected AggNode lastNode = null;

    @Override
    public void clear() {
        lastNode = null;
    }

    /**
     * Add an {@link AggNode} to the {@link AggContainer}, automatically connect
     * it to the last {@link AggNode} added.
     *
     * @param agg
     * @param node
     */
    protected void addNodeToAgg(AggContainer agg, AggNode node) {
        agg.addNode(node);
        if (lastNode != null) {
            agg.addConnection(new AggConnection(lastNode, node, agg));
        }
        // logger.debug("Added node " + node);
    }

    public List<IMergeHandler> getMatches() {
        return matches;
    }

    @Override
    public void setAggContainer(AggContainer aggContainer) {
        this.aggContainer = aggContainer;
    }

    @Override
    public AggContainer getAggContainer() {
        return aggContainer;
    }

    @Override
    public AggConnection mergeConnections(AggConnection newConn,
            AggConnection oldConn) {
        return oldConn;
    }

    @Override
    public ITraceDistance getTraceDist() {
        return traceDistance;
    }

    @Override
    public AggConnection combineConnections(AggConnection firstConn,
            AggConnection secondConn) {
        AggConnection conn = new AggConnection(firstConn.getFrom(),
                secondConn.getTo(), aggContainer);
        conn.setWeight((firstConn.getWeight() + secondConn.getWeight()) / 2);
        conn.setAvgDist((firstConn.getAvgDist() + secondConn.getAvgDist()) / 2);
        return conn;
    }

    public IMergeHandler getMergeHandler() {
        return mergeHandler;
    }

    @Override
    public List<ClassObjectEditor> getSettings() {
        List<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>();
        result.add(new ClassObjectEditor(this, Arrays.asList(new String[]{
            "baseMergeHandler", "aggContainer"})));
        result.add(new ClassObjectEditor(getTraceDist()));
        result.add(new ClassObjectEditor(this.baseMergeHandler, Arrays
                .asList(new String[]{"distance", "rdpf", "aggContainer"})));
        return result;
    }
}
