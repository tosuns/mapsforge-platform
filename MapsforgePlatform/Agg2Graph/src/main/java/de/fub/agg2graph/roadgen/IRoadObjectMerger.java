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
