/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.wrapper;

import de.fub.agg2graph.agg.IAggregationStrategy;
import org.openide.util.lookup.ServiceProvider;

/**
 * This is only a wrapper class and its only usage is to annotate this class and
 * keep the agg2graph module clean of the netbeans api.
 *
 * @author Serdar
 */
@ServiceProvider(service = IAggregationStrategy.class)
public class GpxmergeAggregationStrategy extends de.fub.agg2graph.agg.strategy.GpxmergeAggregationStrategy {
}
