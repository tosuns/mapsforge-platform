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
