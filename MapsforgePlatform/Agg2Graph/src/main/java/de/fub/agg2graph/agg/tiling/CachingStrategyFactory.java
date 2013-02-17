package de.fub.agg2graph.agg.tiling;


/**
 * A factory that returns the {@link ICachingStrategy} currently set via the
 * getObject method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class CachingStrategyFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = DefaultCachingStrategy.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static ICachingStrategy getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (ICachingStrategy) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
