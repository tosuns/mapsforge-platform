/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
package de.fub.agg2graph.agg;

import java.util.List;

import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;

/**
 * Class for handling the merge of a set of {@link GPSPoint}s from the trace
 * being added and a correspondig set of {@link AggNode} objects from an
 * {@link AggContainer}.
 *
 * @author Johannes Mitlmeier
 *
 */
public interface IMergeHandler {
    /**
     * Actually merge the points after the full match has been identified.
     */
    public void mergePoints();

    public boolean isEmpty();

    public List<AggNode> getAggNodes();

    public List<GPSPoint> getGpsPoints();

    public List<PointGhostPointPair> getPointGhostPointPairs();

    public double getDistance();

    /**
     * Save the distance the enclosed nodes had before being merged so that it
     * can be attached to the edges and nodes afterwards.
     */
    public void setDistance(double bestDifference);

    /**
     * Get the first {@link AggNode} of the merged path. This node should be
     * connected to the node inserted in the {@link AggContainer} before.
     *
     * @return
     */
    public AggNode getInNode();

    /**
     * Get the last {@link AggNode} of the merged path. This node should be
     * connected to the node inserted in the {@link AggContainer} next.
     *
     * @return
     */
    public AggNode getOutNode();

    public void setBeforeNode(AggNode lastNode);

    /**
     * After adding one sub-match this method is called and can handle that.
     */
    public void processSubmatch();

    public void addAggNode(AggNode aggNode);

    public void addAggNodes(AggConnection bestConn);

    public void addAggNodes(List<AggNode> aggNodes);

    public void addGPSPoint(GPSPoint gpsPoint);

    public void addGPSPoints(GPSEdge currentEdge);

    public void addGPSPoints(List<GPSPoint> gpsPoints);

    /**
     * Get a copy of the object with the same settings applied (used for
     * enabling parameters to be set from outside).
     *
     * @return
     */
    public IMergeHandler getCopy();

    public AggContainer getAggContainer();

    public void setAggContainer(AggContainer aggContainer);

    /**
     * Return a list of settings to expose to the user via interfaces or the
     * project files.
     *
     * @return
     */
    public List<ClassObjectEditor> getSettings();
}
