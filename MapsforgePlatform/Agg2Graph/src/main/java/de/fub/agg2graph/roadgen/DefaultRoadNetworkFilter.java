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

/**
 * Filtering methods for the {@link RoadNetwork}. Used to strip unreliable
 * roads.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class DefaultRoadNetworkFilter implements IRoadNetworkFilter {
	public boolean removeBorderRoads = true;
	public double minBorderRoadLength = 150; // meters
	public boolean removeIsolatedRoads = true;
	public double minIsolatedRoadLength = 500; // meters

	@Override
	public void filter(RoadNetwork roadNetwork) {
		hideUnreliableRoads(roadNetwork);
	}

	private void hideUnreliableRoads(RoadNetwork roadNetwork) {
		for (Road r : roadNetwork.roads) {
			r.setVisible(true);
			if (!removeBorderRoads && !removeIsolatedRoads) {
				continue;
			}
			double length = r.getLength();

			// remove isolated roads below length limit if requested
			if (removeBorderRoads && r.isBorderRoad()
					&& length < minBorderRoadLength) {
				hideRoad(r);
			}

			// remove isolated roads below length limit if requested
			if (removeIsolatedRoads && r.isIsolated()
					&& length < minIsolatedRoadLength) {
				hideRoad(r);
			}
		}
	}

	private void hideRoad(Road r) {
		r.setVisible(false);
		// check if we have to hide intersections as well
		if (r.getFrom().getVisibleEdgeCount() == 0) {
			r.getFrom().setVisible(false);
		}
		if (r.getTo().getVisibleEdgeCount() == 0) {
			r.getTo().setVisible(false);
		}
	}
}
