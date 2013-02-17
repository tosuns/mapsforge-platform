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
