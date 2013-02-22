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

import de.fub.agg2graph.input.Globals;

/**
 * Minimal class for profiling runtimes of portions of code.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class MiniProfiler {
	public static String print() {
		return print(null);
	}

	public static String print(String name) {
		long now = System.currentTimeMillis();
		// init?
		Object startObject = Globals.get("profiling.start");
		long lastTime = now;
		long startTime = now;
		if (startObject == null) {
			Globals.put("profiling.start", now);
			Globals.put("profiling.last", now);
		} else {
			startTime = (Long) startObject;
			lastTime = (Long) Globals.get("profiling.last");
			Globals.put("profiling.last", System.currentTimeMillis());
		}
		return String.format(
				"Profiling: %s\nSince start: %.3fs\nSince last:  %.3fs", name,
				(now - (double) startTime) / 1000,
				(now - (double) lastTime) / 1000);
	}
}
