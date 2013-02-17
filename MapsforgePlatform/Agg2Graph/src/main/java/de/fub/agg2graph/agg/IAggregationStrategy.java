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
package de.fub.agg2graph.agg;

import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSSegment;
import java.util.List;

/**
 * Strategy for aggregating new {@link GPSSegment}s to an {@link AggContainer}.
 * 
 * @author Johannes Mitlmeier
 */
public interface IAggregationStrategy {
	public void aggregate(GPSSegment segment);

	public void setAggContainer(AggContainer aggContainer);

	public AggContainer getAggContainer();

	public AggConnection mergeConnections(AggConnection changedConn,
			AggConnection oldConn);

	public AggConnection combineConnections(AggConnection firstConn,
			AggConnection secondConn);

	public void clear();

	public ITraceDistance getTraceDist();

	public List<ClassObjectEditor> getSettings();
}
