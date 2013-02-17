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
package de.fub.agg2graph.agg;

import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import java.util.List;

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
