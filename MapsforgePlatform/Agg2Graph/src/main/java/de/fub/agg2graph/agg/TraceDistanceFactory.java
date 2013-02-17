package de.fub.agg2graph.agg;

import de.fub.agg2graph.agg.strategy.DefaultTraceDistance;

/**
 * A factory that returns the {@link ITraceDistance} currently set via the
 * getObject method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class TraceDistanceFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = DefaultTraceDistance.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static ITraceDistance getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (ITraceDistance) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
