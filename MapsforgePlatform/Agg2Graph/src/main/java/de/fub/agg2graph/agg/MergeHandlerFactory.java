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

import de.fub.agg2graph.agg.strategy.DefaultMergeHandler;

/**
 * A factory that returns the {@link IMergeHandler} currently set via the
 * getObject method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class MergeHandlerFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = DefaultMergeHandler.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static IMergeHandler getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (IMergeHandler) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
