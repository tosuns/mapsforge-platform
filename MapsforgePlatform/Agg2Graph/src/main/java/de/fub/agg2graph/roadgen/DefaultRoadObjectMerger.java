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

import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.IEdge;
import de.fub.agg2graph.structs.ILocation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class DefaultRoadObjectMerger implements IRoadObjectMerger {

    private static final Logger logger = Logger
            .getLogger("agg2graph.roadgen.merger");
    public double maxIntersectionMergeDistance = 30;
    public double maxRoadMergeDistance = 50;

    @Override
    public void mergeInteresections(RoadNetwork roadNetwork) {
        List<Intersection[]> mergeableIntersections = new ArrayList<Intersection[]>(
                10);

        for (Intersection i1 : roadNetwork.intersections) {
            if (!i1.isVisible()) {
                continue;
            }
            // get close intersections
            Set<AggNode> closeNodes = i1.baseNode.getAggContainer()
                    .getCachingStrategy()
                    .getCloseNodes(i1.baseNode, maxIntersectionMergeDistance);
            for (AggNode node : closeNodes) {
                Intersection i2 = node.getIntersection();
                if (i2 == null || !i2.isVisible()) {
                    continue;
                }
                // we have a close intersection...
                boolean connected = i1.isDirectlyConnectedTo(i2);
                // boolean justOnePseudo = i1.isPseudo() ^ i2.isPseudo();
                // boolean minOnePseudo = i1.isPseudo() || i2.isPseudo();
                boolean maxOnePseudo = !i1.isPseudo() || !i2.isPseudo();
                boolean allPseudo = i1.isPseudo() && i2.isPseudo();
                if ((connected && maxOnePseudo) || (!connected && allPseudo)) {
                    // ...that can be merged
                    mergeableIntersections.add(new Intersection[]{i1, i2});
                }
            }
        }

        // do the merges
        for (Intersection[] i : mergeableIntersections) {
            mergeIntersectionPair(roadNetwork, i[0], i[1]);
        }

        // remove the rest
        for (Intersection i : roadNetwork.intersections) {
            if (i.mergedTo != null) {
                i = null;
            }
        }
    }

    @Override
    public void mergeIntersectionPair(RoadNetwork roadNetwork, Intersection i1,
            Intersection i2) {
        if (i1 == i2 || i1.equals(i2)
                || (i1.mergedTo != null && i2.mergedTo != null)) {
            return;
        }
        if (i1.mergedTo != null) {
            i1 = i1.mergedTo;
        }
        if (i2.mergedTo != null) {
            i2 = i2.mergedTo;
        }
        logger.info(MessageFormat.format("merging {0} with {1} dist: {2}", i1, i2, GPSCalc.getDistance(i1, i2)));
        AggNode aggNode = new AggNode(GPSCalc.getMidwayLocation(i1, i2),
                i1.baseNode.getAggContainer());
        Intersection newIntersection = new Intersection(aggNode);
        newIntersection.in = i1.in;
        newIntersection.in.addAll(i2.in);
        newIntersection.out = i1.out;
        newIntersection.out.addAll(i2.out);
        for (Road r : newIntersection.in) {
            r.setTo(newIntersection);
        }
        for (Road r : newIntersection.out) {
            r.setFrom(newIntersection);
        }
        i1.mergedTo = newIntersection;
        i2.mergedTo = newIntersection;
        int before = roadNetwork.intersections.size();
        roadNetwork.intersections.remove(i1);
        roadNetwork.intersections.remove(i2);
        roadNetwork.intersections.add(newIntersection);
//        assert before - 1 == roadNetwork.intersections.size();
    }

    @Override
    public void mergeRoads(RoadNetwork roadNetwork) {
        List<Road[]> mergeableRoads = new ArrayList<Road[]>(10);

        for (Intersection i : roadNetwork.intersections) {
            for (Road r1 : i.out) {
                // is there a road in the backwards direction?
                Set<Road> backward = r1.getTo().out;
                Iterator<Road> backwardIt = backward.iterator();
                while (backwardIt.hasNext()) {
                    Road r2 = backwardIt.next();
                    logger.info(r1.toString());
                    logger.info(r2.toString());
                    if (r2.getTo().equals(r1.getFrom())) {
                        mergeableRoads.add(new Road[]{r1, r2});
                    }
                }
            }
        }

        // do the merges
        for (Road[] r : mergeableRoads) {
            mergeRoadPair(roadNetwork, r[0], r[1]);
        }

        // remove the rest
        for (Road r : roadNetwork.roads) {
            if (r.mergedTo != null) {
                r = null;
            }
        }
    }

    @Override
    public void mergeRoadPair(RoadNetwork roadNetwork, Road r1, Road r2) {
        if (r1 == r2 || r1.equals(r2)
                || (r1.mergedTo != null && r2.mergedTo != null)) {
            return;
        }
        if (r1.mergedTo != null) {
            r1 = r1.mergedTo;
        }
        if (r2.mergedTo != null) {
            r2 = r2.mergedTo;
        }
        // close enough?
        boolean close = true;
        List<? extends ILocation> r2nodes = r2.getNodes(), r1nodes = r1
                .getNodes();
        for (IEdge<AggNode> edge : r1.path) {
            if (!GPSCalc.isDistancePointToTraceBelowLimit(edge.getFrom(),
                    r2nodes, maxRoadMergeDistance)) {
                close = false;
                break;
            }
        }
        if (close) {
            for (IEdge<AggNode> edge : r2.path) {
                if (!GPSCalc.isDistancePointToTraceBelowLimit(edge.getFrom(),
                        r1nodes, maxRoadMergeDistance)) {
                    close = false;
                    break;
                }
            }
        }

        // merge and change the oneway attribute's value
        if (close) {
            logger.info("merging " + r1 + " with " + r2);
            r1.setOneWay(false);
            r2.getFrom().out.remove(r2);
            r2.getTo().in.remove(r2);
            roadNetwork.roads.remove(r2);
            r2.mergedTo = r1;
            r1.mergedTo = r1;
        }
    }
}
