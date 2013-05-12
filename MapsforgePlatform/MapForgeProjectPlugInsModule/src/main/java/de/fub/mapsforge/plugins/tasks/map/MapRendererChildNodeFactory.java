/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.map;

import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.models.Aggregator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class MapRendererChildNodeFactory extends ChildFactory<Aggregator> implements ChangeListener, ProcessPipeline.ProcessListener {

    private final MapRenderer mapRenderer;
    private ArrayList<Aggregator> aggregatorList = new ArrayList<Aggregator>();

    public MapRendererChildNodeFactory(MapRenderer mapRenderer) {
        assert mapRenderer != null;
        this.mapRenderer = mapRenderer;
        init();

    }

    private void init() {
        // add change listener to aggregator list of mapRenderer process
        this.mapRenderer.getAggregatorList().addChangeListener(
                WeakListeners.change(
                MapRendererChildNodeFactory.this,
                this.mapRenderer.getAggregatorList()));
        // add process listener to mapRenderer process
        this.mapRenderer.addProcessListener(
                WeakListeners.create(
                ProcessPipeline.ProcessListener.class,
                MapRendererChildNodeFactory.this,
                this.mapRenderer));
        // if the list contains aggregators than add them to the aggregatorList
        if (!this.mapRenderer.getAggregatorList().isEmpty()) {
            aggregatorList.addAll(mapRenderer.getAggregatorList());
        }
    }

    @Override
    protected boolean createKeys(List<Aggregator> toPopulate) {
        toPopulate.addAll(aggregatorList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Aggregator aggregator) {
        return new MapRendererSubNode(mapRenderer, aggregator);
    }

    @Override
    public void started() {
        aggregatorList.clear();
        refresh(true);
    }

    @Override
    public void changed(ProcessPipeline.ProcessEvent event) {
        // do nothing
    }

    @Override
    public void canceled() {
        refresh(true);
    }

    @Override
    public void finished() {
        aggregatorList.clear();
        aggregatorList.addAll(mapRenderer.getAggregatorList());
        refresh(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (this.mapRenderer.getProcessState() != ProcessState.RUNNING) {
            aggregatorList.addAll(mapRenderer.getAggregatorList());
            refresh(true);
        }
    }

    private static class MapRendererSubNode extends FilterNode {

        private final Aggregator aggregator;

        public MapRendererSubNode(MapRenderer mapRenderer, Aggregator aggregator) {
            super(aggregator.getDataObject().getNodeDelegate(), Children.LEAF, Lookups.fixed(mapRenderer.getProcessParentDetector(), aggregator, aggregator.getDataObject()));
            this.aggregator = aggregator;
        }

        @Override
        public Action getPreferredAction() {
            Action[] actions = getActions(false);
            return actions.length > 0 ? actions[0] : null;
        }

        @Override
        public Action[] getActions(boolean context) {
            List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions");
            return actionsForPath.toArray(new Action[actionsForPath.size()]);
        }
    }
}
