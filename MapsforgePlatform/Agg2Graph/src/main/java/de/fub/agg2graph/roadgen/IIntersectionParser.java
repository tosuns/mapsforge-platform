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

import de.fub.agg2graph.agg.AggContainer;

/**
 * Methods for finding and processing intersections in the aggregated data in
 * order to enable other classes to transform the data to a street graph.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public interface IIntersectionParser {

	public void makeNetwork(RoadNetwork roadNetwork, AggContainer agg);
}
