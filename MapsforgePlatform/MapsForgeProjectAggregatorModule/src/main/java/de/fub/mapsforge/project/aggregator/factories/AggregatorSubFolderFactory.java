/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories;

import de.fub.mapsforge.project.models.Aggregator;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class AggregatorSubFolderFactory extends ChildFactory<Node> implements PropertyChangeListener {

    @StaticResource
    private static final String DATASET_ICON_PATH = "de/fub/mapsforge/project/aggregator/datasetIcon.png";
    @StaticResource
    private static final String PROCESS_ICON_PATH = "de/fub/mapsforge/project/aggregator/processFolderIcon.png";
    private final Aggregator aggregator;

    public AggregatorSubFolderFactory(Aggregator aggregator) {
        this.aggregator = aggregator;
        aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorSubFolderFactory.this, aggregator));
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.add(new DatasetFolderNode(aggregator));
        toPopulate.add(new ProcessFolderNode(aggregator));
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return node;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Aggregator.PROP_NAME_DATAOBJECT.equals(evt.getPropertyName())) {
            refresh(true);
        }
    }

    private static class DatasetFolderNode extends AbstractNode {

        @NbBundle.Messages({"CLT_DatasetFolderNode_Name=Dataset", "CLT_DatasetFolderNode_Description=List all files that will be proceeded."})
        public DatasetFolderNode(Aggregator agg) {
            super(Children.create(new SourceChildFactory(agg), true));
            setDisplayName(Bundle.CLT_DatasetFolderNode_Name());
            setShortDescription(Bundle.CLT_DatasetFolderNode_Description());
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(DATASET_ICON_PATH);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
    }

    private static class ProcessFolderNode extends AbstractNode {

        @NbBundle.Messages({"CLT_ProcessFolderNode_Name=Process Pipeline", "CLT_ProcessFolderNode_Description=List of all current process which will be proceeded."})
        public ProcessFolderNode(Aggregator agg) {
            super(Children.create(new ProcessNodeFactory(agg), true));
            setDisplayName(Bundle.CLT_ProcessFolderNode_Name());
            setShortDescription(Bundle.CLT_ProcessFolderNode_Description());
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(PROCESS_ICON_PATH);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
    }
}
