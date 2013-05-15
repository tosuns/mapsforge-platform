/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories;

import de.fub.mapsforge.project.aggregator.factories.nodes.SourceNode;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.models.AggregatorSource;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class SourceChildFactory extends ChildFactory<Node> {

    private final Aggregator aggregator;
    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public SourceChildFactory(Aggregator aggregator) {
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
    protected boolean createKeys(List<Node> toPopulate) {
        for (Source source : this.aggregator.getSourceList()) {
            Node node = getNode(new AggregatorSource(aggregator, source));
            toPopulate.add(node);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Node source) {
        Node node = source;
        return node;
    }

    private static Node getNode(AggregatorSource aggregatorSource) {
        Node node = createErrorNode(aggregatorSource);
        DataObject dataObject = getDataObject(aggregatorSource);
        if (dataObject != null) {
            node = new SourceNode(dataObject.getNodeDelegate(), aggregatorSource);
        }
        return node;
    }

    private static DataObject getDataObject(AggregatorSource aggregatorSource) {
        DataObject dataObject = null;
        Source source = aggregatorSource.getSource();
        File file = new File(source.getUrl());
        FileObject fileObject = FileUtil.toFileObject(file);
        if (fileObject != null) {
            try {
                dataObject = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return dataObject;
    }

    private static Node createErrorNode(AggregatorSource aggregatorSource) {
        return new ErrorNode(aggregatorSource);
    }

    private static class ErrorNode extends AbstractNode {

        public ErrorNode(AggregatorSource aggregatorSource) {
            super(FilterNode.Children.LEAF);
            setDisplayName(MessageFormat.format(
                    "<html><font color='ff0000'>&lt;{0}&gt;</font></html>",
                    aggregatorSource.getSource().getUrl()));
        }

        @Override
        public Image getIcon(int type) {
            Image image = IconRegister.findRegisteredIcon("errorHintIcon.png");
            if (image == null) {
                image = super.getIcon(type);
            }
            return image;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
    }
}
