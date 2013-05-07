/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.plugins.tasks.eval.EvaluateMapAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_MapEvaluateAction")
//@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 255)
@ActionReference(path = "Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions", position = 2000)
@Messages("CTL_MapEvaluateAction=Compare To OSM")
public final class EvaluateMapAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 1L;
    private Aggregator aggregator;
    private RoadNetwork roadNetwork;

    public EvaluateMapAction() {
        super(Bundle.CTL_MapEvaluateAction());
    }

    public EvaluateMapAction(Lookup actionContext) {
        this();
        aggregator = actionContext.lookup(Aggregator.class);
        validate();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (aggregator != null) {
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    setEnabled(false);
                    try {
                        if (roadNetwork != null) {
                            OSMMapEvaluator evaluator = new OSMMapEvaluator(roadNetwork);
                            evaluator.evaluate();
                        }
                    } finally {
                        setEnabled(true);
                    }
                }
            });
        }
    }

    private RoadNetwork getRoadNetwork() {
        RoadNetwork roadNet = null;
        Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();
        for (AbstractAggregationProcess<?, ?> process : processes) {
            if (process instanceof RoadNetworkProcess) {
                roadNet = (RoadNetwork) process.getResult();
                break;
            }
        }
        return roadNet;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new EvaluateMapAction(actionContext);
    }

    private void validate() {
        setEnabled(false);
        if (aggregator != null) {
            roadNetwork = getRoadNetwork();
            if (roadNetwork != null) {
                setEnabled(true);
            }
        }
    }
}
