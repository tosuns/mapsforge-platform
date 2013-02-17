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

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import java.util.Set;
import java.util.logging.Logger;

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
