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
package de.fub.agg2graph.graph;

import java.util.ArrayList;
import java.util.List;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.structs.Path;

/**
 * Filters data using Ramer-Douglas-Peucker algorithm with specified tolerance
 *
 * @author Rzeźnik, Johannes Mitlmeier
 * @see <a
 * href="http://en.wikipedia.org/wiki/Ramer-Douglas-Peucker_algorithm">Ramer-Douglas-Peucker
 * algorithm</a>
 * @see <a
 * href="http://code.google.com/p/savitzky-golay-filter/source/browse/trunk/src/mr/go/sgfilter/RamerDouglasPeuckerFilter.java?r=15">adapted
 * from here</a>
 */
public class RamerDouglasPeuckerFilter {

    private double epsilon;
    private double maxSegmentLength = 100; // meters

    public RamerDouglasPeuckerFilter() {
    }

    public RamerDouglasPeuckerFilter(double epsilon) {
        if (epsilon < 0) {
            throw new IllegalArgumentException("Epsilon must be >= 0");
        }
        this.epsilon = epsilon;
    }

    public RamerDouglasPeuckerFilter(double epsilon, double maxSegmentLength) {
        this(epsilon);
        this.maxSegmentLength = maxSegmentLength;
    }

    public List<ILocation> getRemovablePoints(List<? extends ILocation> data) {
        return getRemovablePoints(data, 0, data.size() - 1);
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getMaxSegmentLength() {
        return maxSegmentLength;
    }

    public void setMaxSegmentLength(double maxSegmentLength) {
        this.maxSegmentLength = maxSegmentLength;
    }

    protected List<ILocation> getRemovablePoints(
            List<? extends ILocation> points, int startIndex, int endIndex) {
        double dmax = 0;
        int idx = 0;
        if (endIndex <= startIndex || epsilon == 0) {
            return new ArrayList<ILocation>(0);
        }

        ILocation start = points.get(startIndex);
        ILocation end = points.get(endIndex);
        for (int i = startIndex + 1; i < endIndex; i++) {
            double distance = GPSCalc.getDistancePointToEdge(points.get(i),
                    start, end);
            // System.out.println(distance);
            if (distance > dmax) {
                idx = i;
                dmax = distance;
            }
        }
        if (dmax >= epsilon) {
            List<ILocation> recursiveResult1 = getRemovablePoints(points,
                    startIndex, idx);
            List<ILocation> recursiveResult2 = getRemovablePoints(points, idx,
                    endIndex);
            List<ILocation> result = new ArrayList<ILocation>(
                    recursiveResult1.size() + recursiveResult2.size());
            result.addAll(recursiveResult1);
            result.addAll(recursiveResult2);
            return result;
        } else {
            List<ILocation> result = new ArrayList<ILocation>(endIndex
                    - startIndex);
            result.addAll(points.subList(startIndex + 1, endIndex));
            return result;
        }
    }

    /**
     *
     * @param epsilon maximum distance of a point in data between original curve
     * and simplified curve
     */
    public void setEpsilon(double epsilon) {
        if (epsilon < 0) {
            throw new IllegalArgumentException("Epsilon must be >= 0");
        }
        this.epsilon = epsilon;
    }

    /**
     * Simplify a {@link GPSSegment} using this filter.
     *
     * @param cleanSegment
     * @return Simplified {@link GPSSegment}.
     */
    public GPSSegment simplify(GPSSegment cleanSegment) {
        List<ILocation> removablePoints = getRemovablePoints(cleanSegment);
        List<GPSPoint> points = cleanSegment;
        for (ILocation point : removablePoints) {
            points.remove(point);
        }
        return cleanSegment;
    }

    /**
     * Simplify a {@link AggContainer} using this filter.
     *
     * @param aggPoints
     * @param agg
     * @return
     */
    public List<AggNode> simplifyAgg(List<AggNode> aggPoints, AggContainer agg) {
        List<ILocation> removablePoints = getRemovablePoints(aggPoints);
        AggNode node = null;
        for (ILocation point : removablePoints) {
            node = (AggNode) point;
            if (!node.isAggIntersection()) {
                agg.extractNode(node);
                aggPoints.remove(node);
            }
        }
        // check for edges that are too long and fix them
        List<AggNode> result = new ArrayList<AggNode>();
        for (int i = 1; i < aggPoints.size(); i++) {
            AggNode lastNode = aggPoints.get(i - 1);
            AggNode thisNode = aggPoints.get(i);
            if (result.size() > 1
                    && !result.get(result.size() - 1).equals(lastNode)) {
                result.add(lastNode);
            }
            double dist = GPSCalc.getDistance(lastNode, thisNode);
            if (dist > maxSegmentLength) {
                AggConnection conn = lastNode.getConnectionTo(thisNode);
                if (conn != null) {
                    List<AggConnection> newConnections = agg.splitConnection(
                            conn, (int) Math.ceil(dist / maxSegmentLength));
                    for (AggConnection newConn : newConnections) {
                        result.add(newConn.getTo());
                    }
                }
            } else {
                result.add(thisNode);
            }
        }
        return result;
    }

    /**
     * Simplify a {@link Path} using this filter. Typically used when making a
     * {@link RoadNetwork}.
     *
     * @param path
     * @return
     */
    public List<ILocation> simplify(Path<? extends ILocation> path) {
        List<ILocation> clonedNodes = path.getClonedNodes();
        ILocation lastPoint = clonedNodes.get(clonedNodes.size() - 1);
        List<ILocation> removablePoints = getRemovablePoints(clonedNodes);
        for (ILocation point : removablePoints) {
            clonedNodes.remove(point);
        }
        // make sure the last point is in the result, but only once
        if (clonedNodes.lastIndexOf(lastPoint) != clonedNodes.size() - 1) {
            clonedNodes.add(lastPoint);
        }
        return clonedNodes;
    }
}
