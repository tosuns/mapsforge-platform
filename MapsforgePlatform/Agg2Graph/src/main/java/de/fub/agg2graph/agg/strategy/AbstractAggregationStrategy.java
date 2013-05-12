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
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractAggregationStrategy implements
        IAggregationStrategy {

    private static final Logger LOG = Logger.getLogger(AbstractAggregationStrategy.class.getName());
    protected AggContainer aggContainer;
    protected ITraceDistance traceDistance;
    protected List<IMergeHandler> matches;
    protected IMergeHandler baseMergeHandler;
    protected IMergeHandler mergeHandler;
    protected AggNode lastNode = null;

    @Override
    public ITraceDistance getTraceDist() {
        return traceDistance;
    }

    public IMergeHandler getBaseMergeHandler() {
        return baseMergeHandler;
    }

    public IMergeHandler getMergeHandler() {
        return mergeHandler;
    }

    public List<IMergeHandler> getMatches() {
        return matches;
    }

    @Override
    public AggContainer getAggContainer() {
        return aggContainer;
    }

    @Override
    public void setAggContainer(AggContainer aggContainer) {
        this.aggContainer = aggContainer;
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
        LOG.log(Level.FINEST, "Added node {0}", node);
    }

    @Override
    public AggConnection mergeConnections(AggConnection newConn, AggConnection oldConn) {
        return oldConn;
    }

    @Override
    public AggConnection combineConnections(AggConnection firstConn, AggConnection secondConn) {
        AggConnection conn = new AggConnection(
                firstConn.getFrom(),
                secondConn.getTo(),
                aggContainer);
        conn.setWeight((firstConn.getWeight() + secondConn.getWeight()) / 2);
        conn.setAvgDist((firstConn.getAvgDist() + secondConn.getAvgDist()) / 2);
        return conn;
    }

    @Override
    public void clear() {
        lastNode = null;
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
