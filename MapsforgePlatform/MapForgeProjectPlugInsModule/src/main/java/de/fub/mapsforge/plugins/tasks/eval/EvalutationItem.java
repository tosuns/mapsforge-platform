/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.mapsforge.project.models.Aggregator;

/**
 *
 * @author Serdar
 */
public class EvalutationItem {

    private final Aggregator aggregator;
    private final RoadNetworkProcess roadNetworkProcess;

    public EvalutationItem(Aggregator aggregator, RoadNetworkProcess roadNetworkProcess) {
        this.aggregator = aggregator;
        this.roadNetworkProcess = roadNetworkProcess;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    public RoadNetworkProcess getRoadNetworkProcess() {
        return roadNetworkProcess;
    }
}
