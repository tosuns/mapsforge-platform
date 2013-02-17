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
