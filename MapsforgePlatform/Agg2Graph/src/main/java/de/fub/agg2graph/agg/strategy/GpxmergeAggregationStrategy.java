/**
 * *****************************************************************************
 * Copyright 2013 Sebastian Müller
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
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.AggregationStrategyFactory;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.MergeHandlerFactory;
import de.fub.agg2graph.agg.TraceDistanceFactory;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GpxmergeAggregationStrategy extends AbstractAggregationStrategy {

    private static final Logger LOG = Logger.getLogger(GpxmergeAggregationStrategy.class.getName());
    private double maxInitDistance = 100;
    private double maxPathDifference = 2;

    /**
     * Preferably use the {@link AggregationStrategyFactory} for creating
     * instances of this class.
     */
    public GpxmergeAggregationStrategy() {
        traceDistance = TraceDistanceFactory.getObject();
        baseMergeHandler = MergeHandlerFactory.getObject();
    }

    public double getMaxInitDistance() {
        return maxInitDistance;
    }

    public void setMaxInitDistance(double maxInitDistance) {
        this.maxInitDistance = maxInitDistance;
    }

    public double getMaxPathDifference() {
        return maxPathDifference;
    }

    public void setMaxPathDifference(double maxPathDifference) {
        this.maxPathDifference = maxPathDifference;
    }

    @Override
    public void aggregate(GPSSegment segment) {
        LOG.setLevel(Level.ALL);

        matches = new ArrayList<IMergeHandler>();

        // insert first segment without changes
        if (aggContainer.getCachingStrategy() == null
                || aggContainer.getCachingStrategy().getNodeCount() == 0) {
            int i = 0;
            while (i < segment.size()) {
                AggNode node = new AggNode(segment.get(i), aggContainer);
                node.setID(MessageFormat.format("A-{0}", segment.get(i).getID()));
                addNodeToAgg(aggContainer, node);
                lastNode = node;
                i++;
            }
            return;
        }

        lastNode = null;
        int i = 0;
        // step 1: find starting point
        // get close edges, within x meters (merge candidates)
        Set<AggConnection> nearEdges = null;
        while (i < segment.size() - 1) {
            GPSPoint firstPoint = segment.get(i);
            GPSPoint secondPoint = segment.get(i + 1);
            GPSEdge currentEdge = new GPSEdge(firstPoint, secondPoint);
            nearEdges = aggContainer.getCachingStrategy().getCloseConnections(
                    currentEdge, maxInitDistance);
            boolean addNode = true;
            LOG.warning(MessageFormat.format("{0} near edges", nearEdges.size()));
            if (!nearEdges.isEmpty()) {
                IMergeHandler mergeHandler = null;
                mergeHandler = baseMergeHandler.getCopy();
                mergeHandler.setAggContainer(aggContainer);
                Iterator<AggConnection> itNear = nearEdges.iterator();
                Double grade = Double.MAX_VALUE;
                AggConnection bestConn = null;
                while (itNear.hasNext()) {
                    AggConnection near = itNear.next();
                    Object[] distReturn = traceDistance.getPathDifference(
                            near.toPointList(), currentEdge.toPointList(), 0,
                            mergeHandler);
                    double dist = (Double) distReturn[0];
                    if (dist < grade && dist < maxPathDifference) {
                        grade = dist;
                        bestConn = near;
                    }
                }
                if (bestConn != null) {
                    addNode = false;
                    mergeHandler.addAggNodes(bestConn);
                    mergeHandler.addGPSPoints(currentEdge);
                    mergeHandler.setDistance(grade);
                    if (!mergeHandler.isEmpty()) {
                        matches.add(mergeHandler);

                        mergeHandler.processSubmatch();
                        // connect to previous node
                        aggContainer
                                .connect(lastNode, mergeHandler.getInNode());
                        mergeHandler.setBeforeNode(lastNode);
                        // remember outgoing node (for later connection)
                        lastNode = mergeHandler.getOutNode();
                    }
                }
            }
            if (addNode) {
                AggNode node = new AggNode(firstPoint, aggContainer);
                node.setID(MessageFormat.format("A-{0}", firstPoint.getID()));
                addNodeToAgg(aggContainer, node);
                lastNode = node;
            }
            i++;
        }
        // step 2 and 3 of 3: ghost points, merge everything
        for (IMergeHandler match : matches) {
            if (!match.isEmpty()) {
                match.mergePoints();
            }
        }
    }
}
