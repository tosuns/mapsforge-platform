/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project;

import de.fub.maps.project.aggregator.factories.nodes.AggregatorFolderNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class MapsAggregatorHelper {

    private AggregatorFolderNode aggregatorFolderNode;

    private MapsAggregatorHelper() {
    }

    protected Node createAggregatorFolderNode(DataObject dataObject, MapsProject project) {
        if (aggregatorFolderNode == null) {
            aggregatorFolderNode = new AggregatorFolderNode(dataObject, project);
        }
        return aggregatorFolderNode;
    }

    public Node getAggregatorFolderNode() {
        return aggregatorFolderNode;
    }

    public static MapsAggregatorHelper getInstance() {
        return MapsAggregatorHelperHolder.INSTANCE;
    }

    private static class MapsAggregatorHelperHolder {

        private static final MapsAggregatorHelper INSTANCE = new MapsAggregatorHelper();
    }
}
