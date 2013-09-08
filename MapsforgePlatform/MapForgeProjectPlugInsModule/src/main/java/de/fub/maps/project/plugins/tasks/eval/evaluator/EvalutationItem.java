/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.plugins.tasks.eval.evaluator;

import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.models.Aggregator;

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
