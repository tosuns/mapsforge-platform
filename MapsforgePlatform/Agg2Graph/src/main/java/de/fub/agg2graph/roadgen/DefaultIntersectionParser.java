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
package de.fub.agg2graph.roadgen;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
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
            logger.info("intersection found: " + intersection);
            // intersection.out = candidate.getVisibleOut();
            // intersection.in = candidate.getVisibleIn();
            roadNetwork.intersections.add(intersection);
        }
    }

    private void makeRoads() {
        // parse roads
        Iterator<Intersection> it = roadNetwork.intersections.iterator();
        List<Intersection> newIntersections = new ArrayList<Intersection>(200);
        while (it.hasNext()) {
            Intersection startIntersection = it.next();
            // every outgoing road
            for (AggConnection c : startIntersection.baseNode.getVisibleOut()) {
                Road road = new Road();
                road.setFrom(startIntersection);
                startIntersection.out.add(road);
                AggConnection currentConn = c;
                // follow the road to the next intersection
                while (currentConn.getTo().getIntersection() == null) {
                    road.path.add(currentConn);

                    if (currentConn.getTo().getVisibleOut() == null
                            || !currentConn.getTo().getVisibleOut().iterator()
                            .hasNext()) {
                        Intersection newIntersection = new Intersection(
                                currentConn.getTo());
                        newIntersection.in.add(road);
                        newIntersections.add(newIntersection);
                        break;
                    } else {
                        currentConn = currentConn.getTo().getVisibleOut()
                                .iterator().next(); // there is only one,
                        // otherwise
                        // it would
                        // have been an intersection
                        // itself
                    }
                }
                road.path.add(currentConn);
                Intersection endIntersection = currentConn.getTo()
                        .getIntersection();
                road.setTo(endIntersection);
                endIntersection.in.add(road);
                roadNetwork.roads.add(road);
            }
        }
        roadNetwork.intersections.addAll(newIntersections);
    }
}
