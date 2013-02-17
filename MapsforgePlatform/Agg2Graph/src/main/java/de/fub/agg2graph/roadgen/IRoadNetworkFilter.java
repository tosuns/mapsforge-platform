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
 * Filtering methods for the {@link RoadNetwork}.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public interface IRoadNetworkFilter {
	/**
	 * Hide {@link Road}s that have features indicating they are not to be
	 * processed any further (export).
	 * 
	 * @param agg
	 */
	public void filter(RoadNetwork roadNetwork);
}
