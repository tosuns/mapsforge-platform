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
package de.fub.agg2graph.management;

import de.fub.agg2graph.roadgen.Intersection;
import de.fub.agg2graph.roadgen.Road;
import de.fub.agg2graph.roadgen.RoadNetwork;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
	/**
	 * Get statistical information about the {@link RoadNetwork} object given.
	 * 
	 * @return
	 */
	public static Map<String, Double> getData(RoadNetwork roadNetwork) {
		Map<String, Double> stats = new HashMap<String, Double>();

		// extract visible items
		Set<Road> visibleRoads = new HashSet<Road>();
		Set<Intersection> visibleIntersections = new HashSet<Intersection>();
		double avgSum = 0, pseudoSum = 0, isolatedSum = 0, oneWaySum = 0;
		for (Road r : roadNetwork.roads) {
			if (r.isVisible()) {
				visibleRoads.add(r);
				avgSum += r.getLength();
				if (r.isIsolated()) {
					isolatedSum++;
				}
				if (r.isOneWay()) {
					oneWaySum++;
				}
			} else {
				System.out.println("invisible road " + r);
			}
		}
		for (Intersection i : roadNetwork.intersections) {
			if (i.isVisible()) {
				visibleIntersections.add(i);
				if (i.isPseudo()) {
					pseudoSum++;
				}
			} else {
				System.out.println("invisible intersection " + i);
			}
		}

		double numRoads = new Double(visibleRoads.size());
		double numIntersections = new Double(visibleIntersections.size());
		stats.put("total number of roads", numRoads);
		stats.put("total number of intersections", numIntersections);
		stats.put("roads/intersections", numRoads / numIntersections);

		// compute average road length
		stats.put("total road length", avgSum);
		stats.put("average road length", avgSum / numRoads);

		// how many intersections are only pseudo intersections?
		stats.put("number of real intersections", numIntersections - pseudoSum);
		stats.put("number of pseudo intersections", pseudoSum);
		stats.put("real/pseudo intersections", (numIntersections - pseudoSum)
				/ pseudoSum);

		// find isolated roads
		stats.put("number of isolated roads", isolatedSum);

		// count one way roads
		stats.put("number of one way roads", oneWaySum);
		stats.put("number of two way roads", numRoads - oneWaySum);
		stats.put("one way/two way roads", oneWaySum / (numRoads - oneWaySum));

		return stats;
	}
}
