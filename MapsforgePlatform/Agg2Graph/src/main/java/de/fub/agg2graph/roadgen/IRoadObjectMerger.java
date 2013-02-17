/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph.roadgen;

public interface IRoadObjectMerger {
	/**
	 * Try to reduce the number of intersections by merging those which are
	 * "close" to an arbitrary definition.
	 */
	public void mergeInteresections(RoadNetwork roadNetwork);

	/**
	 * Try to reduce the number of {@link Road}s by merging.
	 */
	public void mergeRoads(RoadNetwork roadNetwork);

	/**
	 * Take two {@link Road} objects of opposite direction and merge them to
	 * become one two-way {@link Road}.
	 * 
	 * @param r1
	 * @param r2
	 */
	public void mergeRoadPair(RoadNetwork roadNetwork, Road r1, Road r2);

	/**
	 * Take two {@link Intersection} objects and merge them to become one with
	 * appropriate {@link Road}s attached.
	 * 
	 * @param i1
	 * @param i2
	 */
	public void mergeIntersectionPair(RoadNetwork roadNetwork, Intersection i1,
			Intersection i2);
}
