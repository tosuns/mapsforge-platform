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
package de.fub.maps.project.aggregator.factories;

import de.fub.maps.project.aggregator.factories.nodes.ProcessFilterNode;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class ProcessNodeFactory extends ChildFactory<AbstractAggregationProcess<?, ?>> {

    private final Aggregator aggregator;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public ProcessNodeFactory(Aggregator aggregator) {
        assert aggregator != null;
        this.aggregator = aggregator;
        modelSynchronizerClient = aggregator.create(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }

    @Override
    protected boolean createKeys(List<AbstractAggregationProcess<?, ?>> toPopulate) {
        toPopulate.addAll(aggregator.getPipeline().getProcesses());
        return true;
    }

    @Override
    protected Node createNodeForKey(AbstractAggregationProcess<?, ?> process) {
        return new ProcessFilterNode(process.getNodeDelegate());
    }
}
