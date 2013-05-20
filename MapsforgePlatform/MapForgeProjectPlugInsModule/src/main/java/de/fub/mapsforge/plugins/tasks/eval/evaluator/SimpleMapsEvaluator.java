/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval.evaluator;

import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.plugins.tasks.eval.MapComparationTopComponent;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.mapsforge.project.models.Aggregator;
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
                RoadNetworkProcess roadNetworkStatistic = getRoadNetworkStatistic(aggregator);
                roadNetworkStatisticList.add(new EvalutationItem(aggregator, roadNetworkStatistic));
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

    private RoadNetworkProcess getRoadNetworkStatistic(Aggregator aggregator) throws StatisticProvider.StatisticNotAvailableException {
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
                mapComparationTopComponent.setDisplayName(evaluatorName);
                mapComparationTopComponent.open();
                mapComparationTopComponent.requestActive();
            }
        });
    }
}