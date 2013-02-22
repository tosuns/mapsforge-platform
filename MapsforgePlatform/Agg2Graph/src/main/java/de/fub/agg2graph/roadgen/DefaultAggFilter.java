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

import java.util.Set;
import java.util.logging.Logger;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;

/**
 * Filtering methods for the aggregated data. Used to strip unreliable
 * connections.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class DefaultAggFilter implements IAggFilter {
	private static final Logger logger = Logger
			.getLogger("agg2graph.roadgen.aggfilter");
	public double minEdgeWeight = 2;

	@Override
	public void filter(AggContainer agg) {
		hideLowWeightConnections(agg);
	}

	/**
	 * Remove all {@link AggConnection}s that have a weight below the minimum
	 * weight given to this class.
	 * 
	 * @param agg
	 */
	private void hideLowWeightConnections(AggContainer agg) {
		// get all loaded and visible edges
		Set<AggConnection> connections = agg.getCachingStrategy()
				.getLoadedConnections();
		// loop edges, remove
		for (AggConnection conn : connections) {
			conn.setVisible(conn.getWeight() >= minEdgeWeight);
			if (!conn.isVisible()) {
				logger.info("hiding connection: " + conn);
				AggNode from = conn.getFrom();
				from.refreshWeight();
				if (from.getVisibleIn().size() < 1
						&& from.getVisibleOut().size() < 1) {
					logger.info("hiding node: " + from);
					from.setVisible(false);
				} else {
					from.setVisible(true);
				}
				AggNode to = conn.getTo();
				to.refreshWeight();
				if (to.getVisibleIn().size() < 1
						&& to.getVisibleOut().size() < 1) {
					logger.info("hiding node: " + to);
					to.setVisible(false);
				} else {
					to.setVisible(true);
				}
			}
		}
	}
}
