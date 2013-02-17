package de.fub.agg2graph.agg;

import de.fub.agg2graph.agg.strategy.DefaultAggregationStrategy;

/**
 * A factory that returns the {@link IAggregationStrategy} currently set via the
 * getObject method.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class AggregationStrategyFactory {
	/*
	 * Default class to return. Can be overwritten by calls to setClass.
	 */
	private static Class<? extends IAggregationStrategy> factoryClass = DefaultAggregationStrategy.class;

	public static void setClass(Class<? extends IAggregationStrategy> clazz) {
		factoryClass = clazz;
	}

	public static IAggregationStrategy getObject() {
		if (factoryClass == null) {
			return null;
		}
		try {
			return factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
