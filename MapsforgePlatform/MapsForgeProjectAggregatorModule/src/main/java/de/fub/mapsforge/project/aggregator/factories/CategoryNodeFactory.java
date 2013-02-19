/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class CategoryNodeFactory extends ChildFactory<String> {

    public CategoryNodeFactory() {
    }

    @NbBundle.Messages("CLT_Name=Processes")
    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.add(Bundle.CLT_Name());
        return true;
    }

    @Override
    protected Node createNodeForKey(String process) {
        return new ProcessCategory(process);
    }

    private static class ProcessCategory extends AbstractNode {

        public ProcessCategory(String name) {
            super(Children.create(new PaletteProcessNodeFactory(), true));
            setDisplayName(name);
        }
    }

    private static class PaletteProcessNodeFactory extends ChildFactory<AbstractAggregationProcess> {

        public PaletteProcessNodeFactory() {
        }

        @Override
        protected boolean createKeys(List<AbstractAggregationProcess> toPopulate) {
            Lookup.Result<AbstractAggregationProcess> result = Lookup.getDefault().lookupResult(AbstractAggregationProcess.class);
            toPopulate.addAll(result.allInstances());
            return true;
        }

        @Override
        protected Node createNodeForKey(AbstractAggregationProcess process) {
            return process.getNodeDelegate();
        }
    }
}
