package de.fub.agg2graph.roadgen;


/**
 * A factory that returns the {@link IAggFilter} currently set via the getObject
 * method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class AggFilterFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = DefaultAggFilter.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static IAggFilter getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (IAggFilter) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
