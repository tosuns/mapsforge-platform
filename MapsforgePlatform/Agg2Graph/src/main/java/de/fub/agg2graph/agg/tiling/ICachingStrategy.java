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
package de.fub.agg2graph.agg.tiling;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.List;
import java.util.Set;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.IEdge;
import de.fub.agg2graph.structs.ILocation;

/**
 * Strategy for caching data. Includes methods for adding, removing, loading,
 * and saving {@link AggNode}s and {@link AggConnection}s. Also handles
 * proximity searches and clipping {@link AggNode}s from a specified area.
 * 
 * @author Johannes Mitlmeier
 */
public interface ICachingStrategy {

	public AggNode getNode(String ID);

	/**
	 * Make sure that a specific node is loaded and can be accessed just like
	 * any node that would not be or have been cached.
	 * 
	 * @param node
	 * @return
	 */
	public AggNode loadNode(AggNode node);

	public void addNode(AggNode node);

	public void addConnection(AggConnection connection);

	/**
	 * Serialize the underlying {@link AggContainer}.
	 */
	public void save();

	/**
	 * Clear the caches.
	 */
	public void clear();

	/**
	 * Get the number of nodes in the {@link AggContainer}. This should yield
	 * the actual number of nodes, but can be unreliable because it might not be
	 * efficient for certain caching strategies. Don't rely on this value too
	 * much!
	 */
	public int getNodeCount();

	public int getConnectionCount();

	/**
	 * Query {@link AggNode}s in a certain range around an {@link ILocation}.
	 * 
	 * @param loc
	 * @param maxDist
	 * @return
	 */
	public Set<AggNode> getCloseNodes(ILocation loc, double maxDist);

	public void setAggContainer(AggContainer aggContainer);

	public AggContainer getAggContainer();

	public void removeNode(AggNode node);

	public void removeConnection(AggConnection conn);

	public List<AggNode> clipRegionProjected(Rectangle2D.Double rect);

	/**
	 * Query nodes inside an {@link Double} rectangle.
	 * 
	 * @param loc
	 * @param maxDist
	 * @return
	 */
	public List<AggNode> clipRegion(Rectangle2D.Double rect);

	/**
	 * Get the number of {@link AggConnection}s that currently loaded.
	 * 
	 * @return
	 */
	public Set<AggConnection> getLoadedConnections();

	/**
	 * Get the number of {@link AggNode}s that currently loaded.
	 * 
	 * @return
	 */
	public Set<AggNode> getLoadedNodes();

	public void addConnectionCounter(int i);

	/**
	 * Query {@link AggConnection}s in a certain range around an {@link IEdge}.
	 * 
	 * @param loc
	 * @param maxDist
	 * @return
	 */
	public Set<AggConnection> getCloseConnections(
			IEdge<? extends ILocation> currentEdge, double maxDist);
}
