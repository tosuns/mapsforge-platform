package de.fub.agg2graph.roadgen;

/**
 * A factory that returns the {@link IRoadTypeClassifier} currently set via the
 * getObject method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class RoadObjectMergerFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = DefaultRoadObjectMerger.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static IRoadObjectMerger getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (IRoadObjectMerger) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
