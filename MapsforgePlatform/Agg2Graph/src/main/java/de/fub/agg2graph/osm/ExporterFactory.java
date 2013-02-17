package de.fub.agg2graph.osm;


/**
 * A factory that returns the {@link IExporter} currently set via the getObject
 * method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class ExporterFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<?> factoryClass = OsmExporter.class;

	public static void setClass(Class<?> clazz) {
		factoryClass = clazz;
	}

	public static IExporter getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return (IExporter) factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
