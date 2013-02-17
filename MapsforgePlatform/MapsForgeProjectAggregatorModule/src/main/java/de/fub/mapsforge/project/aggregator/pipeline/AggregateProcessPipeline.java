/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.mapsforge.project.models.Aggregator;

/**
 *
 * @author Serdar
 */
public class AggregateProcessPipeline extends ProcessPipeline<AbstractAggregationProcess<?, ?>> {

    private final Aggregator aggregator;

    public AggregateProcessPipeline(Aggregator aggregator1) {
        super();
        assert aggregator1 != null;
        this.aggregator = aggregator1;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }
}
