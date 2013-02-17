/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories;

import de.fub.mapsforge.project.aggregator.factories.nodes.SourceNode;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import java.net.URISyntaxException;
import java.util.List;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class SourceChildFactory extends ChildFactory<Source> {

    private final Aggregator aggregator;

    public SourceChildFactory(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    protected boolean createKeys(List<Source> toPopulate) {
        toPopulate.addAll(this.aggregator.getSourceList());
        return true;
    }

    @Override
    protected Node createNodeForKey(Source source) {
        Node node = Node.EMPTY;
        try {
            node = new SourceNode(source, aggregator);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }
}
