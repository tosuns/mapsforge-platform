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

public class DefaultRoadTypeClassifier implements IRoadTypeClassifier {
	public double minWeightPrimary = 4;
	public double minWeightSecondary = 2;
	public double minWidthPrimary = 4;
	public double minWidthSecondary = 2;

	@Override
	public void classify(RoadNetwork network) {
		for (Road road : network.roads) {
			if (road.getType() != Road.RoadType.UNKNOWN) {
				continue;
			}
			/*
			 * classification algorithm with respect to weight and average
			 * distance saved in the AggConnections in the Road
			 */
			if (road.getWeight() >= minWeightPrimary
					&& road.getAvgDist() >= minWeightPrimary) {
				road.setType(Road.RoadType.PRIMARY);
			} else if (road.getWeight() >= minWeightPrimary
					&& road.getAvgDist() >= minWeightPrimary) {
				road.setType(Road.RoadType.SECONDARY);
			} else {
				road.setType(Road.RoadType.TERTIARY);
			}
		}
	}
}
