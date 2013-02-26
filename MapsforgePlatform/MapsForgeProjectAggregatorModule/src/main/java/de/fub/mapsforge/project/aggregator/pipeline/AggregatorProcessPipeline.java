/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapforgeproject.api.process.ProcessPipeline;

/**
 *
 * @author Serdar
 */
public class AggregatorProcessPipeline extends ProcessPipeline<AbstractAggregationProcess<?, ?>> {

    private final Aggregator aggregator;

    public AggregatorProcessPipeline(Aggregator aggregator1) {
        super();
        assert aggregator1 != null;
        this.aggregator = aggregator1;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }
}
