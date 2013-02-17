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
import de.fub.agg2graph.structs.GPSPoint;
import java.util.List;

public interface ITraceDistance {
	/**
	 * Compute the difference of a path to the aggregation. This measure only
	 * guarantees relative correctness when questioned repeatedly for different
	 * paths. A difference of 0 indicates equality while larger values indicate
	 * increasingly different paths.
	 * 
	 * @param aggPath
	 * @param tracePoints
	 * @param startIndex
	 * @param dmh
	 * @return Object[] { double bestValue, int bestValueLength }
	 */
	public Object[] getPathDifference(List<AggNode> aggPath,
			List<GPSPoint> tracePoints, int startIndex, IMergeHandler dmh);

	/**
	 * Return a list of settings to expose to the user via interfaces or the
	 * project files.
	 * 
	 * @return
	 */
	public List<ClassObjectEditor> getSettings();

}
