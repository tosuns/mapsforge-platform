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
