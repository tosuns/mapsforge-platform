package de.fub.agg2graph.roadgen;

/**
 * A factory that returns the {@link IIntersectionParser} currently set via the
 * getObject method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class IntersectionParserFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = DefaultIntersectionParser.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static IIntersectionParser getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (IIntersectionParser) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
