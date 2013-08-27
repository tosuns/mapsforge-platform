/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.plugins.tasks.eval;

import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.pipeline.processes.RoadNetworkProcess;
import de.fub.maps.project.models.Aggregator;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.plugins.tasks.eval.EvaluateMapAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_MapEvaluateAction")
@ActionReferences({
    @ActionReference(
            id =
            @ActionID(
            category = "Detector",
            id = "de.fub.maps.project.plugins.tasks.eval.EvaluateMapAction"),
            path = "Loaders/text/aggregationbuilder+xml/Actions", position = 255),
    @ActionReference(id =
            @ActionID(
            category = "Detector",
            id = "de.fub.maps.project.plugins.tasks.eval.EvaluateMapAction"),
            path = "Projects/Maps/Detector/Tasks/MapRenderer/Actions", position = 2000)
})
@Messages("CTL_MapEvaluateAction=Compare To OSM")
public final class EvaluateMapAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EvaluateMapAction.class.getName());
    private Aggregator aggregator;
    private RoadNetwork roadNetwork;
    private Lookup context;

    public EvaluateMapAction() {
        super(Bundle.CTL_MapEvaluateAction());
    }

    public EvaluateMapAction(Lookup actionContext) {
        this();
        this.context = actionContext;
        validate();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context != null) {
            if (aggregator != null) {
                setEnabled(false);
                RequestProcessor.getDefault().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (roadNetwork != null) {
                                OSMMapEvaluator evaluator = new OSMMapEvaluator(roadNetwork);
                                evaluator.evaluate();
                            }
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        } finally {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    setEnabled(true);
                                }
                            });
                        }
                    }
                });
            }
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
        if (context != null) {
            Collection<? extends Aggregator> allInstances = context.lookupResult(Aggregator.class).allInstances();
            if (allInstances.size() == 1) {
                setEnabled(true);
                aggregator = allInstances.iterator().next();
                if (aggregator != null) {
                    roadNetwork = getRoadNetwork();
                    if (roadNetwork != null) {
                        setEnabled(true);
                    }
                }
            }
        }
    }
}