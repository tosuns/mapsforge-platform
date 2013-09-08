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

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.api.statistics.StatisticProvider;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.plugins.tasks.eval.MapComparationTopComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class SimpleMapsEvaluator {

    private final Collection<? extends Aggregator> aggregatorList;
    private final String evaluatorName;

    public SimpleMapsEvaluator(String evaluatorName, Collection<? extends Aggregator> instanceList) {
        this.aggregatorList = instanceList;
        this.evaluatorName = evaluatorName;
    }

    public void evaluate() {
        ArrayList<EvalutationItem> roadNetworkStatisticList = new ArrayList<EvalutationItem>(aggregatorList.size());
        try {
            for (Aggregator aggregator : aggregatorList) {
                RoadNetworkProcess roadNetworkProcess = getRoadNetworkProcess(aggregator);
                roadNetworkStatisticList.add(new EvalutationItem(aggregator, roadNetworkProcess));
            }
            handleEvaluation(roadNetworkStatisticList);
        } catch (IllegalStateException ex) {
            displayErrorMessage(ex);
        } catch (StatisticProvider.StatisticNotAvailableException ex) {
            displayErrorMessage(ex);
        }
    }

    private void displayErrorMessage(Exception ex) {
        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(ex.getMessage());
        DialogDisplayer.getDefault().notify(nd);
        Exceptions.printStackTrace(ex);
    }

    private RoadNetworkProcess getRoadNetworkProcess(Aggregator aggregator) throws StatisticProvider.StatisticNotAvailableException {
        RoadNetworkProcess roadNetworkProcess = null;
        Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();
        for (AbstractAggregationProcess<?, ?> process : processes) {
            if (process instanceof RoadNetworkProcess) {
                roadNetworkProcess = (RoadNetworkProcess) process;
                break;
            }
        }
        return roadNetworkProcess;
    }

    private void handleEvaluation(final List<EvalutationItem> roadNetworkStatisticList) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MapComparationTopComponent mapComparationTopComponent = new MapComparationTopComponent(roadNetworkStatisticList);
//                mapComparationTopComponent.setDisplayName(evaluatorName);
                mapComparationTopComponent.open();
                mapComparationTopComponent.requestActive();
            }
        });
    }
}
