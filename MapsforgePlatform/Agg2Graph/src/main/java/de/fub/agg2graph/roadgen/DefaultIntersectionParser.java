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
package de.fub.agg2graph.roadgen;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.IEdge;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Methods for finding and processing intersections in the aggregated data in
 * order to enable other classes to transform the data to a street graph.
 *
 * @author Johannes Mitlmeier
 *
 */
public class DefaultIntersectionParser implements IIntersectionParser {

    private static final Logger logger = Logger
            .getLogger("agg2graph.roadgen.intersectionparser");
    private RoadNetwork roadNetwork;
    private AggContainer agg;

    @Override
    public void makeNetwork(RoadNetwork roadNetwork, AggContainer agg) {
        this.roadNetwork = roadNetwork;
        this.agg = agg;
        makeIntersections();
        makeRoads();
    }

    /**
     * An intersection candidate is an {@link AggNode} that is either already
     * having a intersection structure itself or is at the end of a line.
     *
     * @param agg
     * @return
     */
    private Set<AggNode> getIntersectionCandidates(AggContainer agg) {
        Set<AggNode> candidates = new HashSet<AggNode>();
        Set<AggNode> nodes = agg.getCachingStrategy().getLoadedNodes();
        for (AggNode node : nodes) {
            if (!node.isVisible()) {
                continue;
            }
            if (node.isAggIntersection() || node.isEndNode()) {
                candidates.add(node);
            }
        }
        return candidates;
    }

    private void makeIntersections() {
        // get candidates
        Set<AggNode> candidates = getIntersectionCandidates(agg);
        for (AggNode candidate : candidates) {
            Intersection intersection = new Intersection(candidate);
            logger.info(MessageFormat.format("intersection found: {0}", intersection));
            // intersection.out = candidate.getVisibleOut();
            // intersection.in = candidate.getVisibleIn();
            roadNetwork.intersections.add(intersection);
        }
    }

    private void makeRoads() {
        // parse roads
        Iterator<Intersection> it = roadNetwork.intersections.iterator();
        List<Intersection> newIntersections = new ArrayList<Intersection>(10);
        while (it.hasNext()) {
            Intersection startIntersection = it.next();
            // every outgoing road
            for (AggConnection c : startIntersection.baseNode.getVisibleOut()) {
                Road road = new Road();
                road.setFrom(startIntersection);
                startIntersection.out.add(road);
                AggConnection currentConn = c;
                HashSet<AggConnection> set = new HashSet<AggConnection>();
                // follow the road to the next intersection
                while (currentConn.getTo().getIntersection() == null) {
                    road.path.add(currentConn);
                    if (currentConn.getTo().getVisibleOut() == null
                            || !currentConn.getTo().getVisibleOut().iterator().hasNext()) {

                        Intersection newIntersection = new Intersection(currentConn.getTo());
                        newIntersection.in.add(road);
                        newIntersections.add(newIntersection);
                        break;
                    } else if (set.contains(currentConn)) {
                        IEdge<AggNode> node = null;

                        if (!road.path.isEmpty()) {
                            // we get the last node of the path
                            node = road.path.get(road.path.size() - 1);
                        } else {
                            // current node is the first node in the path
                            node = currentConn;
                        }
                        // create a inter section
                        Intersection newIntersection = new Intersection(node.getTo());
                        newIntersection.in.add(road);
                        newIntersections.add(newIntersection);
                        break;
                    } else {
                        if (currentConn.getTo().getVisibleOut().size() != 1) {
                            logger.severe("Error not exactly one element in set!");
                        }

                        // check whether there is a cycle
                        // and if so break loop
                        if (set.contains(currentConn)) {
                            // attempt to make the condition
                            currentConn.getTo().setIntersection(null);
                            break;
                        }
                        set.add(currentConn);
                        currentConn = currentConn.getTo().getVisibleOut()
                                .iterator().next(); // there is only one,
                        // otherwise
                        // it would
                        // have been an intersection
                        // itself
                    }
                }

                /// add road to road network only when there is valid end intersection
                if (currentConn.getTo().getIntersection() != null) {
                    road.path.add(currentConn);
                    Intersection endIntersection = currentConn.getTo()
                            .getIntersection();
                    road.setTo(endIntersection);
                    endIntersection.in.add(road);
                    roadNetwork.roads.add(road);
                }
            }
        }
        roadNetwork.intersections.addAll(newIntersections);
    }
}
