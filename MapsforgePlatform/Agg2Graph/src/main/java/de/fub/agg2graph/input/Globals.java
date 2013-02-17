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
package de.fub.agg2graph.input;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for storing global values into a map data structure and
 * reading the values from there.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class Globals {
	private static Globals instance = new Globals();

	Map<String, Object> objects = new HashMap<String, Object>();

	private Globals() {

	}

	public static Globals getInstance() {
		return instance;
	}

	public Map<String, Object> getObjects() {
		return objects;
	}

	public static Object get(String key) {
		return Globals.getInstance().getObjects().get(key);
	}

	public static Object put(String key, Object object) {
		return Globals.getInstance().getObjects().put(key, object);
	}
}
