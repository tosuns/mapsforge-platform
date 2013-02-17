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
